package com.artinus.channelsubscription.subscription.application.service;

import com.artinus.channelsubscription.channel.application.port.output.LoadChannelPort;
import com.artinus.channelsubscription.channel.domain.ChannelType;
import com.artinus.channelsubscription.channel.domain.RegisteredChannel;
import com.artinus.channelsubscription.common.exception.CommonApplicationException;
import com.artinus.channelsubscription.common.stereotype.UseCase;
import com.artinus.channelsubscription.subscription.application.port.input.GetSubscriptionHistoryUseCase;
import com.artinus.channelsubscription.subscription.application.port.input.SubscribeCommand;
import com.artinus.channelsubscription.subscription.application.port.input.SubscribeUseCase;
import com.artinus.channelsubscription.subscription.application.port.input.UnsubscribeUseCase;
import com.artinus.channelsubscription.subscription.application.port.output.*;
import com.artinus.channelsubscription.subscription.domain.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @see <a href="https://devloo.tistory.com/entry/Spring-State-Machine-%EC%9D%84-%EC%96%B4%EB%96%BB%EA%B2%8C-%ED%99%9C%EC%9A%A9%ED%95%A0-%EC%88%98-%EC%9E%88%EC%9D%84%EA%B9%8C%EC%9A%94-%EC%82%AC%EC%9A%A9-%EB%B0%A9%EB%B2%95-%EB%B0%8F-%EC%98%88%EC%A0%9C">Spring State Machine을 어떻게 활용할 수 있을까요?</a>
 * @see <a href="https://medium.com/@alishazy/spring-statemachine-a-comprehensive-guide-31dc346a600d">Spring Statemachine: A Comprehensive Guide</a>
 */
@UseCase
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService implements SubscribeUseCase, UnsubscribeUseCase, GetSubscriptionHistoryUseCase {

    private final LoadChannelPort loadChannelPort;

    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;

    private final LoadSubscriptionPort loadSubscriptionPort;
    private final SaveSubscriptionPort saveSubscriptionPort;

    private final AttemptTransitionPort attemptTransitionPort;

    @Transactional
    public RegisteredSubscription subscribe(@Valid SubscribeCommand command) {
        AtomicReference<Boolean> isNewAccount = new AtomicReference<>(false);

        // ChannelJpaEntity 조회
        RegisteredChannel channel = loadChannelPort.findById(command.channelId())
                .orElseThrow(CommonApplicationException.CHANNEL_NOT_FOUND);

        // 휴대폰 번호에 해당하는 회원 조회 및 없으면 신규 생성
        RegisteredAccount account = loadAccountPort.findByPhoneNumber(command.phoneNumber()).orElseGet(() -> {
            isNewAccount.set(true);
            return createNewAccount(command);
        });

        // 신규 회원이 아니면서 현재 구독 상태가 '구독 안함' 이고, 요청한 구독 상태도 '구독 안함'이면 거부
        if (!isNewAccount.get() &&
                SubscriptionStatus.NONE.equals(account.currentSubscriptionStatus()) &&
                SubscriptionStatus.NONE.equals(command.operation())) {
            log.error("SubscriptionJpaEntity transition denied. AccountJpaEntity: {}, Current SubscriptionJpaEntity Status: {}, Requested SubscriptionJpaEntity Status: {}",
                    account.phoneNumber(), account.currentSubscriptionStatus(), command.operation());
            throw CommonApplicationException.SUBSCRIPTION_TRANSITION_DENIED;
        }

        // State Machine에서 사용할 이벤트 생성
        SubscriptionEvent subscriptionEvent = getEvent(account.currentSubscriptionStatus(), command.operation(), channel.type());

        AttemptTransitionSubscriptionStatus transitionBehavior = new AttemptTransitionSubscriptionStatus(account.id(),
                account.phoneNumber(), channel.type(), subscriptionEvent, SubscribeOperation.SUBSCRIBE);
        StateMachineEventResult<SubscriptionStatus, SubscriptionEvent> stateMachineEventResult =
                attemptTransitionPort.transitionSubscriptionStatus(transitionBehavior);

        assert stateMachineEventResult != null;
        StateMachineEventResult.ResultType resultType = stateMachineEventResult.getResultType();

        // State Machine에서 거부되면 예외 발생
        if (StateMachineEventResult.ResultType.DENIED.equals(resultType))
            throw CommonApplicationException.SUBSCRIPTION_TRANSITION_DENIED;

        // State Machine에서 성공하면 DB에 구독 정보 저장
        SaveSubscription behavior = new SaveSubscription(account.phoneNumber(), channel.id(),
                account.currentSubscriptionStatus(), command.operation());
        RegisteredSubscription registeredSubscription = saveSubscriptionPort.saveSubscription(behavior);

        // 회원의 현재 구독 상태 업데이트
        saveAccountPort.updateCurrentSubscriptionStatus(command.phoneNumber(), command.operation());

        return registeredSubscription;
    }

    @Transactional
    public RegisteredSubscription unsubscribe(@Valid SubscribeCommand command) {
        // ChannelJpaEntity 조회
        RegisteredChannel channel = loadChannelPort.findById(command.channelId())
                .orElseThrow(CommonApplicationException.CHANNEL_NOT_FOUND);

        // 휴대폰 번호에 해당하는 회원 조회 및 없으면 예외 발생
        RegisteredAccount account = loadAccountPort.findByPhoneNumber(command.phoneNumber())
                .orElseThrow(CommonApplicationException.ACCOUNT_NOT_FOUND);

        // State Machine에서 사용할 이벤트 생성
        SubscriptionEvent subscriptionEvent = getEvent(account.currentSubscriptionStatus(), command.operation(), channel.type());

        AttemptTransitionSubscriptionStatus transitionBehavior = new AttemptTransitionSubscriptionStatus(account.id(),
                account.phoneNumber(), channel.type(), subscriptionEvent, SubscribeOperation.UNSUBSCRIBE);
        StateMachineEventResult<SubscriptionStatus, SubscriptionEvent> stateMachineEventResult =
                attemptTransitionPort.transitionSubscriptionStatus(transitionBehavior);

        assert stateMachineEventResult != null;
        StateMachineEventResult.ResultType resultType = stateMachineEventResult.getResultType();

        // State Machine에서 거부되면 예외 발생
        if (StateMachineEventResult.ResultType.DENIED.equals(resultType))
            throw CommonApplicationException.SUBSCRIPTION_TRANSITION_DENIED;

        // State Machine에서 성공하면 DB에 구독 정보 저장
        SaveSubscription behavior = new SaveSubscription(account.phoneNumber(), channel.id(),
                account.currentSubscriptionStatus(), command.operation());
        RegisteredSubscription registeredSubscription = saveSubscriptionPort.saveSubscription(behavior);

        // 회원의 현재 구독 상태 업데이트
        saveAccountPort.updateCurrentSubscriptionStatus(command.phoneNumber(), command.operation());

        return registeredSubscription;
    }

    @Transactional(readOnly = true)
    public Map<String, List<SubscriptionHistory>> getSubscriptionHistory(@NotBlank String phoneNumber) {
        return loadSubscriptionPort.getSubscriptionHistory(phoneNumber);
    }

    @Transactional
    public RegisteredAccount createNewAccount(SubscribeCommand command) {
        return saveAccountPort.createAccount(command.phoneNumber());
    }

    private static SubscriptionEvent getEvent(final SubscriptionStatus previousStatus, final SubscriptionStatus nextStatus, final ChannelType channelType) {
        return SubscriptionEvent.from(previousStatus, nextStatus, channelType);
    }

}
