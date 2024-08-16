package com.artinus.channelsubscription.channel.service;

import com.artinus.channelsubscription.channel.domain.RegisterChannelRequest;
import com.artinus.channelsubscription.channel.domain.RegisteredChannel;
import com.artinus.channelsubscription.channel.mapper.ChannelMapper;
import com.artinus.channelsubscription.channel.entity.Channel;
import com.artinus.channelsubscription.channel.repository.ChannelRepository;
import com.artinus.channelsubscription.common.exception.CommonApplicationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final ChannelMapper channelMapper;

    @Transactional
    public RegisteredChannel registerChannel(@Valid RegisterChannelRequest request) {
        channelRepository.findByNameAndAvailableTrue(request.name())
                .ifPresent(channel -> CommonApplicationException.CHANNEL_ALREADY_EXISTS.run());

        Channel channel = Channel.builder().name(request.name()).type(request.type()).build();
        Channel savedChannel = channelRepository.save(channel);

        return channelMapper.registeredChannel(savedChannel);
    }
}
