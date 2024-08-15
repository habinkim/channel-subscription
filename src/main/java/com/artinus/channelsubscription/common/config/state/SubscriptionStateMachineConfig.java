package com.artinus.channelsubscription.common.config.state;

import com.artinus.channelsubscription.subscription.entity.SubscriptionStatus;
import com.artinus.channelsubscription.subscription.service.SubscriptionEvent;
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

@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
@Slf4j
public class SubscriptionStateMachineConfig extends StateMachineConfigurerAdapter<SubscriptionStatus, SubscriptionEvent> {

    private final StateMachineListener<SubscriptionStatus, SubscriptionEvent> stateMachineListener;
    private final StateMachineRuntimePersister<SubscriptionStatus, SubscriptionEvent, String> stateMachineRuntimePersister;

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
                .listener(stateMachineListener)
                .and()
                .withPersistence()
                .runtimePersister(stateMachineRuntimePersister);
    }

    /**
     * @see <a href="https://stackoverflow.com/questions/69506161/spring-statemachinefactory-getstatemachine-returns-currents-state-as-initial-sta">Spring StateMachineFactory getStateMachine returns currents state as initial state</a>
     * @see <a href="https://stackoverflow.com/questions/63984734/spring-state-machine-with-jpa-persistence-repository-usage/63998307#63998307">Spring state machine with JPA persistence- Repository usage</a>
     * @param stateMachineFactory State Machine 생성자
     * @param stateMachineRuntimePersister State Machine 영속성 관리자
     * @return
     */
    @Bean
    public StateMachineService<SubscriptionStatus, SubscriptionEvent> stateMachineService(
            final StateMachineFactory<SubscriptionStatus, SubscriptionEvent> stateMachineFactory,
            final StateMachineRuntimePersister<SubscriptionStatus, SubscriptionEvent, String> stateMachineRuntimePersister) {
        return new DefaultStateMachineService<>(stateMachineFactory, stateMachineRuntimePersister);
    }


}
