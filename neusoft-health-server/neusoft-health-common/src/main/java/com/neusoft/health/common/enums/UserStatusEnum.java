package com.neusoft.health.common.enums;

/**
 * 用户状态枚举
 * <p>
 * 定义用户账号的状态：禁用、正常。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public enum UserStatusEnum {

    /**
     * 禁用状态，用户无法登录
     */
    DISABLED(0, "禁用"),
    /**
     * 正常状态，用户可以正常使用
     */
    NORMAL(1, "正常");

    /**
     * 编码
     */
    private final int code;
    /**
     * 描述
     */
    private final String desc;

    /**
     * 构造方法
     *
     * @param code 编码
     * @param desc 描述
     */
    UserStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取编码
     *
     * @return 编码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取描述
     *
     * @return 描述
     */
    public String getDesc() {
        return desc;
    }
}
