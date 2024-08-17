package com.artinus.channelsubscription.channel.application.port.output;

import jakarta.validation.constraints.NotBlank;

public interface LoadChannelPort {

    Boolean existsByName(@NotBlank String name);

}
