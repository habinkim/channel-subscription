package com.artinus.channelsubscription.channel.application.port.input;


import com.artinus.channelsubscription.channel.domain.ChannelType;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RegisterChannelCommand(@NotBlank String name, @NotNull ChannelType type) {
}
