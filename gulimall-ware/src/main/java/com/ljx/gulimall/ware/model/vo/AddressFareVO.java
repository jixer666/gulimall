package com.ljx.gulimall.ware.model.vo;

import com.ljx.gulimall.ware.model.entity.MemberReceiveAddressEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddressFareVO {

    private BigDecimal fare;

    private MemberReceiveAddressEntity address;

}
