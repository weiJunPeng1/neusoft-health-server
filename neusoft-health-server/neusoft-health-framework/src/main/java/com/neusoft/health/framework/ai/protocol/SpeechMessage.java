package com.neusoft.health.framework.ai.protocol;

import com.neusoft.health.framework.ai.protocol.SpeechProtocol.*;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

@Data
public class SpeechMessage {

    private byte version = 1;
    private byte headerSize = 1;
    private MsgType type;
    private MsgTypeFlagBits flag;
    private byte serialization = SerializationBits.JSON.getValue();
    private byte compression = CompressionBits.None.getValue();

    private EventType event;
    private String sessionId;
    private int errorCode;
    private byte[] payload;

    public SpeechMessage(MsgType type, MsgTypeFlagBits flag) {
        this.type = type;
        this.flag = flag;
    }

    public static SpeechMessage unmarshal(byte[] data) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int versionAndHeaderSize = buffer.get() & 0xFF;
        int version = (versionAndHeaderSize >> 4) & 0x0F;
        int headerSize = versionAndHeaderSize & 0x0F;

        int typeAndFlag = buffer.get() & 0xFF;
        MsgType type = MsgType.fromValue((typeAndFlag >> 4) & 0x0F);
        MsgTypeFlagBits flag = MsgTypeFlagBits.fromValue(typeAndFlag & 0x0F);

        int serialAndCompress = buffer.get() & 0xFF;
        SerializationBits serialization = SerializationBits.values()[0];
        for (SerializationBits s : SerializationBits.values()) {
            if (s.getValue() == ((serialAndCompress >> 4) & 0x0F)) {
                serialization = s;
                break;
            }
        }

        int headerSizeInt = 4 * headerSize;
        int paddingSize = headerSizeInt - 3;
        while (paddingSize > 0 && buffer.hasRemaining()) {
            buffer.get();
            paddingSize--;
        }

        SpeechMessage message = new SpeechMessage(type, flag);
        message.setVersion((byte) version);
        message.setHeaderSize((byte) headerSize);
        message.setSerialization(serialization.getValue());

        if (flag == MsgTypeFlagBits.WITH_EVENT) {
            if (buffer.remaining() >= 4) {
                byte[] eventBytes = new byte[4];
                buffer.get(eventBytes);
                message.setEvent(EventType.fromValue(ByteBuffer.wrap(eventBytes).order(ByteOrder.BIG_ENDIAN).getInt()));
            }

            if (type != MsgType.ERROR && message.getEvent() != null
                    && message.getEvent() != EventType.START_CONNECTION
                    && message.getEvent() != EventType.FINISH_CONNECTION
                    && message.getEvent() != EventType.CONNECTION_STARTED
                    && message.getEvent() != EventType.CONNECTION_FAILED
                    && message.getEvent() != EventType.CONNECTION_FINISHED) {
                if (buffer.remaining() >= 4) {
                    int sessionIdLen = buffer.getInt();
                    if (sessionIdLen > 0 && buffer.remaining() >= sessionIdLen) {
                        byte[] sessionIdBytes = new byte[sessionIdLen];
                        buffer.get(sessionIdBytes);
                        message.setSessionId(new String(sessionIdBytes, StandardCharsets.UTF_8));
                    }
                }
            }
        }

        if (type == MsgType.ERROR && buffer.remaining() >= 4) {
            byte[] errorCodeBytes = new byte[4];
            buffer.get(errorCodeBytes);
            message.setErrorCode(ByteBuffer.wrap(errorCodeBytes).order(ByteOrder.BIG_ENDIAN).getInt());
        }

        if (buffer.remaining() >= 4) {
            int payloadLength = buffer.getInt();
            if (payloadLength > 0 && buffer.remaining() >= payloadLength) {
                byte[] payloadBytes = new byte[payloadLength];
                buffer.get(payloadBytes);
                message.setPayload(payloadBytes);
            }
        }

        return message;
    }

    public byte[] marshal() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        buffer.write(((version & 0x0F) << 4) | (headerSize & 0x0F));
        buffer.write(((type.getValue() & 0x0F) << 4) | (flag.getValue() & 0x0F));
        buffer.write(((serialization & 0x0F) << 4) | (compression & 0x0F));

        int headerSizeInt = 4 * headerSize;
        int padding = headerSizeInt - buffer.size();
        while (padding > 0) {
            buffer.write(0);
            padding--;
        }

        if (event != null) {
            buffer.write(ByteBuffer.allocate(4).putInt(event.getValue()).array());
        }

        if (sessionId != null) {
            byte[] sessionIdBytes = sessionId.getBytes(StandardCharsets.UTF_8);
            buffer.write(ByteBuffer.allocate(4).putInt(sessionIdBytes.length).array());
            buffer.write(sessionIdBytes);
        }

        if (payload != null && payload.length > 0) {
            buffer.write(ByteBuffer.allocate(4).putInt(payload.length).array());
            buffer.write(payload);
        }

        return buffer.toByteArray();
    }
}
