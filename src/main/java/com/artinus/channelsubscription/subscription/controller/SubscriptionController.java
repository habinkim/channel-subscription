package com.artinus.channelsubscription.subscription.controller;

import com.artinus.channelsubscription.common.response.MessageCode;
import com.artinus.channelsubscription.common.response.Response;
import com.artinus.channelsubscription.common.response.ResponseMapper;
import com.artinus.channelsubscription.subscription.domain.RegisteredSubscription;
import com.artinus.channelsubscription.subscription.domain.SubscribeRequest;
import com.artinus.channelsubscription.subscription.domain.SubscriptionHistory;
import com.artinus.channelsubscription.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Response<RegisteredSubscription>> unsubscribeChannel(@Valid @RequestBody SubscribeRequest request) {
        RegisteredSubscription registeredSubscription = subscriptionService.unsubscribe(request);
        return responseMapper.ok(MessageCode.SUCCESS, registeredSubscription);
    }

    @GetMapping
    public ResponseEntity<Response<Map<String, List<SubscriptionHistory>>>> getSubscriptionHistory(
            @RequestParam(name = "phoneNumber") @NotBlank String phoneNumber) {
        Map<String, List<SubscriptionHistory>> histories = subscriptionService.getSubscriptionHistory(phoneNumber);
        return responseMapper.ok(histories);
    }


}
