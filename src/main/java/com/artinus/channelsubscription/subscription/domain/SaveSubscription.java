package com.artinus.channelsubscription.subscription.domain;

public record SaveSubscription(
        String phoneNumber,
        Long channelId,
        SubscriptionStatus previousSubscriptionStatus,
        SubscriptionStatus subscriptionStatus
) {
}
