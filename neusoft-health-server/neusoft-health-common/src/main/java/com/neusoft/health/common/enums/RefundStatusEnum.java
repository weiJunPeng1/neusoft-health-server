package com.neusoft.health.common.enums;

/**
 * 退款状态枚举
 * <p>
 * 定义退款申请的状态：待审核、已通过、已拒绝、已到账、到账失败。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public enum RefundStatusEnum {

    /**
     * 待审核，退款申请已提交等待审核
     */
    PENDING(0, "待审核"),
    /**
     * 已通过，退款申请已审核通过
     */
    APPROVED(1, "已通过"),
    /**
     * 已拒绝，退款申请已审核拒绝
     */
    REJECTED(2, "已拒绝"),
    /**
     * 已到账，退款已到账
     */
    RECEIVED(3, "已到账"),
    /**
     * 到账失败，退款到账失败
     */
    FAILED(4, "到账失败");

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
    RefundStatusEnum(int code, String desc) {
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
     * 根据编码获取描述
     *
     * @param code 编码
     * @return 描述，如果编码不存在则返回"未知状态"
     */
    public static String getDescByCode(int code) {
        for (RefundStatusEnum status : values()) {
            if (status.getCode() == code) {
                return status.getDesc();
            }
        }
        return "未知状态";
    }
}
