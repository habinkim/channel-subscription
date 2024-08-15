package com.artinus.channelsubscription.subscription.service;

import com.artinus.channelsubscription.channel.entity.Channel;
import com.artinus.channelsubscription.channel.repository.ChannelRepository;
import com.artinus.channelsubscription.common.exception.CommonApplicationException;
import com.artinus.channelsubscription.subscription.domain.SubscribeRequest;
import com.artinus.channelsubscription.subscription.entity.Account;
import com.artinus.channelsubscription.subscription.entity.SubscriptionStatus;
import com.artinus.channelsubscription.subscription.repository.AccountRepository;
import com.artinus.channelsubscription.subscription.repository.SubscriptionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final AccountRepository accountRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ChannelRepository channelRepository;

    private final StateMachine<SubscriptionStatus, SubscriptionEvent> stateMachine;

    @Transactional
    public void subscribe(@Valid SubscribeRequest request) {
        Channel channel = channelRepository.findByIdAndAvailableTrue(request.channelId())
                .orElseThrow(CommonApplicationException.CHANNEL_NOT_FOUND);

        Account account = accountRepository.findByPhoneNumber(request.phoneNumber())
                .orElseThrow(CommonApplicationException.ACCOUNT_NOT_FOUND);

        stateMachine.getStateMachineAccessor().doWithAllRegions(access -> access.resetStateMachine(account.getCurrentSubscriptionStatus()));
        stateMachine.sendEvent(event);

    }
}
