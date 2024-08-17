package com.artinus.channelsubscription.channel.application.port.input;

import com.artinus.channelsubscription.channel.domain.RegisterChannelCommand;
import com.artinus.channelsubscription.channel.domain.RegisteredChannel;

@FunctionalInterface
public interface RegisterChannelUseCase {

    RegisteredChannel registerChannel(final RegisterChannelCommand command);

}
