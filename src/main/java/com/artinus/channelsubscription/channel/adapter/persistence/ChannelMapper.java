package com.artinus.channelsubscription.channel.adapter.persistence;

import com.artinus.channelsubscription.channel.domain.RegisteredChannel;
import com.artinus.channelsubscription.channel.domain.SaveChannel;
import com.artinus.channelsubscription.common.config.BaseMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public abstract class ChannelMapper {

    public abstract RegisteredChannel registeredChannel(ChannelJpaEntity channel);
    
    public abstract ChannelJpaEntity toEntity(SaveChannel behavior);

}
