package com.artinus.channelsubscription.common.config;

import com.artinus.channelsubscription.subscription.entity.SubscriptionStatus;
import com.artinus.channelsubscription.subscription.service.SubscriptionEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachine
public class SubscriptionStateMachineConfig extends StateMachineConfigurerAdapter<SubscriptionStatus, SubscriptionEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<SubscriptionStatus, SubscriptionEvent> states) throws Exception {
        states
                .withStates()
                .initial(SubscriptionStatus.NONE)
                .state(SubscriptionStatus.REGULAR)
                .state(SubscriptionStatus.PREMIUM);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<SubscriptionStatus, SubscriptionEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(SubscriptionStatus.NONE).target(SubscriptionStatus.REGULAR).event(SubscriptionEvent.SUBSCRIBE_REGULAR)
                .and()
                .withExternal()
                .source(SubscriptionStatus.NONE).target(SubscriptionStatus.PREMIUM).event(SubscriptionEvent.SUBSCRIBE_PREMIUM)
                .and()
                .withExternal()
                .source(SubscriptionStatus.REGULAR).target(SubscriptionStatus.PREMIUM).event(SubscriptionEvent.UPGRADE_TO_PREMIUM)
                .and()
                .withExternal()
                .source(SubscriptionStatus.PREMIUM).target(SubscriptionStatus.REGULAR).event(SubscriptionEvent.DOWNGRADE_TO_REGULAR)
                .and()
                .withExternal()
                .source(SubscriptionStatus.PREMIUM).target(SubscriptionStatus.NONE).event(SubscriptionEvent.CANCEL_SUBSCRIPTION)
                .and()
                .withExternal()
                .source(SubscriptionStatus.REGULAR).target(SubscriptionStatus.NONE).event(SubscriptionEvent.CANCEL_SUBSCRIPTION);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<SubscriptionStatus, SubscriptionEvent> config) throws Exception {
        config
                .withConfiguration()
                .autoStartup(true)
                .machineId("subscriptionStateMachine");
    }


}
