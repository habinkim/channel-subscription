package com.artinus.channelsubscription.channel.application.port.output;

import com.artinus.channelsubscription.channel.domain.RegisteredChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public interface LoadChannelPort {

    Optional<RegisteredChannel> findById(@NotNull final Long channelId);

    Boolean existsByName(@NotBlank final String name);

    Boolean existsById(@NotNull final Long channelId);
}
