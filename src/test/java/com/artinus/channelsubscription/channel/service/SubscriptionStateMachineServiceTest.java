package com.artinus.channelsubscription.channel.service;

import com.artinus.channelsubscription.base.IntegrationTest;
import com.artinus.channelsubscription.subscription.domain.SubscriptionStatus;
import com.artinus.channelsubscription.subscription.domain.SubscriptionEvent;
import com.github.f4b6a3.ulid.UlidCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class SubscriptionStateMachineServiceTest {

    @Autowired
    private StateMachineService<SubscriptionStatus, SubscriptionEvent> stateMachineService;

    StateMachine<SubscriptionStatus, SubscriptionEvent> stateMachine;

    @BeforeEach
    void setUp() {
        stateMachine = stateMachineService.acquireStateMachine(UlidCreator.getMonotonicUlid().toString());
    }

//    private static Message<SubscriptionEvent> buildMessage(SubscriptionEvent event) {
//        return MessageBuilder.withPayload(event)
//                .build();
//    }

    @Test
    @Order(1)
    @DisplayName("초기 상태는 'NONE' 이다.")
    void initialState() {
        SubscriptionStatus state = stateMachine.getState().getId();
        assertThat(state).isEqualTo(SubscriptionStatus.NONE);
    }

    @Test
    @Order(2)
    @DisplayName("Transition Success - NONE -> REGULAR")
    void subscribeRegular() throws Exception {
        StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .expectState(SubscriptionStatus.NONE)
                        .and()
                        .step()
                        .sendEvent(SubscriptionEvent.SUBSCRIBE_REGULAR)
                        .expectStateExited(SubscriptionStatus.NONE)
                        .expectState(SubscriptionStatus.REGULAR)
                        .and()
                        .build();

        plan.test();
    }

    @Test
    @Order(3)
    @DisplayName("Transition Success - NONE -> PREMIUM")
    void subscribePremium() throws Exception {
        StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .expectState(SubscriptionStatus.NONE)
                        .and()
                        .step()
                        .sendEvent(SubscriptionEvent.SUBSCRIBE_PREMIUM)
                        .expectStateExited(SubscriptionStatus.NONE)
                        .expectState(SubscriptionStatus.PREMIUM)
                        .and()
                        .build();

        plan.test();
    }

    @Test
    @Order(4)
    @DisplayName("Transition Success - REGULAR -> PREMIUM")
    void subscribeRegularToPremium() throws Exception {
        StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .expectState(SubscriptionStatus.NONE)
                        .and()
                        .step()
                        .sendEvent(SubscriptionEvent.SUBSCRIBE_REGULAR)
                        .expectStateExited(SubscriptionStatus.NONE)
                        .expectState(SubscriptionStatus.REGULAR)
                        .and()
                        .step()
                        .sendEvent(SubscriptionEvent.UPGRADE_TO_PREMIUM)
                        .expectStateExited(SubscriptionStatus.REGULAR)
                        .expectState(SubscriptionStatus.PREMIUM)
                        .and()
                        .build();

        plan.test();
    }

    @Test
    @Order(5)
    @DisplayName("Transition Success - PREMIUM -> REGULAR")
    void subscribePremiumToRegular() throws Exception {
        StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .expectState(SubscriptionStatus.NONE)
                        .and()
                        .step()
                        .sendEvent(SubscriptionEvent.SUBSCRIBE_PREMIUM)
                        .expectStateExited(SubscriptionStatus.NONE)
                        .expectState(SubscriptionStatus.PREMIUM)
                        .and()
                        .step()
                        .sendEvent(SubscriptionEvent.DOWNGRADE_TO_REGULAR)
                        .expectStateExited(SubscriptionStatus.PREMIUM)
                        .expectState(SubscriptionStatus.REGULAR)
                        .and()
                        .build();

        plan.test();
    }

    @Test
    @Order(6)
    @DisplayName("Transition Success - PREMIUM -> NONE")
    void subscribePremiumToNone() throws Exception {
        StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .expectState(SubscriptionStatus.NONE)
                        .and()
                        .step()
                        .sendEvent(SubscriptionEvent.SUBSCRIBE_PREMIUM)
                        .expectStateExited(SubscriptionStatus.NONE)
                        .expectState(SubscriptionStatus.PREMIUM)
                        .and()
                        .step()
                        .sendEvent(SubscriptionEvent.CANCEL_SUBSCRIPTION)
                        .expectStateExited(SubscriptionStatus.PREMIUM)
                        .expectState(SubscriptionStatus.NONE)
                        .and()
                        .build();

        plan.test();
    }

    @Test
    @Order(7)
    @DisplayName("Transition Success - REGULAR -> NONE")
    void subscribeRegularToNone() throws Exception {
        StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .expectState(SubscriptionStatus.NONE)
                        .and()
                        .step()
                        .sendEvent(SubscriptionEvent.SUBSCRIBE_REGULAR)
                        .expectStateExited(SubscriptionStatus.NONE)
                        .expectState(SubscriptionStatus.REGULAR)
                        .and()
                        .step()
                        .sendEvent(SubscriptionEvent.CANCEL_SUBSCRIPTION)
                        .expectStateExited(SubscriptionStatus.REGULAR)
                        .expectState(SubscriptionStatus.NONE)
                        .and()
                        .build();

        plan.test();
    }

    @Test
    @Order(8)
    @DisplayName("Transition Success - NONE -> NONE")
    void subscribeNoneToNone() throws Exception {
        StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .expectState(SubscriptionStatus.NONE)
                        .and()
                        .step()
                        .sendEvent(SubscriptionEvent.INITIALIZE)
                        .expectState(SubscriptionStatus.NONE)
                        .expectStateChanged(0)
                        .and()
                        .build();

        plan.test();
    }

    @Test
    @Order(9)
    @DisplayName("Transition Failed - REGULAR -> REGULAR")
    void subscribeRegularToRegular() throws Exception {
        StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .expectState(SubscriptionStatus.NONE)
                        .and()
                        .step()
                        .sendEvent(SubscriptionEvent.SUBSCRIBE_REGULAR)
                        .expectStateExited(SubscriptionStatus.NONE)
                        .expectState(SubscriptionStatus.REGULAR)
                        .and()
                        .step()
                        .sendEvent(SubscriptionEvent.SUBSCRIBE_REGULAR)
                        .expectStateChanged(0)
                        .and()
                        .build();

        plan.test();
    }

    @Test
    @Order(10)
    @DisplayName("Transition Failed - PREMIUM -> PREMIUM")
    void subscribePremiumToPremium() throws Exception {
        StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .expectState(SubscriptionStatus.NONE)
                        .and()
                        .step()
                        .sendEvent(SubscriptionEvent.SUBSCRIBE_PREMIUM)
                        .expectStateExited(SubscriptionStatus.NONE)
                        .expectState(SubscriptionStatus.PREMIUM)
                        .and()
                        .step()
                        .sendEvent(SubscriptionEvent.SUBSCRIBE_PREMIUM)
                        .expectStateChanged(0)
                        .and()
                        .build();

        plan.test();
    }

}
