package com.artinus.channelsubscription.channel.adapter.persistence;

import com.artinus.channelsubscription.channel.application.port.output.LoadChannelPort;
import com.artinus.channelsubscription.channel.application.port.output.SaveChannelPort;
import com.artinus.channelsubscription.channel.domain.RegisteredChannel;
import com.artinus.channelsubscription.channel.domain.SaveChannel;
import com.artinus.channelsubscription.common.stereotype.PersistenceAdapter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * Output port adapter for channel persistence.
 */
@PersistenceAdapter
@RequiredArgsConstructor
public class ChannelPersistenceAdapter implements LoadChannelPort, SaveChannelPort {

    private final ChannelJpaRepository channelJpaRepository;

    private final ChannelMapper channelMapper;

    @Override
    public Optional<RegisteredChannel> findById(Long channelId) {
        return channelJpaRepository.findByIdAndAvailableTrue(channelId)
                .map(channelMapper::registeredChannel);
    }

    @Override
    public Boolean existsByName(final String name) {
        return channelJpaRepository.existsByName(name);
    }

    @Override
    public Boolean existsById(final Long channelId) {
        return channelJpaRepository.existsById(channelId);
    }

    @Override
    public RegisteredChannel saveChannel(SaveChannel behavior) {
        ChannelJpaEntity build = channelMapper.toEntity(behavior);
        ChannelJpaEntity savedChannel = channelJpaRepository.save(build);
        return channelMapper.registeredChannel(savedChannel);
    }
}
