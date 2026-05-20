package com.neusoft.health.common.exception;

import com.neusoft.health.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 * <p>
 * 用于抛出业务逻辑相关的异常，包含错误码和错误消息。
 * 所有自定义业务异常都应继承此类。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    /**
     * 根据ResultCode构造业务异常
     *
     * @param resultCode 结果码枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    /**
     * 根据ResultCode和自定义消息构造业务异常
     *
     * @param resultCode 结果码枚举
     * @param message    自定义错误消息
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    /**
     * 根据错误码和消息构造业务异常
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
