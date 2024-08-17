package com.artinus.channelsubscription.subscription.application.port.input;

import com.artinus.channelsubscription.subscription.domain.RegisteredSubscription;

@FunctionalInterface
public interface SubscribeUseCase {

    RegisteredSubscription subscribe(final SubscribeCommand command);

}
