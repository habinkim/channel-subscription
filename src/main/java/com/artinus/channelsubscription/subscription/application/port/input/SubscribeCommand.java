package com.artinus.channelsubscription.subscription.application.port.input;

import com.artinus.channelsubscription.subscription.domain.SubscriptionStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SubscribeCommand(@NotBlank String phoneNumber, @NotNull Long channelId, @NotNull SubscriptionStatus operation) {
}
