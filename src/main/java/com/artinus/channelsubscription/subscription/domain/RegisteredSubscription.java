package com.artinus.channelsubscription.subscription.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RegisteredSubscription(Long subscriptionId, String phoneNumber, Long channelId, String channelName,
                                     SubscriptionStatus previousStatus,
                                     SubscriptionStatus status,

                                     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                     LocalDateTime createdAt) {
}
