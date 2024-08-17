package com.artinus.channelsubscription.channel.service;

import com.artinus.channelsubscription.channel.adapter.persistence.ChannelJpaRepository;
import com.artinus.channelsubscription.channel.adapter.persistence.ChannelMapper;
import com.artinus.channelsubscription.channel.application.port.input.RegisterChannelCommand;
import com.artinus.channelsubscription.channel.application.port.output.LoadChannelPort;
import com.artinus.channelsubscription.channel.application.port.output.SaveChannelPort;
import com.artinus.channelsubscription.channel.application.service.ChannelService;
import com.artinus.channelsubscription.channel.domain.ChannelType;
import com.artinus.channelsubscription.common.exception.CommonApplicationException;
import com.artinus.channelsubscription.subscription.repository.SubscriptionRepository;
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
class ChannelServiceTest {

    @Mock
    private ChannelJpaRepository channelJpaRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private LoadChannelPort loadChannelPort;

    @Mock
    private SaveChannelPort saveChannelPort;

    @InjectMocks
    private ChannelService channelService;

    @Test
    @Order(1)
    @DisplayName("이미 존재하는 채널 명 으로 채널을 생성할 수 없다.")
    void registerChannel_alreadyExists_throwsException() {
        // given
        RegisterChannelCommand request = new RegisterChannelCommand("Existing ChannelJpaEntity", ChannelType.SUBSCRIBE_ONLY);

        when(loadChannelPort.existsByName(request.name())).thenReturn(true);

        // when & then
        CommonApplicationException exception = assertThrows(CommonApplicationException.class, () -> channelService.registerChannel(request));

        assertEquals(CommonApplicationException.CHANNEL_ALREADY_EXISTS, exception);

        verify(loadChannelPort, times(1)).existsByName(request.name());
        verify(saveChannelPort, never()).saveChannel(any());
    }

    @Test
    @Order(2)
    @DisplayName("존재하지 않는 채널의 이력을 조회할 수 없다.")
    void getChannelSubscriptionHistory_notFound_throwsException() {
        // given
        Long channelId = 1L;

        when(channelJpaRepository.findByIdAndAvailableTrue(channelId)).thenReturn(Optional.empty());

        // when & then
        CommonApplicationException exception = assertThrows(CommonApplicationException.class, () -> channelService.getChannelSubscriptionHistory(channelId, null));

        assertEquals(CommonApplicationException.CHANNEL_NOT_FOUND, exception);

        verify(channelJpaRepository, times(1)).findByIdAndAvailableTrue(channelId);
        verify(subscriptionRepository, never()).findAllByChannelIdAndDate(any(), any());
    }

}
