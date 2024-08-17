package com.artinus.channelsubscription.subscription.service;

import com.artinus.channelsubscription.channel.adapter.persistence.ChannelJpaEntity;
import com.artinus.channelsubscription.channel.adapter.persistence.ChannelJpaRepository;
import com.artinus.channelsubscription.common.exception.CommonApplicationException;
import com.artinus.channelsubscription.subscription.domain.SubscribeRequest;
import com.artinus.channelsubscription.subscription.domain.SubscriptionStatus;
import com.artinus.channelsubscription.subscription.entity.Account;
import com.artinus.channelsubscription.subscription.repository.AccountRepository;
import com.artinus.channelsubscription.subscription.repository.SubscriptionRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SubscriptionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private ChannelJpaRepository channelJpaRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    @Order(1)
    @DisplayName("존재하지 않는 채널로 구독할 수 없다.")
    void subscribe_channelNotFound_throwsException() {
        // given
        SubscribeRequest request = new SubscribeRequest("010-0000-0000", 1L, SubscriptionStatus.REGULAR);

        when(channelJpaRepository.findByIdAndAvailableTrue(request.channelId())).thenReturn(Optional.empty());

        // when & then
        CommonApplicationException exception = assertThrows(CommonApplicationException.class, () -> subscriptionService.subscribe(request));

        assertEquals(CommonApplicationException.CHANNEL_NOT_FOUND, exception);

        verify(channelJpaRepository, times(1)).findByIdAndAvailableTrue(request.channelId());
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    @Order(2)
    @DisplayName("기존 회원은 구독 안함으로 상태를 초기화할 수 없다.")
    void subscribe_existingAccountWithNoneStatus_throwsException() {
        // given
        SubscribeRequest request = new SubscribeRequest("010-0000-0000", 1L, SubscriptionStatus.NONE);

        Account accountMock = mock(Account.class);
        when(channelJpaRepository.findByIdAndAvailableTrue(request.channelId())).thenReturn(Optional.of(mock(ChannelJpaEntity.class)));
        when(accountRepository.findByPhoneNumber(request.phoneNumber())).thenReturn(Optional.of(accountMock));
        when(accountMock.getCurrentSubscriptionStatus()).thenReturn(SubscriptionStatus.NONE);

        // when & then
        CommonApplicationException exception = assertThrows(CommonApplicationException.class, () -> subscriptionService.subscribe(request));

        assertEquals(CommonApplicationException.SUBSCRIPTION_TRANSITION_DENIED, exception);

        verify(channelJpaRepository, times(1)).findByIdAndAvailableTrue(request.channelId());
        verify(accountRepository, times(1)).findByPhoneNumber(request.phoneNumber());
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    @Order(3)
    @DisplayName("존재하지 않는 채널로 구독해지할 수 없다.")
    void unsubscribe_channelNotFound_throwsException() {
        // given
        SubscribeRequest request = new SubscribeRequest("010-0000-0000", 1L, SubscriptionStatus.NONE);

        when(channelJpaRepository.findByIdAndAvailableTrue(request.channelId())).thenReturn(Optional.empty());

        // when & then
        CommonApplicationException exception = assertThrows(CommonApplicationException.class, () -> subscriptionService.unsubscribe(request));

        assertEquals(CommonApplicationException.CHANNEL_NOT_FOUND, exception);

        verify(channelJpaRepository, times(1)).findByIdAndAvailableTrue(request.channelId());
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    @Order(4)
    @DisplayName("존재하지 않는 전화번호로 구독해지할 수 없다.")
    void unsubscribe_accountNotFound_throwsException() {
        // given
        SubscribeRequest request = new SubscribeRequest("010-0000-0000", 1L, SubscriptionStatus.NONE);

        when(channelJpaRepository.findByIdAndAvailableTrue(request.channelId())).thenReturn(Optional.of(mock(ChannelJpaEntity.class)));
        when(accountRepository.findByPhoneNumber(request.phoneNumber())).thenReturn(Optional.empty());

        // when & then
        CommonApplicationException exception = assertThrows(CommonApplicationException.class, () -> subscriptionService.unsubscribe(request));

        assertEquals(CommonApplicationException.ACCOUNT_NOT_FOUND, exception);

        verify(channelJpaRepository, times(1)).findByIdAndAvailableTrue(request.channelId());
        verify(accountRepository, times(1)).findByPhoneNumber(request.phoneNumber());
        verify(subscriptionRepository, never()).save(any());
    }

}
