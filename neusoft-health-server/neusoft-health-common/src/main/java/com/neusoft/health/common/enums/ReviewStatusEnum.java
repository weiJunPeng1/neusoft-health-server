package com.neusoft.health.common.enums;

/**
 * 审核状态枚举
 * <p>
 * 定义内容审核的状态：待审核、通过、违规。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public enum ReviewStatusEnum {

    /**
     * 待审核
     */
    PENDING(0, "待审核"),
    /**
     * 审核通过
     */
    APPROVED(1, "通过"),
    /**
     * 违规
     */
    VIOLATION(2, "违规");

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
    ReviewStatusEnum(int code, String desc) {
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
