package com.neusoft.health.framework.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.neusoft.health.framework.config.SmsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信发送工具类
 * <p>
 * 用于发送短信验证码，调用第三方短信服务API。
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public final class SmsUtil {

    /**
     * 短信配置属性
     */
    private final SmsProperties smsProperties;

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @param code 验证码
     * @return 是否发送成功
     */
    public boolean send(String phone, String code) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("code", code);
            body.put("number", smsProperties.getTime());
            body.put("to", phone);

            String result = HttpRequest.post(smsProperties.getUrl())
                    .header("Content-Type", "application/json")
                    .body(JSONUtil.toJsonStr(body))
                    .execute()
                    .body();
            JSONObject jsonObject = JSONUtil.parseObj(result);
            String code1 = jsonObject.getStr("code");
            String requestId = jsonObject.getStr("request_id");
            String msg = jsonObject.getStr("msg");
            if (!code1.equals("200")) {
                throw new RuntimeException("短信发送请求失败，request_id=" + msg + ",code=" + code1);
            }
            log.info("SMS sent to {} with code {}, Valid within {} minutes , response: {}", phone, code, smsProperties.getTime(), result);
            return true;
        } catch (Exception e) {
            log.error("SMS send failed to {}: {}", phone, e.getMessage(), e);
            return false;
        }
    }
}
