package com.artinus.channelsubscription.subscription.domain;

import com.artinus.channelsubscription.subscription.entity.SubscriptionStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RegisteredSubscription(Long subscriptionId, String phoneNumber, Long channelId, String channelName,
                                     SubscriptionStatus previousSubscriptionStatus,
                                     SubscriptionStatus subscriptionStatus) {
}
