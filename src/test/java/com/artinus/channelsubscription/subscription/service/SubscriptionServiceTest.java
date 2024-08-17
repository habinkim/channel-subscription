package com.artinus.channelsubscription.subscription.service;

import com.artinus.channelsubscription.channel.application.port.output.LoadChannelPort;
import com.artinus.channelsubscription.channel.domain.RegisteredChannel;
import com.artinus.channelsubscription.common.exception.CommonApplicationException;
import com.artinus.channelsubscription.subscription.application.port.input.SubscribeCommand;
import com.artinus.channelsubscription.subscription.application.port.output.*;
import com.artinus.channelsubscription.subscription.application.service.SubscriptionService;
import com.artinus.channelsubscription.subscription.domain.RegisteredAccount;
import com.artinus.channelsubscription.subscription.domain.SubscriptionStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SubscriptionServiceTest {

    @Mock
    private LoadChannelPort loadChannelPort;

    @Mock
    private LoadAccountPort loadAccountPort;

    @Mock
    private SaveAccountPort saveAccountPort;

    @Mock
    private LoadSubscriptionPort loadSubscriptionPort;

    @Mock
    private SaveSubscriptionPort saveSubscriptionPort;

    @Mock
    private AttemptTransitionPort attemptTransitionPort;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    @Order(1)
    @DisplayName("존재하지 않는 채널로 구독할 수 없다.")
    void subscribe_channelNotFound_throwsException() {
        // given
        SubscribeCommand request = new SubscribeCommand("010-0000-0000", 1L, SubscriptionStatus.REGULAR);

        when(loadChannelPort.findById(request.channelId())).thenReturn(Optional.empty());

        // when & then
        CommonApplicationException exception = assertThrows(CommonApplicationException.class, () -> subscriptionService.subscribe(request));

        assertEquals(CommonApplicationException.CHANNEL_NOT_FOUND, exception);

        verify(loadChannelPort, times(1)).findById(request.channelId());
        verify(saveSubscriptionPort, never()).saveSubscription(any());
    }

    @Test
    @Order(2)
    @DisplayName("기존 회원은 구독 안함으로 상태를 초기화할 수 없다.")
    void subscribe_existingAccountWithNoneStatus_throwsException() {
        // given
        SubscribeCommand request = new SubscribeCommand("010-0000-0000", 1L, SubscriptionStatus.NONE);

        RegisteredAccount accountMock = mock(RegisteredAccount.class);
        when(loadChannelPort.findById(request.channelId())).thenReturn(Optional.of(mock(RegisteredChannel.class)));
        when(loadAccountPort.findByPhoneNumber(request.phoneNumber())).thenReturn(Optional.of(accountMock));
        when(accountMock.currentSubscriptionStatus()).thenReturn(SubscriptionStatus.NONE);

        // when & then
        CommonApplicationException exception = assertThrows(CommonApplicationException.class, () -> subscriptionService.subscribe(request));

        assertEquals(CommonApplicationException.SUBSCRIPTION_TRANSITION_DENIED, exception);

        verify(loadChannelPort, times(1)).findById(request.channelId());
        verify(loadAccountPort, times(1)).findByPhoneNumber(request.phoneNumber());
        verify(saveSubscriptionPort, never()).saveSubscription(any());
    }

    @Test
    @Order(3)
    @DisplayName("존재하지 않는 채널로 구독해지할 수 없다.")
    void unsubscribe_channelNotFound_throwsException() {
        // given
        SubscribeCommand request = new SubscribeCommand("010-0000-0000", 1L, SubscriptionStatus.NONE);

        when(loadChannelPort.findById(request.channelId())).thenReturn(Optional.empty());

        // when & then
        CommonApplicationException exception = assertThrows(CommonApplicationException.class, () -> subscriptionService.unsubscribe(request));

        assertEquals(CommonApplicationException.CHANNEL_NOT_FOUND, exception);

        verify(loadChannelPort, times(1)).findById(request.channelId());
        verify(saveSubscriptionPort, never()).saveSubscription(any());
    }

    @Test
    @Order(4)
    @DisplayName("존재하지 않는 전화번호로 구독해지할 수 없다.")
    void unsubscribe_accountNotFound_throwsException() {
        // given
        SubscribeCommand request = new SubscribeCommand("010-0000-0000", 1L, SubscriptionStatus.NONE);

        when(loadChannelPort.findById(request.channelId())).thenReturn(Optional.of(mock(RegisteredChannel.class)));
        when(loadAccountPort.findByPhoneNumber(request.phoneNumber())).thenReturn(Optional.empty());

        // when & then
        CommonApplicationException exception = assertThrows(CommonApplicationException.class, () -> subscriptionService.unsubscribe(request));

        assertEquals(CommonApplicationException.ACCOUNT_NOT_FOUND, exception);

        verify(loadChannelPort, times(1)).findById(request.channelId());
        verify(loadAccountPort, times(1)).findByPhoneNumber(request.phoneNumber());
        verify(saveSubscriptionPort, never()).saveSubscription(any());
    }

}
