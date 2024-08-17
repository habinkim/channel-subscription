package com.artinus.channelsubscription.channel.adapter.persistence;

import com.artinus.channelsubscription.channel.application.port.output.LoadChannelPort;
import com.artinus.channelsubscription.common.stereotype.PersistenceAdapter;
import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class ChannelPersistenceAdapter implements LoadChannelPort {

    private final ChannelJpaRepository channelJpaRepository;

    @Override
    public Boolean existsByName(final String name) {
        return channelJpaRepository.existsByName(name);
    }
}
