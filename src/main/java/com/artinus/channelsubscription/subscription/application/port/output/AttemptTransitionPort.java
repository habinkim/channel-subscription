package com.artinus.channelsubscription.subscription.application.port.output;

import com.artinus.channelsubscription.subscription.domain.AttemptTransitionSubscriptionStatus;
import com.artinus.channelsubscription.subscription.domain.SubscriptionEvent;
import com.artinus.channelsubscription.subscription.domain.SubscriptionStatus;
import org.springframework.statemachine.StateMachineEventResult;

public interface AttemptTransitionPort {
    StateMachineEventResult<SubscriptionStatus, SubscriptionEvent> transitionSubscriptionStatus(AttemptTransitionSubscriptionStatus behavior);
}
