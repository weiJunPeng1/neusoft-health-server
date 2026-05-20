package com.neusoft.health.common.enums;

/**
 * 消息角色枚举
 * <p>
 * 定义咨询对话中的消息发送者角色：用户、AI助手、系统。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public enum MessageRoleEnum {

    /**
     * 用户
     */
    USER("user", "用户"),
    /**
     * AI助手
     */
    ASSISTANT("assistant", "AI助手"),
    /**
     * 系统
     */
    SYSTEM("system", "系统");

    /**
     * 编码
     */
    private final String code;
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
    MessageRoleEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取编码
     *
     * @return 编码
     */
    public String getCode() {
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
