package com.artinus.channelsubscription.subscription.application.port.output;

import com.artinus.channelsubscription.subscription.domain.RegisteredSubscription;
import com.artinus.channelsubscription.subscription.domain.SaveSubscription;

public interface SaveSubscriptionPort {
    RegisteredSubscription saveSubscription(SaveSubscription behavior);
}
