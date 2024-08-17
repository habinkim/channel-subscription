package com.artinus.channelsubscription.channel.adapter.persistence;

public interface ChannelJpaRepositoryCustom {

    Boolean existsByName(final String name);

    Boolean existsByChannelId(final Long channelId);

}
