package com.artinus.channelsubscription.subscription.adapter.persistence;

import com.artinus.channelsubscription.channel.adapter.persistence.ChannelJpaEntity;
import com.artinus.channelsubscription.common.config.BaseMapperConfig;
import com.artinus.channelsubscription.subscription.domain.RegisteredSubscription;
import com.artinus.channelsubscription.subscription.application.port.input.SubscribeCommand;
import com.artinus.channelsubscription.subscription.domain.SaveSubscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapperConfig.class)
public abstract  class SubscriptionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "account", source = "account")
    @Mapping(target = "channel", source = "channel")
    @Mapping(target = "previousSubscriptionStatus", source = "account.currentSubscriptionStatus")
    @Mapping(target = "subscriptionStatus", source = "behavior.subscriptionStatus")
    public abstract SubscriptionJpaEntity toEntity(SaveSubscription behavior, AccountJpaEntity account, ChannelJpaEntity channel);

    @Mapping(target = "subscriptionId", source = "subscription.id")
    @Mapping(target = "phoneNumber", source = "account.phoneNumber")
    @Mapping(target = "channelId", source = "channel.id")
    @Mapping(target = "channelName", source = "channel.name")
    @Mapping(target = "previousStatus", source = "subscription.previousSubscriptionStatus")
    @Mapping(target = "status", source = "subscription.subscriptionStatus")
    @Mapping(target = "createdAt", source = "subscription.createdAt")
    public abstract RegisteredSubscription registeredSubscription(SubscriptionJpaEntity subscription, AccountJpaEntity account, ChannelJpaEntity channel);

}
