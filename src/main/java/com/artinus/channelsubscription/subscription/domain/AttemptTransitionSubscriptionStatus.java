package com.artinus.channelsubscription.subscription.domain;

import com.artinus.channelsubscription.channel.domain.ChannelType;

public record AttemptTransitionSubscriptionStatus(Long accountId, String phoneNumber, ChannelType channelType,
                                                  SubscriptionEvent subscriptionEvent,
                                                  SubscribeOperation operation) {
}
