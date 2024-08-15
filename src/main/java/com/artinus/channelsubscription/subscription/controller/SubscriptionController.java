package com.artinus.channelsubscription.subscription.controller;

import com.artinus.channelsubscription.common.response.Response;
import com.artinus.channelsubscription.common.response.ResponseMapper;
import com.artinus.channelsubscription.subscription.domain.SubscribeRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final ResponseMapper responseMapper;

    @PostMapping("/subscribe")
    public ResponseEntity<Response<?>> subscribeChannel(@Valid @RequestBody SubscribeRequest request) {
        return responseMapper.ok();
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<Response<?>> unsubscribeChannel() {
        return responseMapper.ok();
    }

    @GetMapping
    public ResponseEntity<Response<?>> getSubscriptionHistory(
            @RequestParam(name = "phoneNumber") @NotBlank String phoneNumber) {
        return responseMapper.ok();
    }


}
