package com.artinus.channelsubscription.common.config.state;

import com.artinus.channelsubscription.subscription.entity.SubscriptionStatus;
import com.artinus.channelsubscription.subscription.service.SubscriptionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

@Configuration
@Slf4j
public class StateMachineListenerConfig {

    @Bean
    public StateMachineListener<SubscriptionStatus, SubscriptionEvent> stateMachineListener() {
        return new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<SubscriptionStatus, SubscriptionEvent> from, State<SubscriptionStatus, SubscriptionEvent> to) {
                log.info("Transitioned from {} to {}", from == null ? "none" : from.getId(), to.getId());
            }

            @Override
            public void stateEntered(State<SubscriptionStatus, SubscriptionEvent> state) {
                log.info("Entered state: {}", state.getId());
            }

            @Override
            public void stateExited(State<SubscriptionStatus, SubscriptionEvent> state) {
                log.info("Exited state: {}", state.getId());
            }

            @Override
            public void eventNotAccepted(Message<SubscriptionEvent> event) {
                log.info("Event not accepted: {}", event.getPayload());
//                throw CommonApplicationException.ERROR;
            }

            @Override
            public void transition(Transition<SubscriptionStatus, SubscriptionEvent> transition) {
                log.info("Transition: {} -> {}",
                        transition.getSource() != null ? transition.getSource().getId() : transition.getSource(),
                        transition.getTarget().getId());
            }

            @Override
            public void stateMachineError(StateMachine<SubscriptionStatus, SubscriptionEvent> stateMachine, Exception exception) {
                log.info("stateMachineError: {}", stateMachine.getId(), exception);
            }

            @Override
            public void stateContext(StateContext<SubscriptionStatus, SubscriptionEvent> stateContext) {
                log.info("stateContent: {}", stateContext);
            }
        };
    }

}
