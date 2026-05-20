package com.neusoft.health.common.enums;

/**
 * 性别枚举
 * <p>
 * 定义用户性别：未知、男、女。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public enum GenderEnum {

    /**
     * 未知性别
     */
    UNKNOWN(0, "未知"),
    /**
     * 男
     */
    MALE(1, "男"),
    /**
     * 女
     */
    FEMALE(2, "女");

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
    GenderEnum(int code, String desc) {
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

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 性别枚举，找不到时返回UNKNOWN
     */
    public static GenderEnum of(int code) {
        for (GenderEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return UNKNOWN;
    }
}
