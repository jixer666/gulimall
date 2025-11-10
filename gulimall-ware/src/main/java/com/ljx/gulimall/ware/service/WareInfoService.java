package com.ljx.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljx.common.utils.PageUtils;
import com.ljx.gulimall.ware.model.entity.WareInfoEntity;
import com.ljx.gulimall.ware.model.vo.AddressFareVO;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 22:01:01
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    AddressFareVO getAddIdFare(Long addrId);
}

