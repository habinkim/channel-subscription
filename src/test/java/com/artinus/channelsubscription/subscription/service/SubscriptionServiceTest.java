package com.artinus.channelsubscription.subscription.service;

import com.artinus.channelsubscription.channel.repository.ChannelRepository;
import com.artinus.channelsubscription.common.exception.CommonApplicationException;
import com.artinus.channelsubscription.subscription.domain.SubscribeRequest;
import com.artinus.channelsubscription.subscription.domain.SubscriptionStatus;
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
    private ChannelRepository channelRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    @Order(1)
    @DisplayName("존재하지 않는 채널로 구독할 수 없다.")
    void subscribe_channelNotFound_throwsException() {
        // given
        SubscribeRequest request = new SubscribeRequest("010-0000-0000", 1L, SubscriptionStatus.REGULAR);

        when(channelRepository.findByIdAndAvailableTrue(request.channelId())).thenReturn(Optional.empty());

        // when & then
        CommonApplicationException exception = assertThrows(CommonApplicationException.class, () -> subscriptionService.subscribe(request));

        assertEquals(CommonApplicationException.CHANNEL_NOT_FOUND, exception);

        verify(channelRepository, times(1)).findByIdAndAvailableTrue(request.channelId());
        verify(subscriptionRepository, never()).save(any());
    }

}
