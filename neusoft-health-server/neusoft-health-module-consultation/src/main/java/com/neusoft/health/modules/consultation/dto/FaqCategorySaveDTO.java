package com.neusoft.health.modules.consultation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FaqCategorySaveDTO {

    private Long id;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 64, message = "分类名称最长64个字符")
    private String name;

    private String icon;

    private Integer sortOrder;

    private Integer status;
}
