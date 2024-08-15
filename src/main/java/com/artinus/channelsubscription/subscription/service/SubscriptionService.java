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
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * @see <a href="https://devloo.tistory.com/entry/Spring-State-Machine-%EC%9D%84-%EC%96%B4%EB%96%BB%EA%B2%8C-%ED%99%9C%EC%9A%A9%ED%95%A0-%EC%88%98-%EC%9E%88%EC%9D%84%EA%B9%8C%EC%9A%94-%EC%82%AC%EC%9A%A9-%EB%B0%A9%EB%B2%95-%EB%B0%8F-%EC%98%88%EC%A0%9C">Spring State Machine을 어떻게 활용할 수 있을까요?</a>
 * @see <a href="https://medium.com/@alishazy/spring-statemachine-a-comprehensive-guide-31dc346a600d">Spring Statemachine: A Comprehensive Guide</a>
 */
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final AccountRepository accountRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ChannelRepository channelRepository;

    private final StateMachineService<SubscriptionStatus, SubscriptionEvent> stateMachineService;

    @Transactional
    public void subscribe(@Valid SubscribeRequest request) {
        Channel channel = channelRepository.findByIdAndAvailableTrue(request.channelId())
                .orElseThrow(CommonApplicationException.CHANNEL_NOT_FOUND);

        Account account = accountRepository.findByPhoneNumber(request.phoneNumber())
                .orElseThrow(CommonApplicationException.ACCOUNT_NOT_FOUND);

        StateMachine<SubscriptionStatus, SubscriptionEvent> acquiredStateMachine =
                stateMachineService.acquireStateMachine(String.valueOf(account.getId()));

        acquiredStateMachine.getExtendedState();

        StateMachineEventResult<SubscriptionStatus, SubscriptionEvent> result = acquiredStateMachine
                .sendEvent(buildMessage(getEvent(account, request), channel.getId(), account.getPhoneNumber()))
                .blockFirst();

        StateMachineEventResult.ResultType resultType = result.getResultType();



    }

    private static SubscriptionEvent getEvent(Account account, SubscribeRequest request) {
        return SubscriptionEvent.from(account.getCurrentSubscriptionStatus(), request.operation());
    }

    private static Mono<Message<SubscriptionEvent>> buildMessage(SubscriptionEvent event, Long channelId, String phoneNumber) {
        return Mono.just(
                MessageBuilder.withPayload(event)
                        .setHeader("channelId", channelId)
                        .setHeader("phoneNumber", phoneNumber)
                        .build()
        );
    }
}
