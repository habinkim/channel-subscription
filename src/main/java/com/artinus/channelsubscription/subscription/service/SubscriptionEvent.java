package com.artinus.channelsubscription.subscription.service;

import com.artinus.channelsubscription.subscription.entity.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

import static com.artinus.channelsubscription.subscription.entity.SubscriptionStatus.*;
import static java.util.Set.of;

@Getter
@AllArgsConstructor
public enum SubscriptionEvent {

    SUBSCRIBE_REGULAR(of(NONE), REGULAR),
    SUBSCRIBE_PREMIUM(of(NONE), PREMIUM),
    UPGRADE_TO_PREMIUM(of(REGULAR), PREMIUM),
    DOWNGRADE_TO_REGULAR(of(PREMIUM), REGULAR),
    CANCEL_SUBSCRIPTION(of(PREMIUM, REGULAR), NONE);

    private final Set<SubscriptionStatus> previousStatuses;
    private final SubscriptionStatus nextStatus;

    public static SubscriptionEvent from(SubscriptionStatus previousStatus, SubscriptionStatus nextStatus) {
        for (SubscriptionEvent event : values())
            if (event.getPreviousStatuses().contains(previousStatus) && event.getNextStatus() == nextStatus)
                return event;

        throw new IllegalArgumentException("Invalid status transition");
    }

}
