package com.artinus.channelsubscription.subscription.controller;

import com.artinus.channelsubscription.common.response.ResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    private final ResponseMapper responseMapper;

    @PostMapping("/channels/subscribe")
    public void subscribeChannel() {

    }

    @PostMapping("/channels/unsubscribe")
    public void unsubscribeChannel() {

    }

    @GetMapping("/history")
    public void getSubscriptionHistory() {

    }

    @GetMapping("/channel-history")
    public void getChannelSubscriptionHistory() {

    }



}
