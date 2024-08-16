package com.artinus.channelsubscription.common.config.state;

import com.artinus.channelsubscription.subscription.domain.SubscribeOperation;
import com.artinus.channelsubscription.subscription.domain.SubscriptionEvent;
import com.artinus.channelsubscription.subscription.domain.SubscriptionStatus;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Service;

/**
 * 구독하기 API와 구독해지 API의 권한을 검사하는 Guard
 */
@Service
public class OperationGuard implements Guard<SubscriptionStatus, SubscriptionEvent> {

    @Override
    public boolean evaluate(StateContext<SubscriptionStatus, SubscriptionEvent> context) {
        SubscribeOperation operation = SubscribeOperation.valueOf(context.getMessageHeader("operation").toString());

        return switch (operation) {
            case SUBSCRIBE -> SubscriptionEvent.INITIALIZE.equals(context.getEvent()) ||
                    SubscriptionEvent.SUBSCRIBE_REGULAR.equals(context.getEvent()) ||
                    SubscriptionEvent.SUBSCRIBE_PREMIUM.equals(context.getEvent()) ||
                    SubscriptionEvent.UPGRADE_TO_PREMIUM.equals(context.getEvent());
            case UNSUBSCRIBE -> SubscriptionEvent.DOWNGRADE_TO_REGULAR.equals(context.getEvent()) ||
                    SubscriptionEvent.CANCEL_SUBSCRIPTION.equals(context.getEvent());
        };

    }

}
