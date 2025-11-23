package com.ljx.gulimall.ware.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljx.common.exception.RRException;
import com.ljx.common.utils.AssertUtil;
import com.ljx.common.utils.R;
import com.ljx.gulimall.ware.fegin.OrderServiceFeign;
import com.ljx.gulimall.ware.model.dto.OrderStockLockDTO;
import com.ljx.gulimall.ware.model.dto.StockLockDTO;
import com.ljx.gulimall.ware.model.entity.OrderEntity;
import com.ljx.gulimall.ware.model.entity.WareOrderTaskDetailEntity;
import com.ljx.gulimall.ware.model.entity.WareOrderTaskEntity;
import com.ljx.gulimall.ware.model.enums.OrderStatusEnum;
import com.ljx.gulimall.ware.model.enums.WareOrderTaskDetailLocakStatusEnum;
import com.ljx.gulimall.ware.model.vo.OrderStockLockVO;
import com.ljx.gulimall.ware.model.vo.SkuHasStockVo;
import com.ljx.gulimall.ware.service.WareOrderTaskDetailService;
import com.ljx.gulimall.ware.service.WareOrderTaskService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljx.common.utils.PageUtils;
import com.ljx.common.utils.Query;

import com.ljx.gulimall.ware.dao.WareSkuDao;
import com.ljx.gulimall.ware.model.entity.WareSkuEntity;
import com.ljx.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private WareOrderTaskService wareOrderTaskService;

    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderServiceFeign orderServiceFeign;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public List<SkuHasStockVo> getHashStock(List<Long> skuIds) {
        if (CollUtil.isEmpty(skuIds)) {
            return new ArrayList<>();
        }
        return wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>()
                        .in("sku_id", skuIds))
                .stream().map(item -> {
                    SkuHasStockVo resp = new SkuHasStockVo();
                    resp.setSkuId(item.getSkuId());
                    resp.setHashStock(item.getStock() - item.getStockLocked() > 0);
                    return resp;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean lockOrderStock(OrderStockLockDTO orderStockLockDTO) {
        StockLockDTO stockLockDTO = new StockLockDTO();
        WareOrderTaskEntity wareOrderTaskEntity = saveWareOrderTask(orderStockLockDTO.getOrderSn());
        stockLockDTO.setOrderStockLockId(wareOrderTaskEntity.getId());

        for (OrderStockLockVO orderStockLockVO : orderStockLockDTO.getOrderStockLockVOS()) {
            List<Long> wareIds = wareSkuDao.selectWareListBySkuId(orderStockLockVO.getSkuId());
            if (CollUtil.isEmpty(wareIds)) {
                throw new RRException("锁定订单失败");
            }

            Boolean itemResult = false;
            for (Long wareId : wareIds) {
                int update = wareSkuDao.lockStock(orderStockLockVO.getSkuId(), orderStockLockVO.getCount(), wareId);
                if (update > 0) {
                    itemResult = true;
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = saveWareOrderTaskDetail(wareOrderTaskEntity.getId(), wareId, orderStockLockVO);
                    stockLockDTO.getWareOrderTaskDetailIdList().add(wareOrderTaskDetailEntity.getId());
                    break;
                }
            }
            if (!itemResult) {
                return false;
            }
        }

        rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", stockLockDTO);

        return true;
    }

    private WareOrderTaskDetailEntity saveWareOrderTaskDetail(Long taskId, Long wareId, OrderStockLockVO orderStockLockVO) {
        WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
        wareOrderTaskDetailEntity.setTaskId(taskId);
        wareOrderTaskDetailEntity.setSkuId(orderStockLockVO.getSkuId());
        wareOrderTaskDetailEntity.setSkuNum(orderStockLockVO.getCount());
        wareOrderTaskDetailEntity.setLockStatus(WareOrderTaskDetailLocakStatusEnum.WAIT.getStatus());
        wareOrderTaskDetailEntity.setWareId(wareId);

        wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);

        return wareOrderTaskDetailEntity;
    }

    private WareOrderTaskEntity saveWareOrderTask(String orderSn) {
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(orderSn);

        wareOrderTaskService.save(wareOrderTaskEntity);

        return wareOrderTaskEntity;
    }

    @Override
    public Boolean unLockStock(StockLockDTO stockLockDTO) {
        if (Objects.isNull(stockLockDTO.getOrderStockLockId())) {
            // 参数错误，应单独放一个消息队列人工处理
            return true;
        }
        if (CollUtil.isEmpty(stockLockDTO.getWareOrderTaskDetailIdList())) {
            // 参数错误，应单独放一个消息队列人工处理
            return true;
        }

        WareOrderTaskEntity wareOrderTask = wareOrderTaskService.getById(stockLockDTO.getOrderStockLockId());
        if (Objects.isNull(wareOrderTask)) {
            // 订单锁定不存在，应单独放一个消息队列人工处理
            return true;
        }

        R<OrderEntity> orderResult = orderServiceFeign.getByOrderSn(wareOrderTask.getOrderSn());
        if (orderResult.getCode() != 0) {
            // 查询订单出错，应单独放一个消息队列人工处理
            return true;
        }

        Boolean flag = true;
        OrderEntity orderEntity = orderResult.getDataObj(OrderEntity.class);
        for (WareOrderTaskDetailEntity wareOrderTaskDetailEntity : wareOrderTaskDetailService.listByIds(stockLockDTO.getWareOrderTaskDetailIdList())) {
            if (Objects.isNull(wareOrderTaskDetailEntity)) {
                // 订单详情不存在
                flag = false;
                continue;
            }

            if (wareOrderTaskDetailEntity.isOver()) {
                // 已解锁，不做处理
                continue;
            }

            // 解锁库存 和 更新订单详情锁定状态为已解锁
            if (wareOrderTaskDetailEntity.isWait()) {
                unlockStock(wareOrderTaskDetailEntity.getSkuId(), wareOrderTaskDetailEntity.getSkuNum(), wareOrderTaskDetailEntity.getWareId());

                wareOrderTaskDetailEntity.setLockStatus(WareOrderTaskDetailLocakStatusEnum.OVER.getStatus());
                wareOrderTaskDetailService.updateById(wareOrderTaskDetailEntity);
            }
        }

        return flag;
    }

    @Override
    public Boolean unLockStock(String orderSn) {
        if (StrUtil.isEmpty(orderSn)) {
            // 参数错误，应单独放一个消息队列人工处理
            return true;
        }

        R<OrderEntity> orderResult = orderServiceFeign.getByOrderSn(orderSn);
        if (orderResult.getCode() != 0) {
            // 订单不存在，应单独放一个消息队列人工处理
            return true;
        }

        WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskService.getByOrderSn(orderSn);
        for (WareOrderTaskDetailEntity wareOrderTaskDetail : wareOrderTaskDetailService.list(new LambdaQueryWrapper<WareOrderTaskDetailEntity>().eq(WareOrderTaskDetailEntity::getTaskId, wareOrderTaskEntity.getId()))) {
            if (wareOrderTaskDetail.isOver()) {
                continue;
            }
            // 解锁库存
            unlockStock(wareOrderTaskDetail.getSkuId(), wareOrderTaskDetail.getSkuNum(), wareOrderTaskDetail.getWareId());
            // 更新订单库存状态
            wareOrderTaskDetail.setLockStatus(WareOrderTaskDetailLocakStatusEnum.OVER.getStatus());
            wareOrderTaskDetailService.updateById(wareOrderTaskDetail);
        }

        return true;
    }

    private void unlockStock(Long id, Integer num, Long wareId) {
        AssertUtil.isNotEmpty(id, "解锁ID不能为空");
        AssertUtil.isNotEmpty(num, "解锁库存数量不能为空");
        AssertUtil.isNotEmpty(wareId, "仓库有ID不能为空");

        wareSkuDao.unlockStock(id, num, wareId);
    }
}