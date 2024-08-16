package com.artinus.channelsubscription.common.config.state;

import com.artinus.channelsubscription.subscription.domain.SubscriptionEvent;
import com.artinus.channelsubscription.subscription.domain.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

import static com.artinus.channelsubscription.subscription.domain.SubscriptionEvent.*;
import static com.artinus.channelsubscription.subscription.domain.SubscriptionStatus.*;

@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
@Slf4j
public class SubscriptionStateMachineConfig extends StateMachineConfigurerAdapter<SubscriptionStatus, SubscriptionEvent> {

    private final StateMachineListener<SubscriptionStatus, SubscriptionEvent> stateMachineListener;
    private final StateMachineRuntimePersister<SubscriptionStatus, SubscriptionEvent, String> stateMachineRuntimePersister;
    private final OperationGuard operationGuard;

    @Override
    public void configure(StateMachineStateConfigurer<SubscriptionStatus, SubscriptionEvent> states) throws Exception {
        states
                .withStates()
                .initial(NONE)
                .state(REGULAR)
                .state(PREMIUM);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<SubscriptionStatus, SubscriptionEvent> transitions) throws Exception {
        transitions
                .withInternal()
                .source(NONE).event(INITIALIZE).guard(operationGuard)
                .and()
                .withExternal()
                .source(NONE).target(REGULAR).event(SUBSCRIBE_REGULAR).guard(operationGuard)
                .and()
                .withExternal()
                .source(NONE).target(PREMIUM).event(SUBSCRIBE_PREMIUM).guard(operationGuard)
                .and()
                .withExternal()
                .source(REGULAR).target(PREMIUM).event(UPGRADE_TO_PREMIUM).guard(operationGuard)
                .and()
                .withExternal()
                .source(PREMIUM).target(REGULAR).event(DOWNGRADE_TO_REGULAR).guard(operationGuard)
                .and()
                .withExternal()
                .source(PREMIUM).target(NONE).event(CANCEL_SUBSCRIPTION).guard(operationGuard)
                .and()
                .withExternal()
                .source(REGULAR).target(NONE).event(CANCEL_SUBSCRIPTION).guard(operationGuard);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<SubscriptionStatus, SubscriptionEvent> config) throws Exception {
        config
                .withConfiguration()
                .autoStartup(true)
                .listener(stateMachineListener)
                .and()
                .withPersistence()
                .runtimePersister(stateMachineRuntimePersister);
    }

    /**
     * @param stateMachineFactory          State Machine 생성자
     * @param stateMachineRuntimePersister State Machine 영속성 관리자
     * @return
     * @see <a href="https://stackoverflow.com/questions/69506161/spring-statemachinefactory-getstatemachine-returns-currents-state-as-initial-sta">Spring StateMachineFactory getStateMachine returns currents state as initial state</a>
     * @see <a href="https://stackoverflow.com/questions/63984734/spring-state-machine-with-jpa-persistence-repository-usage/63998307#63998307">Spring state machine with JPA persistence- Repository usage</a>
     */
    @Bean
    public StateMachineService<SubscriptionStatus, SubscriptionEvent> stateMachineService(
            final StateMachineFactory<SubscriptionStatus, SubscriptionEvent> stateMachineFactory,
            final StateMachineRuntimePersister<SubscriptionStatus, SubscriptionEvent, String> stateMachineRuntimePersister) {
        return new DefaultStateMachineService<>(stateMachineFactory, stateMachineRuntimePersister);
    }


}
