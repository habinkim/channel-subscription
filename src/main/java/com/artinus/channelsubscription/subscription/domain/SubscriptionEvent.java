package com.artinus.channelsubscription.subscription.domain;

import com.artinus.channelsubscription.channel.domain.ChannelType;
import com.artinus.channelsubscription.common.exception.CommonApplicationException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

import static com.artinus.channelsubscription.channel.domain.ChannelType.*;
import static com.artinus.channelsubscription.subscription.domain.SubscriptionStatus.*;
import static java.util.Set.of;

@Getter
@AllArgsConstructor
public enum SubscriptionEvent {

    INITIALIZE(of(NONE), NONE, of(SUBSCRIBE_UNSUBSCRIBE, SUBSCRIBE_ONLY)),
    SUBSCRIBE_REGULAR(of(NONE), REGULAR, of(SUBSCRIBE_UNSUBSCRIBE, SUBSCRIBE_ONLY)),
    SUBSCRIBE_PREMIUM(of(NONE), PREMIUM, of(SUBSCRIBE_UNSUBSCRIBE, SUBSCRIBE_ONLY)),
    UPGRADE_TO_PREMIUM(of(REGULAR), PREMIUM, of(SUBSCRIBE_UNSUBSCRIBE, SUBSCRIBE_ONLY)),
    DOWNGRADE_TO_REGULAR(of(PREMIUM), REGULAR, of(SUBSCRIBE_UNSUBSCRIBE, UNSUBSCRIBE_ONLY)),
    CANCEL_SUBSCRIPTION(of(PREMIUM, REGULAR), NONE, of(SUBSCRIBE_UNSUBSCRIBE, UNSUBSCRIBE_ONLY));

    private final Set<SubscriptionStatus> previousStatuses;
    private final SubscriptionStatus nextStatus;
    private final Set<ChannelType> availableChannelTypes;

    public static SubscriptionEvent from(SubscriptionStatus previousStatus, SubscriptionStatus nextStatus, ChannelType channelType) {
        for (SubscriptionEvent event : values())
            if (event.getPreviousStatuses().contains(previousStatus) &&
                    event.getNextStatus().equals(nextStatus) &&
                    event.getAvailableChannelTypes().contains(channelType))
                return event;

        throw CommonApplicationException.INVALID_STATUS_TRANSITION;
    }

}
