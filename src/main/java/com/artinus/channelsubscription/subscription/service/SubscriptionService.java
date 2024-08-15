package com.artinus.channelsubscription.subscription.service;

import com.artinus.channelsubscription.subscription.domain.SubscribeRequest;
import com.artinus.channelsubscription.subscription.repository.AccountRepository;
import com.artinus.channelsubscription.channel.repository.ChannelRepository;
import com.artinus.channelsubscription.subscription.repository.SubscriptionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final AccountRepository accountRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ChannelRepository channelRepository;

    @Transactional
    public void subscribe(@Valid SubscribeRequest request) {



    }
}
