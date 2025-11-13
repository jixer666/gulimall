package com.ljx.gulimall.order.model.vo;

import com.ljx.gulimall.order.model.entity.MemberReceiveAddressEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddressFareVO {

    private BigDecimal fare;

    private MemberReceiveAddressEntity address;

}
