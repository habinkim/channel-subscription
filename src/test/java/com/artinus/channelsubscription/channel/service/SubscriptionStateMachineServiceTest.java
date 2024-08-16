package com.artinus.channelsubscription.channel.service;

import com.artinus.channelsubscription.base.IntegrationTest;
import com.artinus.channelsubscription.channel.domain.ChannelType;
import com.artinus.channelsubscription.subscription.domain.SubscribeOperation;
import com.artinus.channelsubscription.subscription.domain.SubscriptionStatus;
import com.artinus.channelsubscription.subscription.domain.SubscriptionEvent;
import com.github.f4b6a3.ulid.UlidCreator;
import org.junit.jupiter.api.*;
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

    private static Message<SubscriptionEvent> buildMessage(SubscriptionEvent event, SubscribeOperation operation, ChannelType channelType) {
        return MessageBuilder.withPayload(event)
                .setHeader("operation", operation)
                .setHeader("channelType", channelType)
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("초기 상태는 'NONE' 이다.")
    void initialState() {
        SubscriptionStatus state = stateMachine.getState().getId();
        assertThat(state).isEqualTo(SubscriptionStatus.NONE);
    }

    @Nested
    @DisplayName("SubscribeOperation - SUBSCRIBE")
    class SubscribeOperationCase {

        @Test
        @Order(2)
        @DisplayName("Transition Success - NONE -> REGULAR")
        void subscribeRegularSuccess() throws Exception {
            Message<SubscriptionEvent> message = buildMessage(SubscriptionEvent.SUBSCRIBE_REGULAR,
                    SubscribeOperation.SUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                    StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                            .stateMachine(stateMachine)
                            .step()
                            .expectState(SubscriptionStatus.NONE)
                            .and()
                            .step()
                            .sendEvent(message)
                            .expectStateExited(SubscriptionStatus.NONE)
                            .expectState(SubscriptionStatus.REGULAR)
                            .and()
                            .build();

            plan.test();
        }

        @Test
        @Order(3)
        @DisplayName("Transition Success - NONE -> PREMIUM")
        void subscribePremiumSuccess() throws Exception {
            Message<SubscriptionEvent> message = buildMessage(SubscriptionEvent.SUBSCRIBE_PREMIUM,
                    SubscribeOperation.SUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                    StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                            .stateMachine(stateMachine)
                            .step()
                            .expectState(SubscriptionStatus.NONE)
                            .and()
                            .step()
                            .sendEvent(message)
                            .expectStateExited(SubscriptionStatus.NONE)
                            .expectState(SubscriptionStatus.PREMIUM)
                            .and()
                            .build();

            plan.test();
        }

        @Test
        @Order(4)
        @DisplayName("Transition Success - REGULAR -> PREMIUM")
        void subscribeRegularToPremiumSuccess() throws Exception {
            Message<SubscriptionEvent> step1Message = buildMessage(SubscriptionEvent.SUBSCRIBE_REGULAR,
                    SubscribeOperation.SUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            Message<SubscriptionEvent> step2Message = buildMessage(SubscriptionEvent.UPGRADE_TO_PREMIUM,
                    SubscribeOperation.SUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                    StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                            .stateMachine(stateMachine)
                            .step()
                            .expectState(SubscriptionStatus.NONE)
                            .and()
                            .step()
                            .sendEvent(step1Message)
                            .expectStateExited(SubscriptionStatus.NONE)
                            .expectState(SubscriptionStatus.REGULAR)
                            .and()
                            .step()
                            .sendEvent(step2Message)
                            .expectStateExited(SubscriptionStatus.REGULAR)
                            .expectState(SubscriptionStatus.PREMIUM)
                            .and()
                            .build();

            plan.test();
        }

        @Test
        @Order(5)
        @DisplayName("Transition Failed - PREMIUM -> REGULAR")
        void subscribePremiumToRegularFailed() throws Exception {
            Message<SubscriptionEvent> step1Message = buildMessage(SubscriptionEvent.SUBSCRIBE_PREMIUM,
                    SubscribeOperation.SUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            Message<SubscriptionEvent> step2Message = buildMessage(SubscriptionEvent.DOWNGRADE_TO_REGULAR,
                    SubscribeOperation.SUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                    StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                            .stateMachine(stateMachine)
                            .step()
                            .expectState(SubscriptionStatus.NONE)
                            .and()
                            .step()
                            .sendEvent(step1Message)
                            .expectStateExited(SubscriptionStatus.NONE)
                            .expectState(SubscriptionStatus.PREMIUM)
                            .and()
                            .step()
                            .sendEvent(step2Message)
                            .expectState(SubscriptionStatus.PREMIUM)
                            .expectStateChanged(0)
                            .and()
                            .build();

            plan.test();
        }

        @Test
        @Order(6)
        @DisplayName("Transition Failed - PREMIUM -> NONE")
        void subscribePremiumToNoneFailed() throws Exception {
            Message<SubscriptionEvent> step1Message = buildMessage(SubscriptionEvent.SUBSCRIBE_PREMIUM,
                    SubscribeOperation.SUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            Message<SubscriptionEvent> step2Message = buildMessage(SubscriptionEvent.CANCEL_SUBSCRIPTION,
                    SubscribeOperation.SUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                    StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                            .stateMachine(stateMachine)
                            .step()
                            .expectState(SubscriptionStatus.NONE)
                            .and()
                            .step()
                            .sendEvent(step1Message)
                            .expectStateExited(SubscriptionStatus.NONE)
                            .expectState(SubscriptionStatus.PREMIUM)
                            .and()
                            .step()
                            .sendEvent(step2Message)
                            .expectState(SubscriptionStatus.PREMIUM)
                            .expectStateChanged(0)
                            .and()
                            .build();

            plan.test();
        }

        @Test
        @Order(7)
        @DisplayName("Transition Failed - REGULAR -> NONE")
        void subscribeRegularToNoneFailed() throws Exception {
            Message<SubscriptionEvent> step1Message = buildMessage(SubscriptionEvent.SUBSCRIBE_REGULAR,
                    SubscribeOperation.SUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            Message<SubscriptionEvent> step2Message = buildMessage(SubscriptionEvent.CANCEL_SUBSCRIPTION,
                    SubscribeOperation.SUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                    StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                            .stateMachine(stateMachine)
                            .step()
                            .expectState(SubscriptionStatus.NONE)
                            .and()
                            .step()
                            .sendEvent(step1Message)
                            .expectStateExited(SubscriptionStatus.NONE)
                            .expectState(SubscriptionStatus.REGULAR)
                            .and()
                            .step()
                            .sendEvent(step2Message)
                            .expectState(SubscriptionStatus.REGULAR)
                            .expectStateChanged(0)
                            .and()
                            .build();

            plan.test();
        }

    }

    @Nested
    @DisplayName("SubscribeOperation - UNSUBSCRIBE")
    class UnsubscribeOperationCase {

        @Test
        @Order(8)
        @DisplayName("Transition Failed - NONE -> REGULAR")
        void subscribeRegularFailed() throws Exception {
            Message<SubscriptionEvent> message = buildMessage(SubscriptionEvent.SUBSCRIBE_REGULAR,
                    SubscribeOperation.UNSUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                    StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                            .stateMachine(stateMachine)
                            .step()
                            .expectState(SubscriptionStatus.NONE)
                            .and()
                            .step()
                            .sendEvent(message)
                            .expectState(SubscriptionStatus.NONE)
                            .expectStateChanged(0)
                            .and()
                            .build();

            plan.test();
        }

        @Test
        @Order(9)
        @DisplayName("Transition Failed - NONE -> PREMIUM")
        void subscribePremiumFailed() throws Exception {
            Message<SubscriptionEvent> message = buildMessage(SubscriptionEvent.SUBSCRIBE_PREMIUM,
                    SubscribeOperation.UNSUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                    StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                            .stateMachine(stateMachine)
                            .step()
                            .expectState(SubscriptionStatus.NONE)
                            .and()
                            .step()
                            .sendEvent(message)
                            .expectState(SubscriptionStatus.NONE)
                            .expectStateChanged(0)
                            .and()
                            .build();

            plan.test();
        }

        @Test
        @Order(10)
        @DisplayName("Transition Failed - REGULAR -> PREMIUM")
        void subscribeRegularToPremiumFailed() throws Exception {
            Message<SubscriptionEvent> step1Message = buildMessage(SubscriptionEvent.SUBSCRIBE_REGULAR,
                    SubscribeOperation.SUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            Message<SubscriptionEvent> step2Message = buildMessage(SubscriptionEvent.UPGRADE_TO_PREMIUM,
                    SubscribeOperation.UNSUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                    StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                            .stateMachine(stateMachine)
                            .step()
                            .expectState(SubscriptionStatus.NONE)
                            .and()
                            .step()
                            .sendEvent(step1Message)
                            .expectStateExited(SubscriptionStatus.NONE)
                            .expectState(SubscriptionStatus.REGULAR)
                            .and()
                            .step()
                            .sendEvent(step2Message)
                            .expectState(SubscriptionStatus.REGULAR)
                            .expectStateChanged(0)
                            .and()
                            .build();

            plan.test();
        }

        @Test
        @Order(11)
        @DisplayName("Transition Success - PREMIUM -> REGULAR")
        void subscribePremiumToRegularSuccess() throws Exception {
            Message<SubscriptionEvent> step1Message = buildMessage(SubscriptionEvent.SUBSCRIBE_PREMIUM,
                    SubscribeOperation.SUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            Message<SubscriptionEvent> step2Message = buildMessage(SubscriptionEvent.DOWNGRADE_TO_REGULAR,
                    SubscribeOperation.UNSUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                    StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                            .stateMachine(stateMachine)
                            .step()
                            .expectState(SubscriptionStatus.NONE)
                            .and()
                            .step()
                            .sendEvent(step1Message)
                            .expectStateExited(SubscriptionStatus.NONE)
                            .expectState(SubscriptionStatus.PREMIUM)
                            .and()
                            .step()
                            .sendEvent(step2Message)
                            .expectStateExited(SubscriptionStatus.PREMIUM)
                            .expectState(SubscriptionStatus.REGULAR)
                            .and()
                            .build();

            plan.test();
        }

        @Test
        @Order(12)
        @DisplayName("Transition Success - PREMIUM -> NONE")
        void subscribePremiumToNoneSuccess() throws Exception {
            Message<SubscriptionEvent> step1Message = buildMessage(SubscriptionEvent.SUBSCRIBE_PREMIUM,
                    SubscribeOperation.SUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            Message<SubscriptionEvent> step2Message = buildMessage(SubscriptionEvent.CANCEL_SUBSCRIPTION,
                    SubscribeOperation.UNSUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                    StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                            .stateMachine(stateMachine)
                            .step()
                            .expectState(SubscriptionStatus.NONE)
                            .and()
                            .step()
                            .sendEvent(step1Message)
                            .expectStateExited(SubscriptionStatus.NONE)
                            .expectState(SubscriptionStatus.PREMIUM)
                            .and()
                            .step()
                            .sendEvent(step2Message)
                            .expectStateExited(SubscriptionStatus.PREMIUM)
                            .expectState(SubscriptionStatus.NONE)
                            .and()
                            .build();

            plan.test();
        }

        @Test
        @Order(13)
        @DisplayName("Transition Success - REGULAR -> NONE")
        void subscribeRegularToNoneSuccess() throws Exception {
            Message<SubscriptionEvent> step1Message = buildMessage(SubscriptionEvent.SUBSCRIBE_REGULAR,
                    SubscribeOperation.SUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            Message<SubscriptionEvent> step2Message = buildMessage(SubscriptionEvent.CANCEL_SUBSCRIPTION,
                    SubscribeOperation.UNSUBSCRIBE, ChannelType.SUBSCRIBE_UNSUBSCRIBE);

            StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
                    StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
                            .stateMachine(stateMachine)
                            .step()
                            .expectState(SubscriptionStatus.NONE)
                            .and()
                            .step()
                            .sendEvent(step1Message)
                            .expectStateExited(SubscriptionStatus.NONE)
                            .expectState(SubscriptionStatus.REGULAR)
                            .and()
                            .step()
                            .sendEvent(step2Message)
                            .expectStateExited(SubscriptionStatus.REGULAR)
                            .expectState(SubscriptionStatus.NONE)
                            .and()
                            .build();

            plan.test();
        }

    }



//    @Test
//    @Order(8)
//    @DisplayName("Transition Success - NONE -> NONE")
//    void subscribeNoneToNone() throws Exception {
//        StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
//                StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
//                        .stateMachine(stateMachine)
//                        .step()
//                        .expectState(SubscriptionStatus.NONE)
//                        .and()
//                        .step()
//                        .sendEvent(SubscriptionEvent.INITIALIZE)
//                        .expectState(SubscriptionStatus.NONE)
//                        .expectStateChanged(0)
//                        .and()
//                        .build();
//
//        plan.test();
//    }

//    @Test
//    @Order(9)
//    @DisplayName("Transition Failed - REGULAR -> REGULAR")
//    void subscribeRegularToRegular() throws Exception {
//        StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
//                StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
//                        .stateMachine(stateMachine)
//                        .step()
//                        .expectState(SubscriptionStatus.NONE)
//                        .and()
//                        .step()
//                        .sendEvent(SubscriptionEvent.SUBSCRIBE_REGULAR)
//                        .expectStateExited(SubscriptionStatus.NONE)
//                        .expectState(SubscriptionStatus.REGULAR)
//                        .and()
//                        .step()
//                        .sendEvent(SubscriptionEvent.SUBSCRIBE_REGULAR)
//                        .expectStateChanged(0)
//                        .and()
//                        .build();
//
//        plan.test();
//    }
//
//    @Test
//    @Order(10)
//    @DisplayName("Transition Failed - PREMIUM -> PREMIUM")
//    void subscribePremiumToPremium() throws Exception {
//        StateMachineTestPlan<SubscriptionStatus, SubscriptionEvent> plan =
//                StateMachineTestPlanBuilder.<SubscriptionStatus, SubscriptionEvent>builder()
//                        .stateMachine(stateMachine)
//                        .step()
//                        .expectState(SubscriptionStatus.NONE)
//                        .and()
//                        .step()
//                        .sendEvent(SubscriptionEvent.SUBSCRIBE_PREMIUM)
//                        .expectStateExited(SubscriptionStatus.NONE)
//                        .expectState(SubscriptionStatus.PREMIUM)
//                        .and()
//                        .step()
//                        .sendEvent(SubscriptionEvent.SUBSCRIBE_PREMIUM)
//                        .expectStateChanged(0)
//                        .and()
//                        .build();
//
//        plan.test();
//    }

}
