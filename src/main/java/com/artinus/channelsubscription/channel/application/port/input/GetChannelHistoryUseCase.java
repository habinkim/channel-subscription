package com.artinus.channelsubscription.channel.application.port.input;

import com.artinus.channelsubscription.subscription.domain.SubscriptionHistory;

import java.time.LocalDate;
import java.util.List;

@FunctionalInterface
public interface GetChannelHistoryUseCase {

    List<SubscriptionHistory>  getChannelSubscriptionHistory(Long channelId, LocalDate date);

}
