package com.artinus.channelsubscription.channel.domain;

import com.artinus.channelsubscription.channel.repository.ChannelType;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RegisteredChannel(Long id, String name, ChannelType type, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
