package com.artinus.channelsubscription.channel.application.service;

import com.artinus.channelsubscription.channel.application.port.input.GetChannelHistoryUseCase;
import com.artinus.channelsubscription.channel.application.port.input.RegisterChannelCommand;
import com.artinus.channelsubscription.channel.application.port.input.RegisterChannelUseCase;
import com.artinus.channelsubscription.channel.application.port.output.LoadChannelPort;
import com.artinus.channelsubscription.channel.application.port.output.SaveChannelPort;
import com.artinus.channelsubscription.channel.domain.RegisteredChannel;
import com.artinus.channelsubscription.channel.domain.SaveChannel;
import com.artinus.channelsubscription.common.exception.CommonApplicationException;
import com.artinus.channelsubscription.subscription.domain.SubscriptionHistory;
import com.artinus.channelsubscription.subscription.adapter.persistence.SubscriptionJpaRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelService implements RegisterChannelUseCase, GetChannelHistoryUseCase {

    private final SubscriptionJpaRepository subscriptionRepository;

    private final LoadChannelPort loadChannelPort;
    private final SaveChannelPort saveChannelPort;

    @Transactional
    public RegisteredChannel registerChannel(@Valid RegisterChannelCommand command) {
        if (loadChannelPort.existsByName(command.name())) CommonApplicationException.CHANNEL_ALREADY_EXISTS.run();

        SaveChannel behavior = new SaveChannel(command.name(), command.type());
        return saveChannelPort.saveChannel(behavior);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionHistory> getChannelSubscriptionHistory(@NotNull Long channelId, LocalDate date) {
        if (loadChannelPort.existsById(channelId)) CommonApplicationException.CHANNEL_NOT_FOUND.run();
        return subscriptionRepository.findAllByChannelIdAndDate(channelId, date);
    }
}
