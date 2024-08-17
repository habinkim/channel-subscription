package com.artinus.channelsubscription.subscription.application.port.output;

import com.artinus.channelsubscription.subscription.domain.SubscriptionHistory;

import java.util.List;
import java.util.Map;

public interface LoadSubscriptionPort {
    Map<String, List<SubscriptionHistory>> getSubscriptionHistory(final String phoneNumber);
}
