package com.neusoft.health.framework.ai.protocol;

import lombok.Getter;

public final class SpeechProtocol {

    private SpeechProtocol() {
    }

    @Getter
    public enum MsgType {
        INVALID((byte) 0),
        FULL_CLIENT_REQUEST((byte) 0b1),
        AUDIO_ONLY_CLIENT((byte) 0b10),
        FULL_SERVER_RESPONSE((byte) 0b1001),
        AUDIO_ONLY_SERVER((byte) 0b1011),
        FRONT_END_RESULT_SERVER((byte) 0b1100),
        ERROR((byte) 0b1111);

        private final byte value;

        MsgType(byte value) {
            this.value = value;
        }

        public static MsgType fromValue(int value) {
            for (MsgType type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            return INVALID;
        }
    }

    @Getter
    public enum MsgTypeFlagBits {
        NO_SEQ((byte) 0),
        POSITIVE_SEQ((byte) 0b1),
        LAST_NO_SEQ((byte) 0b10),
        NEGATIVE_SEQ((byte) 0b11),
        WITH_EVENT((byte) 0b100);

        private final byte value;

        MsgTypeFlagBits(byte value) {
            this.value = value;
        }

        public static MsgTypeFlagBits fromValue(int value) {
            for (MsgTypeFlagBits flag : values()) {
                if (flag.value == value) {
                    return flag;
                }
            }
            return NO_SEQ;
        }
    }

    @Getter
    public enum EventType {
        NONE(0),
        START_CONNECTION(1),
        START_TASK(1),
        FINISH_CONNECTION(2),
        FINISH_TASK(2),
        CONNECTION_STARTED(50),
        TASK_STARTED(50),
        CONNECTION_FAILED(51),
        TASK_FAILED(51),
        CONNECTION_FINISHED(52),
        TASK_FINISHED(52),
        START_SESSION(100),
        CANCEL_SESSION(101),
        FINISH_SESSION(102),
        SESSION_STARTED(150),
        SESSION_CANCELED(151),
        SESSION_FINISHED(152),
        SESSION_FAILED(153),
        USAGE_RESPONSE(154),
        CHARGE_DATA(154),
        TASK_REQUEST(200),
        UPDATE_CONFIG(201),
        AUDIO_MUTED(250),
        SAY_HELLO(300),
        TTS_SENTENCE_START(350),
        TTS_SENTENCE_END(351),
        TTS_RESPONSE(352),
        TTS_ENDED(359),
        PODCAST_ROUND_START(360),
        PODCAST_ROUND_RESPONSE(361),
        PODCAST_ROUND_END(362),
        ASR_INFO(450),
        ASR_RESPONSE(451),
        ASR_ENDED(459),
        CHAT_TTS_TEXT(500),
        CHAT_RESPONSE(550),
        CHAT_ENDED(559),
        SOURCE_SUBTITLE_START(650),
        SOURCE_SUBTITLE_RESPONSE(651),
        SOURCE_SUBTITLE_END(652),
        TRANSLATION_SUBTITLE_START(653),
        TRANSLATION_SUBTITLE_RESPONSE(654),
        TRANSLATION_SUBTITLE_END(655);

        private final int value;

        EventType(int value) {
            this.value = value;
        }

        public static EventType fromValue(int value) {
            for (EventType type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            return NONE;
        }
    }

    @Getter
    public enum SerializationBits {
        Raw((byte) 0),
        JSON((byte) 0b1),
        Thrift((byte) 0b11),
        Custom((byte) 0b1111);

        private final byte value;

        SerializationBits(byte value) {
            this.value = value;
        }
    }

    @Getter
    public enum CompressionBits {
        None((byte) 0),
        Gzip((byte) 0b1),
        Custom((byte) 0b11);

        private final byte value;

        CompressionBits(byte value) {
            this.value = value;
        }
    }
}
