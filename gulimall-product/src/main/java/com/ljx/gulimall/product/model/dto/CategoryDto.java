package com.ljx.gulimall.product.model.dto;

import com.ljx.gulimall.product.model.entity.CategoryEntity;
import lombok.Data;

import java.util.List;

/**
 * @Author: LiJunXi
 * @Description:
 * @Date: 2025-07-17  23:17
 */
@Data
public class CategoryDto extends CategoryEntity {

    private List<CategoryDto> children;

}
