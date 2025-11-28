package com.ljx.gulimall.coupon.model.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ljx.gulimall.coupon.model.entity.SeckillSkuRelationEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SeckillSessionDTO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 场次名称
     */
    private String name;
    /**
     * 每日开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT+8")
    private Date startTime;
    /**
     * 每日结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT+8")
    private Date endTime;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT+8")
    private Date createTime;


    private List<SeckillSkuRelationEntity> seckillSkuRelationList;

}
