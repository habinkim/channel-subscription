package com.artinus.channelsubscription.common.exception;

import com.artinus.channelsubscription.common.response.MessageCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.function.Supplier;

/**
 * @see <a href="https://velog.io/@hope0206/Java의-예외-생성-비용-비용-절감-방법">Java의 예외 생성 비용 절감 방법</a>
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class CommonApplicationException extends RuntimeException implements Supplier<CommonApplicationException>, Runnable {

    private MessageCode messageCode;

    public static final CommonApplicationException TOO_MANY_REQUESTS = new CommonApplicationException(MessageCode.TOO_MANY_REQUESTS);
    public static final CommonApplicationException NOT_OWNER = new CommonApplicationException(MessageCode.NOT_OWNER);
    public static final CommonApplicationException NOT_FOUND_DATA = new CommonApplicationException(MessageCode.NOT_FOUND_DATA);
    public static final CommonApplicationException ERROR = new CommonApplicationException(MessageCode.ERROR);

    public static final CommonApplicationException CHANNEL_ALREADY_EXISTS = new CommonApplicationException(MessageCode.CHANNEL_ALREADY_EXISTS);
    public static final CommonApplicationException CHANNEL_NOT_FOUND = new CommonApplicationException(MessageCode.CHANNEL_NOT_FOUND);

    public static final CommonApplicationException ACCOUNT_NOT_FOUND = new CommonApplicationException(MessageCode.ACCOUNT_NOT_FOUND);

    @Override
    public synchronized Throwable fillInStackTrace() {
        return MessageCode.ERROR.equals(messageCode) ? super.fillInStackTrace() : this;
    }

    @Override
    public CommonApplicationException get() {
        return this;
    }

    @Override
    public void run() {
        throw this;
    }
}
