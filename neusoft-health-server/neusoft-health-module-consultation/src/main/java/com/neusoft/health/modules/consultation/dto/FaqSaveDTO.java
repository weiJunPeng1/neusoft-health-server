package com.neusoft.health.modules.consultation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FaqSaveDTO {

    private Long id;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @NotBlank(message = "问题内容不能为空")
    @Size(max = 500, message = "问题内容最长500字")
    private String question;

    private String presetAnswer;

    private Integer sortOrder;

    private Integer status;
}
