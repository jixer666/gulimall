package com.ljx.gulimall.product.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: LiJunXi
 * @Description:
 * @Date: 2025-07-26  16:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category2Vo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String catalog1Id;
    private List<Category3Vo> catalog3List;
    private String id;
    private String name;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Category3Vo implements Serializable {
        private static final long serialVersionUID = 1L;
        private String catalog2Id;
        private String id;
        private String name;
    }
}
