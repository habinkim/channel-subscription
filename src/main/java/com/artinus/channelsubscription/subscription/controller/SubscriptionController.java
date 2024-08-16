package com.artinus.channelsubscription.subscription.controller;

import com.artinus.channelsubscription.common.response.MessageCode;
import com.artinus.channelsubscription.common.response.Response;
import com.artinus.channelsubscription.common.response.ResponseMapper;
import com.artinus.channelsubscription.subscription.domain.RegisteredSubscription;
import com.artinus.channelsubscription.subscription.domain.SubscribeRequest;
import com.artinus.channelsubscription.subscription.service.SubscriptionService;
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

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    public ResponseEntity<Response<RegisteredSubscription>> subscribeChannel(@Valid @RequestBody SubscribeRequest request) {
        RegisteredSubscription registeredSubscription = subscriptionService.subscribe(request);
        return responseMapper.ok(MessageCode.CREATED, registeredSubscription);
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
