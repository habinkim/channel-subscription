package com.artinus.channelsubscription.subscription.adapter.rest;

import com.artinus.channelsubscription.common.response.MessageCode;
import com.artinus.channelsubscription.common.response.Response;
import com.artinus.channelsubscription.common.response.ResponseMapper;
import com.artinus.channelsubscription.common.stereotype.WebAdapter;
import com.artinus.channelsubscription.subscription.application.port.input.GetSubscriptionHistoryUseCase;
import com.artinus.channelsubscription.subscription.application.port.input.SubscribeCommand;
import com.artinus.channelsubscription.subscription.application.port.input.SubscribeUseCase;
import com.artinus.channelsubscription.subscription.application.port.input.UnsubscribeUseCase;
import com.artinus.channelsubscription.subscription.domain.RegisteredSubscription;
import com.artinus.channelsubscription.subscription.domain.SubscriptionHistory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@WebAdapter
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final ResponseMapper responseMapper;

    private final SubscribeUseCase subscribeUseCase;
    private final UnsubscribeUseCase unsubscribeUseCase;
    private final GetSubscriptionHistoryUseCase getSubscriptionHistoryUseCase;


    @PostMapping("/subscribe")
    public ResponseEntity<Response<RegisteredSubscription>> subscribeChannel(@Valid @RequestBody SubscribeCommand request) {
        RegisteredSubscription registeredSubscription = subscribeUseCase.subscribe(request);
        return responseMapper.ok(MessageCode.CREATED, registeredSubscription);
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<Response<RegisteredSubscription>> unsubscribeChannel(@Valid @RequestBody SubscribeCommand request) {
        RegisteredSubscription registeredSubscription = unsubscribeUseCase.unsubscribe(request);
        return responseMapper.ok(MessageCode.SUCCESS, registeredSubscription);
    }

    @GetMapping
    public ResponseEntity<Response<Map<String, List<SubscriptionHistory>>>> getSubscriptionHistory(
            @RequestParam(name = "phoneNumber") @NotBlank String phoneNumber) {
        Map<String, List<SubscriptionHistory>> histories = getSubscriptionHistoryUseCase.getSubscriptionHistory(phoneNumber);
        return responseMapper.ok(histories);
    }


}
