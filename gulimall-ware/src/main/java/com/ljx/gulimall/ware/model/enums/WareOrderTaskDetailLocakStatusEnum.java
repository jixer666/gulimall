package com.ljx.gulimall.ware.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WareOrderTaskDetailLocakStatusEnum {

    WAIT(1),
    OVER(2);

    private Integer status;
}
