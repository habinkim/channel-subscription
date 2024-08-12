package com.artinus.channelsubscription;

import com.artinus.channelsubscription.common.response.BasePayload;
import com.artinus.channelsubscription.common.response.MessageCode;
import com.artinus.channelsubscription.common.response.Response;
import com.artinus.channelsubscription.common.response.ResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {

    private final ResponseMapper responseMapper;

    @GetMapping(value = "/hello")
    public ResponseEntity<Response<HelloResponse>> hello() {
        return responseMapper.ok(MessageCode.SUCCESS, new HelloResponse("Hello, World!"));
    }

    public record HelloResponse(String message) implements BasePayload {
    }

}
