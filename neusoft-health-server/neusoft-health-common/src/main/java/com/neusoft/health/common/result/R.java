package com.neusoft.health.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一API响应结果
 * <p>
 * 用于封装所有API接口的响应结果，包含状态码、消息和数据。
 * 成功状态码为0，非0表示失败。
 * </p>
 *
 * @param <T> 响应数据类型
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class R<T> {

    /**
     * 状态码，0表示成功，非0表示失败
     */
    private int code;
    /**
     * 响应消息
     */
    private String message;
    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应，无数据
     *
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> R<T> ok() {
        return new R<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功响应，带数据
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> R<T> ok(T data) {
        return new R<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功响应，自定义消息，带数据
     *
     * @param message 自定义消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return 成功响应
     */
    public static <T> R<T> ok(String message, T data) {
        return new R<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败响应，根据ResultCode
     *
     * @param resultCode 结果码枚举
     * @param <T>        数据类型
     * @return 失败响应
     */
    public static <T> R<T> fail(ResultCode resultCode) {
        return new R<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * 失败响应，根据ResultCode，自定义消息
     *
     * @param resultCode 结果码枚举
     * @param message    自定义消息
     * @param <T>        数据类型
     * @return 失败响应
     */
    public static <T> R<T> fail(ResultCode resultCode, String message) {
        return new R<>(resultCode.getCode(), message, null);
    }

    /**
     * 失败响应，指定错误码和消息
     *
     * @param code    错误码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 失败响应
     */
    public static <T> R<T> fail(int code, String message) {
        return new R<>(code, message, null);
    }
}
