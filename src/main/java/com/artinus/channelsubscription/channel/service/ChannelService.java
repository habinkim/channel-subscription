package com.artinus.channelsubscription.channel.service;

import com.artinus.channelsubscription.channel.adapter.persistence.ChannelJpaEntity;
import com.artinus.channelsubscription.channel.domain.RegisterChannelRequest;
import com.artinus.channelsubscription.channel.domain.RegisteredChannel;
import com.artinus.channelsubscription.channel.adapter.persistence.ChannelMapper;
import com.artinus.channelsubscription.channel.adapter.persistence.ChannelJpaRepository;
import com.artinus.channelsubscription.common.exception.CommonApplicationException;
import com.artinus.channelsubscription.subscription.domain.SubscriptionHistory;
import com.artinus.channelsubscription.subscription.repository.SubscriptionRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelJpaRepository channelJpaRepository;
    private final SubscriptionRepository subscriptionRepository;

    private final ChannelMapper channelMapper;

    @Transactional
    public RegisteredChannel registerChannel(@Valid RegisterChannelRequest request) {
        channelJpaRepository.findByNameAndAvailableTrue(request.name())
                .ifPresent(channel -> CommonApplicationException.CHANNEL_ALREADY_EXISTS.run());

        ChannelJpaEntity channel = ChannelJpaEntity.builder().name(request.name()).type(request.type()).build();
        ChannelJpaEntity savedChannel = channelJpaRepository.save(channel);

        return channelMapper.registeredChannel(savedChannel);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionHistory> getChannelSubscriptionHistory(@NotNull Long channelId, LocalDate date) {
        channelJpaRepository.findByIdAndAvailableTrue(channelId).orElseThrow(CommonApplicationException.CHANNEL_NOT_FOUND);
        return subscriptionRepository.findAllByChannelIdAndDate(channelId, date);
    }
}
