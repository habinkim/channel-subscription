package com.artinus.channelsubscription.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

/**
 * @see com.artinus.channelsubscription.common.response.ResponseMapper#ok(MessageCode)
 * @see com.artinus.channelsubscription.common.response.ResponseMapper#error(MessageCode)
 */
@Getter
@AllArgsConstructor
public enum MessageCode {

    SUCCESS(OK, "0000"),
    CREATED(HttpStatus.CREATED, "0001"),
    ACCEPTED(HttpStatus.ACCEPTED, "0002"),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "9996"),
    NOT_OWNER(UNAUTHORIZED, "9997"),
    NOT_FOUND_DATA(BAD_REQUEST, "9998"),
    ERROR(INTERNAL_SERVER_ERROR, "9999"),
    EXCEPTION_ILLEGAL_ARGUMENT(BAD_REQUEST, "9100"),

    // Channel
    CHANNEL_ALREADY_EXISTS(BAD_REQUEST, "1101"),
    CHANNEL_NOT_FOUND(BAD_REQUEST, "1102"),

    // Account
    ACCOUNT_NOT_FOUND(BAD_REQUEST, "1201"),

    // Subscription
    SUBSCRIPTION_DENIED(BAD_REQUEST, "1301");

    private final HttpStatus httpStatus;
    private final String code;

    public static Optional<MessageCode> get(String name) {
        return Arrays.stream(MessageCode.values())
                .filter(env -> env.name().equals(name))
                .findFirst();
    }

}
