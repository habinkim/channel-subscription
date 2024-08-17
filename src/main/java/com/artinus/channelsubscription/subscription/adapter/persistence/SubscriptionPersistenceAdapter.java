package com.artinus.channelsubscription.subscription.adapter.persistence;

import com.artinus.channelsubscription.channel.adapter.persistence.ChannelJpaEntity;
import com.artinus.channelsubscription.channel.adapter.persistence.ChannelPersistenceAdapter;
import com.artinus.channelsubscription.common.stereotype.PersistenceAdapter;
import com.artinus.channelsubscription.subscription.application.port.output.LoadSubscriptionPort;
import com.artinus.channelsubscription.subscription.application.port.output.SaveSubscriptionPort;
import com.artinus.channelsubscription.subscription.domain.RegisteredSubscription;
import com.artinus.channelsubscription.subscription.domain.SaveSubscription;
import com.artinus.channelsubscription.subscription.domain.SubscriptionHistory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@PersistenceAdapter
@RequiredArgsConstructor
public class SubscriptionPersistenceAdapter implements SaveSubscriptionPort, LoadSubscriptionPort {

    private final SubscriptionJpaRepository subscriptionJpaRepository;

    private final ChannelPersistenceAdapter channelPersistenceAdapter;
    private final AccountPersistenceAdapter accountPersistenceAdapter;

    private final SubscriptionMapper subscriptionMapper;

    @Override
    public RegisteredSubscription saveSubscription(SaveSubscription behavior) {
        AccountJpaEntity accountJpaEntity = accountPersistenceAdapter.findEntityByPhoneNumber(behavior.phoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        ChannelJpaEntity channelJpaEntity = channelPersistenceAdapter.findByAndAvailableTrue(behavior.channelId())
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));

        SubscriptionJpaEntity build = subscriptionMapper.toEntity(behavior, accountJpaEntity, channelJpaEntity);
        SubscriptionJpaEntity saved = subscriptionJpaRepository.save(build);

        return subscriptionMapper.registeredSubscription(saved, accountJpaEntity, channelJpaEntity);
    }

    @Override
    public Map<String, List<SubscriptionHistory>> getSubscriptionHistory(final String phoneNumber) {
        return subscriptionJpaRepository.findAllByPhoneNumber(phoneNumber);
    }

    @Override
    public List<SubscriptionHistory> findAllByChannelIdAndDate(Long channelId, LocalDate date) {
        return subscriptionJpaRepository.findAllByChannelIdAndDate(channelId, date);
    }
}
