package com.neusoft.health.common.enums;

/**
 * 支付状态枚举
 * <p>
 * 定义支付订单的状态：待支付、已支付、已取消、退款中、已到账、到账失败。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
public enum PaymentStatusEnum {

    /**
     * 待支付，订单已创建但尚未完成支付
     */
    PENDING(0, "待支付"),
    /**
     * 已支付，用户已完成支付
     */
    PAID(1, "已支付"),
    /**
     * 已取消，订单已取消
     */
    CANCELLED(2, "已取消"),
    /**
     * 退款中，退款申请已提交
     */
    REFUNDING(3, "退款中"),
    /**
     * 已到账，退款已到账
     */
    RECEIVED(4, "已到账"),
    /**
     * 到账失败，退款到账失败
     */
    FAILED(5, "到账失败");

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
    PaymentStatusEnum(int code, String desc) {
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
        for (PaymentStatusEnum status : values()) {
            if (status.getCode() == code) {
                return status.getDesc();
            }
        }
        return "未知状态";
    }
}
