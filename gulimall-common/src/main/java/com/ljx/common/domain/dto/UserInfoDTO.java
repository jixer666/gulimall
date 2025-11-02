package com.ljx.common.domain.dto;

import lombok.Data;

@Data
public class UserInfoDTO {

    private Long userId;

    private String userKey;

    private Boolean isFirstSave = false;

}
