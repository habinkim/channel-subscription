package com.artinus.channelsubscription.common.config.state;

import com.artinus.channelsubscription.subscription.domain.SubscriptionEvent;
import com.artinus.channelsubscription.subscription.domain.SubscriptionStatus;
import org.slf4j.MDC;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Multi-threaded State Machine에서 MDC context를 유지하기 위한 Action
 */
@Component
public class MDCContextAction implements Action<SubscriptionStatus, SubscriptionEvent> {

    @Override
    public void execute(StateContext<SubscriptionStatus, SubscriptionEvent> context) {
        Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
        MDC.setContextMap(copyOfContextMap);
    }

}
