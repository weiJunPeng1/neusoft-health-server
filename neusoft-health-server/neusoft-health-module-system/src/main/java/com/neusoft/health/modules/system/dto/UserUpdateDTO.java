package com.neusoft.health.modules.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户更新DTO
 * <p>
 * 用于更新用户基本信息
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
public class UserUpdateDTO {

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 20, message = "昵称长度为2-20个字符")
    private String nickname;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 性别：0=未知，1=男，2=女
     */
    private Integer gender;

    /**
     * 年龄
     */
    private Integer age;
}
