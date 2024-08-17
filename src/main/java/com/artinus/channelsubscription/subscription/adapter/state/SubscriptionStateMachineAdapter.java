package com.artinus.channelsubscription.subscription.adapter.state;

import com.artinus.channelsubscription.channel.domain.ChannelType;
import com.artinus.channelsubscription.channel.domain.RegisteredChannel;
import com.artinus.channelsubscription.common.exception.CommonApplicationException;
import com.artinus.channelsubscription.subscription.application.port.output.AttemptTransitionPort;
import com.artinus.channelsubscription.subscription.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SubscriptionStateMachineAdapter implements AttemptTransitionPort {

    private final StateMachineService<SubscriptionStatus, SubscriptionEvent> stateMachineService;

    public void transitionSubscriptionStatus(AttemptTransitionSubscriptionStatus behavior) {
        // 회원에 해당하는 State Machine을 Redis에서 가져옴 (없으면 신규 생성)
        String stateMachineId = String.format("%d_%s", behavior.accountId(), behavior.phoneNumber());
        StateMachine<SubscriptionStatus, SubscriptionEvent> acquiredStateMachine =
                stateMachineService.acquireStateMachine(stateMachineId);

        // State Machine에 이벤트 전달 및 결과 리턴
        StateMachineEventResult<SubscriptionStatus, SubscriptionEvent> stateMachineEventResult = acquiredStateMachine
                .sendEvent(buildMessage(behavior.subscriptionEvent(), behavior.channelType(), behavior.operation()))
                .blockFirst();

        assert stateMachineEventResult != null;
        StateMachineEventResult.ResultType resultType = stateMachineEventResult.getResultType();

        // State Machine에서 거부되면 예외 발생
        if (StateMachineEventResult.ResultType.DENIED.equals(resultType))
            throw CommonApplicationException.SUBSCRIPTION_TRANSITION_DENIED;
    }

    private static Mono<Message<SubscriptionEvent>> buildMessage(final SubscriptionEvent event, final ChannelType channelType, final SubscribeOperation operation) {
        return Mono.just(
                MessageBuilder.withPayload(event)
                        .setHeader("channelType", channelType)
                        .setHeader("operation", operation)
                        .build()
        );
    }
}
