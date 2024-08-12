package com.artinus.channelsubscription.common.response;

public sealed interface AbstractResponse permits Response, ExceptionResponse {

    String getMessage();

    String getCode();

}
