package com.artinus.channelsubscription.channel.application.port.output;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface LoadChannelPort {

    Boolean existsByName(@NotBlank final String name);

    Boolean existsById(@NotNull final Long channelId);
}
