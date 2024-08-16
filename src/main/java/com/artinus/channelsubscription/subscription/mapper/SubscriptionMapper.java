package com.artinus.channelsubscription.subscription.mapper;

import com.artinus.channelsubscription.channel.entity.Channel;
import com.artinus.channelsubscription.common.config.BaseMapperConfig;
import com.artinus.channelsubscription.subscription.domain.RegisteredSubscription;
import com.artinus.channelsubscription.subscription.entity.Account;
import com.artinus.channelsubscription.subscription.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapperConfig.class)
public abstract  class SubscriptionMapper {

    @Mapping(target = "subscriptionId", source = "subscription.id")
    @Mapping(target = "phoneNumber", source = "account.phoneNumber")
    @Mapping(target = "channelId", source = "channel.id")
    @Mapping(target = "channelName", source = "channel.name")
    @Mapping(target = "previousSubscriptionStatus", source = "subscription.previousSubscriptionStatus")
    @Mapping(target = "subscriptionStatus", source = "subscription.subscriptionStatus")
    public abstract RegisteredSubscription registeredSubscription(Subscription subscription, Account account, Channel channel);

}
