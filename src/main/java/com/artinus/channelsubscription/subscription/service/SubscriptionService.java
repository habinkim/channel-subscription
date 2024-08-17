package com.artinus.channelsubscription.subscription.service;

import com.artinus.channelsubscription.channel.adapter.persistence.ChannelJpaEntity;
import com.artinus.channelsubscription.channel.domain.ChannelType;
import com.artinus.channelsubscription.channel.adapter.persistence.ChannelJpaRepository;
import com.artinus.channelsubscription.common.exception.CommonApplicationException;
import com.artinus.channelsubscription.subscription.adapter.persistence.AccountJpaEntity;
import com.artinus.channelsubscription.subscription.adapter.persistence.SubscriptionJpaEntity;
import com.artinus.channelsubscription.subscription.domain.*;
import com.artinus.channelsubscription.subscription.adapter.persistence.SubscriptionMapper;
import com.artinus.channelsubscription.subscription.adapter.persistence.AccountJpaRepository;
import com.artinus.channelsubscription.subscription.adapter.persistence.SubscriptionJpaRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @see <a href="https://devloo.tistory.com/entry/Spring-State-Machine-%EC%9D%84-%EC%96%B4%EB%96%BB%EA%B2%8C-%ED%99%9C%EC%9A%A9%ED%95%A0-%EC%88%98-%EC%9E%88%EC%9D%84%EA%B9%8C%EC%9A%94-%EC%82%AC%EC%9A%A9-%EB%B0%A9%EB%B2%95-%EB%B0%8F-%EC%98%88%EC%A0%9C">Spring State Machine을 어떻게 활용할 수 있을까요?</a>
 * @see <a href="https://medium.com/@alishazy/spring-statemachine-a-comprehensive-guide-31dc346a600d">Spring Statemachine: A Comprehensive Guide</a>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final AccountJpaRepository accountRepository;
    private final SubscriptionJpaRepository subscriptionRepository;
    private final ChannelJpaRepository channelJpaRepository;

    private final StateMachineService<SubscriptionStatus, SubscriptionEvent> stateMachineService;

    private final SubscriptionMapper subscriptionMapper;

    @Transactional
    public RegisteredSubscription subscribe(@Valid SubscribeRequest request) {
        AtomicReference<Boolean> isNewAccount = new AtomicReference<>(false);

        // ChannelJpaEntity 조회
        ChannelJpaEntity channel = channelJpaRepository.findByIdAndAvailableTrue(request.channelId())
                .orElseThrow(CommonApplicationException.CHANNEL_NOT_FOUND);

        // 휴대폰 번호에 해당하는 회원 조회 및 없으면 신규 생성
        AccountJpaEntity account = accountRepository.findByPhoneNumber(request.phoneNumber()).orElseGet(() -> {
            isNewAccount.set(true);
            return createNewAccount(request);
        });

        // 신규 회원이 아니면서 현재 구독 상태가 '구독 안함' 이고, 요청한 구독 상태도 '구독 안함'이면 거부
        if (!isNewAccount.get() &&
                SubscriptionStatus.NONE.equals(account.getCurrentSubscriptionStatus()) &&
                SubscriptionStatus.NONE.equals(request.operation())) {
            log.error("SubscriptionJpaEntity transition denied. AccountJpaEntity: {}, Current SubscriptionJpaEntity Status: {}, Requested SubscriptionJpaEntity Status: {}",
                    account.getPhoneNumber(), account.getCurrentSubscriptionStatus(), request.operation());
            throw CommonApplicationException.SUBSCRIPTION_TRANSITION_DENIED;
        }

        // State Machine에서 사용할 이벤트 생성
        SubscriptionEvent subscriptionEvent = getEvent(account.getCurrentSubscriptionStatus(), request.operation(), channel.getType());

        transitionSubscriptionStatus(account, channel, subscriptionEvent, SubscribeOperation.SUBSCRIBE);

        // State Machine에서 성공하면 DB에 구독 정보 저장
        SubscriptionJpaEntity savedSubscription = createNewSubscription(request, account, channel);

        // 회원의 현재 구독 상태 업데이트
        AccountJpaEntity updatedAccount = updateCurrentSubscriptionStatus(request, account);

        return subscriptionMapper.registeredSubscription(savedSubscription, updatedAccount, channel);
    }

    @Transactional
    public RegisteredSubscription unsubscribe(@Valid SubscribeRequest request) {
        // ChannelJpaEntity 조회
        ChannelJpaEntity channel = channelJpaRepository.findByIdAndAvailableTrue(request.channelId())
                .orElseThrow(CommonApplicationException.CHANNEL_NOT_FOUND);

        // 휴대폰 번호에 해당하는 회원 조회 및 없으면 예외 발생
        AccountJpaEntity account = accountRepository.findByPhoneNumber(request.phoneNumber())
                .orElseThrow(CommonApplicationException.ACCOUNT_NOT_FOUND);

        // State Machine에서 사용할 이벤트 생성
        SubscriptionEvent subscriptionEvent = getEvent(account.getCurrentSubscriptionStatus(), request.operation(), channel.getType());

        transitionSubscriptionStatus(account, channel, subscriptionEvent, SubscribeOperation.UNSUBSCRIBE);

        // State Machine에서 성공하면 DB에 구독 정보 저장
        SubscriptionJpaEntity savedSubscription = createNewSubscription(request, account, channel);

        // 회원의 현재 구독 상태 업데이트
        AccountJpaEntity updatedAccount = updateCurrentSubscriptionStatus(request, account);

        return subscriptionMapper.registeredSubscription(savedSubscription, updatedAccount, channel);
    }

    @Transactional(readOnly = true)
    public Map<String, List<SubscriptionHistory>> getSubscriptionHistory(@NotBlank String phoneNumber) {
        return subscriptionRepository.findAllByPhoneNumber(phoneNumber);
    }


    @Transactional
    public AccountJpaEntity createNewAccount(SubscribeRequest request) {
        AccountJpaEntity build = AccountJpaEntity.builder().phoneNumber(request.phoneNumber()).build();
        return accountRepository.save(build);
    }

    @Transactional
    public SubscriptionJpaEntity createNewSubscription(SubscribeRequest request, AccountJpaEntity account, ChannelJpaEntity channel) {
        SubscriptionJpaEntity build = subscriptionMapper.toEntity(request, account, channel);
        return subscriptionRepository.save(build);
    }

    @Transactional
    public AccountJpaEntity updateCurrentSubscriptionStatus(SubscribeRequest request, AccountJpaEntity account) {
        AccountJpaEntity updatedAccount = account.toBuilder().currentSubscriptionStatus(request.operation()).build();
        accountRepository.save(updatedAccount);
        return updatedAccount;
    }

    private static SubscriptionEvent getEvent(final SubscriptionStatus previousStatus, final SubscriptionStatus nextStatus, final ChannelType channelType) {
        return SubscriptionEvent.from(previousStatus, nextStatus, channelType);
    }

    private void transitionSubscriptionStatus(AccountJpaEntity account, ChannelJpaEntity channel, SubscriptionEvent subscriptionEvent, SubscribeOperation operation) {
        // 회원에 해당하는 State Machine을 Redis에서 가져옴 (없으면 신규 생성)
        StateMachine<SubscriptionStatus, SubscriptionEvent> acquiredStateMachine =
                stateMachineService.acquireStateMachine(String.valueOf(account.getId()));

        // State Machine에 이벤트 전달 및 결과 리턴
        StateMachineEventResult<SubscriptionStatus, SubscriptionEvent> stateMachineEventResult = acquiredStateMachine
                .sendEvent(buildMessage(subscriptionEvent, channel.getType(), operation))
                .blockFirst();

        assert stateMachineEventResult != null;
        StateMachineEventResult.ResultType resultType = stateMachineEventResult.getResultType();

        // State Machine에서 거부되면 예외 발생
        if (StateMachineEventResult.ResultType.DENIED.equals(resultType))
            throw CommonApplicationException.SUBSCRIPTION_TRANSITION_DENIED;
    }

    private static Mono<Message<SubscriptionEvent>> buildMessage(final SubscriptionEvent event, final ChannelType channelType, final SubscribeOperation operation) {
        return Mono.just(
                MessageBuilder.withPayload(event)
                        .setHeader("channelType", channelType)
                        .setHeader("operation", operation)
                        .build()
        );
    }
}
