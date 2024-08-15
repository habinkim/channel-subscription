package com.artinus.channelsubscription.channel.mapper;

import com.artinus.channelsubscription.channel.domain.RegisteredChannel;
import com.artinus.channelsubscription.channel.entity.Channel;
import com.artinus.channelsubscription.common.config.BaseMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public abstract class ChannelMapper {

    public abstract RegisteredChannel registeredChannel(Channel channel);

}
