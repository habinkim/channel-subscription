package com.artinus.channelsubscription.channel.domain;


import com.artinus.channelsubscription.channel.repository.ChannelType;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RegisterChannelRequest(@NotBlank String name, @NotNull ChannelType type) {
}
