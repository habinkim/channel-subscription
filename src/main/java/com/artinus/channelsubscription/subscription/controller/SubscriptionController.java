package com.artinus.channelsubscription.subscription.controller;

import com.artinus.channelsubscription.common.response.ResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final ResponseMapper responseMapper;

    @PostMapping("/subscribe")
    public void subscribeChannel() {

    }

    @PostMapping("/unsubscribe")
    public void unsubscribeChannel() {

    }

    @GetMapping("/history")
    public void getSubscriptionHistory() {

    }

    @GetMapping("/channel-history")
    public void getChannelSubscriptionHistory() {

    }



}
