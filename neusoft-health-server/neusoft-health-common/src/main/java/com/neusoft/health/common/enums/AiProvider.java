package com.neusoft.health.common.enums;

import lombok.Getter;

@Getter
public enum AiProvider {

    DEEPSEEK("DeepSeek", "Authorization", "Bearer "),
    MIMO("MiMo", "api-key", ""),
    BAILIAN_MIMO("百炼-MiMo", "Authorization", "Bearer "),
    OPENAI("OpenAI", "Authorization", "Bearer "),
    DOUBAO("豆包", "Authorization", "Bearer ");

    private final String displayName;
    private final String authHeaderName;
    private final String authHeaderPrefix;

    AiProvider(String displayName, String authHeaderName, String authHeaderPrefix) {
        this.displayName = displayName;
        this.authHeaderName = authHeaderName;
        this.authHeaderPrefix = authHeaderPrefix;
    }

    public static AiProvider fromCode(String code) {
        if (code == null || code.isBlank()) {
            return DEEPSEEK;
        }
        for (AiProvider provider : values()) {
            if (provider.name().equalsIgnoreCase(code)) {
                return provider;
            }
        }
        return DEEPSEEK;
    }
}
