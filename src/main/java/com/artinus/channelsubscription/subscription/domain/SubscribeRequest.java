package com.artinus.channelsubscription.subscription.domain;

import com.artinus.channelsubscription.subscription.entity.SubscriptionStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SubscribeRequest(@NotBlank String phoneNumber, @NotNull Long channelId, @NotNull SubscriptionStatus operation) {
}
