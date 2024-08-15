package com.artinus.channelsubscription.channel.domain;

import com.artinus.channelsubscription.channel.entity.ChannelType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RegisteredChannel(Long id, String name, ChannelType type,
                                @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                LocalDateTime createdAt,
                                @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                LocalDateTime updatedAt) {
}
