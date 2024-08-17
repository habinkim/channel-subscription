package com.artinus.channelsubscription.subscription.application.port.input;

import com.artinus.channelsubscription.subscription.domain.SubscriptionHistory;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface GetSubscriptionHistoryUseCase {

    Map<String, List<SubscriptionHistory>> getSubscriptionHistory(final String phoneNumber);

}
