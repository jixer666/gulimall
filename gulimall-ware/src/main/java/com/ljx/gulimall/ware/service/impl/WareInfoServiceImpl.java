package com.ljx.gulimall.ware.service.impl;

import com.ljx.common.utils.AssertUtil;
import com.ljx.common.utils.R;
import com.ljx.gulimall.ware.fegin.MemberServiceFeign;
import com.ljx.gulimall.ware.model.entity.MemberReceiveAddressEntity;
import com.ljx.gulimall.ware.model.vo.AddressFareVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljx.common.utils.PageUtils;
import com.ljx.common.utils.Query;

import com.ljx.gulimall.ware.dao.WareInfoDao;
import com.ljx.gulimall.ware.model.entity.WareInfoEntity;
import com.ljx.gulimall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    private MemberServiceFeign memberServiceFeign;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                new QueryWrapper<WareInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public AddressFareVO getAddIdFare(Long addrId) {
        AssertUtil.isNotEmpty(addrId, "地址不能为空");

        AddressFareVO addressFareVO = new AddressFareVO();
        R<MemberReceiveAddressEntity> addInfoResult = memberServiceFeign.getAddInfo(addrId);
        if (addInfoResult.getCode() == 0) {
            MemberReceiveAddressEntity memberReceiveAddress = addInfoResult.getDataObj(MemberReceiveAddressEntity.class);

            // 此处运费需要调用第三方接口。所以这里模拟随机取数：手机号前第一位
            String phone = memberReceiveAddress.getPhone();

            addressFareVO.setAddress(memberReceiveAddress);
            addressFareVO.setFare(new BigDecimal(phone.substring(0, 1)));
        } else {
            addressFareVO.setAddress(new MemberReceiveAddressEntity());
            addressFareVO.setFare(new BigDecimal(0));
        }

        return addressFareVO;
    }
}