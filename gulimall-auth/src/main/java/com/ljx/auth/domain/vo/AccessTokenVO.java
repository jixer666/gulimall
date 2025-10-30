package com.ljx.auth.domain.vo;

import lombok.Data;

@Data
public class AccessTokenVO {

    private String access_token;

    private String token_type;

    private String scope;

}
