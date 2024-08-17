package com.artinus.channelsubscription.channel.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SaveChannel(@NotBlank String name, @NotNull ChannelType type) {
}
