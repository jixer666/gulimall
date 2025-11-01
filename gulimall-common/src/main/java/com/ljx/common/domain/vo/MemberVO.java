package com.ljx.common.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MemberVO implements Serializable {

    private Long id;

    private String username;

    private String nickname;

    private String mobile;

    private String email;


}
