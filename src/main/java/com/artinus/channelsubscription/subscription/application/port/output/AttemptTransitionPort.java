package com.artinus.channelsubscription.subscription.application.port.output;

import com.artinus.channelsubscription.subscription.domain.AttemptTransitionSubscriptionStatus;

public interface AttemptTransitionPort {
    void transitionSubscriptionStatus(AttemptTransitionSubscriptionStatus behavior);
}
