package com.neusoft.health.common.result;

import lombok.Getter;

/**
 * API响应结果码枚举
 * <p>
 * 定义系统中所有API响应的状态码和消息。
 * 0表示成功，其他非0值表示不同类型的失败。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Getter
public enum ResultCode {

    /**
     * 操作成功
     */
    SUCCESS(0, "操作成功"),

    /**
     * 请求参数错误
     */
    BAD_REQUEST(400, "请求参数错误"),
    /**
     * 未登录或登录已过期
     */
    UNAUTHORIZED(401, "未登录或登录已过期"),
    /**
     * 没有访问权限
     */
    FORBIDDEN(403, "没有访问权限"),
    /**
     * 请求的资源不存在
     */
    NOT_FOUND(404, "请求的资源不存在"),
    /**
     * 不支持的请求方法
     */
    METHOD_NOT_ALLOWED(405, "不支持的请求方法"),
    /**
     * 系统繁忙，请稍后再试
     */
    INTERNAL_ERROR(500, "系统繁忙，请稍后再试"),

    /**
     * 请输入正确的11位手机号
     */
    PHONE_FORMAT_ERROR(1001, "请输入正确的11位手机号"),
    /**
     * 验证码错误，请重新输入
     */
    SMS_CODE_ERROR(1002, "验证码错误，请重新输入"),
    /**
     * 验证码已过期，请重新获取
     */
    SMS_CODE_EXPIRED(1003, "验证码已过期，请重新获取"),
    /**
     * 验证码发送过于频繁，请稍后再试
     */
    SMS_CODE_FREQUENT(1004, "验证码发送过于频繁，请稍后再试"),
    /**
     * 今日验证码发送次数已达上限，请明日再试
     */
    SMS_CODE_DAILY_LIMIT(1007, "今日验证码发送次数已达上限，请明日再试"),
    /**
     * 账号已被禁用，请联系管理员
     */
    USER_DISABLED(1005, "账号已被禁用，请联系管理员"),
    /**
     * 用户不存在
     */
    USER_NOT_FOUND(1006, "用户不存在"),
    /**
     * 角色不存在
     */
    ROLE_NOT_FOUND(1008, "角色不存在"),
    /**
     * 权限不存在
     */
    PERMISSION_NOT_FOUND(1009, "权限不存在"),

    /**
     * 您的输入包含违规内容，请修改后重新发送
     */
    CONTENT_VIOLATION(2001, "您的输入包含违规内容，请修改后重新发送"),
    /**
     * 检测到紧急医疗情况
     */
    EMERGENCY_TRIGGERED(2002, "检测到紧急医疗情况"),
    /**
     * AI服务暂时不可用，请稍后再试
     */
    AI_API_ERROR(2003, "AI服务暂时不可用，请稍后再试"),
    /**
     * 抱歉，我无法为您提供相关建议，请咨询专业医生
     */
    AI_RESPONSE_VIOLATION(2004, "抱歉，我无法为您提供相关建议，请咨询专业医生"),

    /**
     * 该敏感词已存在
     */
    SENSITIVE_WORD_EXISTS(3001, "该敏感词已存在"),
    /**
     * 该分类下存在常见问题，无法删除
     */
    FAQ_CATEGORY_NOT_EMPTY(3002, "该分类下存在常见问题，无法删除"),
    /**
     * 该配置项已存在
     */
    CONFIG_KEY_EXISTS(3003, "该配置项已存在"),

    /**
     * 咨询频率过高，请稍后再试
     */
    RATE_LIMIT_EXCEEDED(4001, "咨询频率过高，请稍后再试"),
    /**
     * 文件上传失败
     */
    FILE_UPLOAD_ERROR(4002, "文件上传失败"),
    /**
     * 不支持的文件类型
     */
    FILE_TYPE_NOT_ALLOWED(4003, "不支持的文件类型"),
    /**
     * 文件大小超出限制
     */
    FILE_SIZE_EXCEEDED(4004, "文件大小超出限制"),

    /**
     * 每日咨询配额已用完
     */
    DAILY_QUOTA_EXCEEDED(4005, "每日咨询配额已用完"),
    /**
     * 会员等级不存在
     */
    MEMBER_LEVEL_NOT_FOUND(5001, "会员等级不存在"),
    /**
     * 方案不存在或已下架
     */
    PLAN_NOT_FOUND(5002, "方案不存在或已下架"),
    /**
     * 订单不存在
     */
    ORDER_NOT_FOUND(5003, "订单不存在"),
    /**
     * 订单已过期
     */
    ORDER_EXPIRED(5004, "订单已过期"),
    /**
     * 订单已支付
     */
    ORDER_ALREADY_PAID(5005, "订单已支付"),
    /**
     * 金额不匹配
     */
    AMOUNT_MISMATCH(5006, "金额不匹配"),
    /**
     * 支付宝验签失败
     */
    ALIPAY_VERIFY_FAILED(5007, "支付宝验签失败"),
    /**
     * 已有生效会员
     */
    MEMBER_ALREADY_EXISTS(5008, "已有生效会员"),
    /**
     * 首月体验价已使用
     */
    FIRST_PURCHASE_USED(5009, "首月体验价已使用"),
    /**
     * 退款金额计算异常
     */
    REFUND_AMOUNT_ERROR(5010, "退款金额计算异常"),
    /**
     * 体验价订单不支持退款
     */
    REFUND_NOT_ALLOWED(5011, "体验价订单不支持退款"),
    /**
     * 退款审核已处理
     */
    REFUND_ALREADY_HANDLED(5012, "退款审核已处理"),
    /**
     * 邀请码生成失败
     */
    INVITE_CODE_FAILED(5013, "邀请码生成失败"),
    /**
     * 订单状态不允许此操作
     */
    ORDER_STATUS_INVALID(5014, "订单状态不允许此操作"),
    /**
     * 会员记录不存在
     */
    MEMBER_NOT_FOUND(5015, "会员记录不存在"),
    /**
     * 支付回调处理失败
     */
    PAYMENT_CALLBACK_ERROR(5016, "支付回调处理失败"),
    /**
     * 退款状态异常
     */
    REFUND_STATUS_ERROR(5017, "退款状态异常"),
    /**
     * 不能使用自己的邀请码
     */
    SELF_INVITE_FORBIDDEN(5018, "不能使用自己的邀请码"),
    /**
     * 已被邀请，不可重复绑定
     */
    ALREADY_INVITED(5019, "已被邀请，不可重复绑定");

    /**
     * 状态码
     */
    private final int code;
    /**
     * 状态消息
     */
    private final String message;

    /**
     * 构造方法
     *
     * @param code    状态码
     * @param message 状态消息
     */
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
