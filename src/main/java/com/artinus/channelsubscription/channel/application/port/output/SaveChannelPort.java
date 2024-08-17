package com.artinus.channelsubscription.channel.application.port.output;

import com.artinus.channelsubscription.channel.domain.RegisteredChannel;
import com.artinus.channelsubscription.channel.domain.SaveChannel;

public interface SaveChannelPort {
    RegisteredChannel saveChannel(SaveChannel behavior);
}
