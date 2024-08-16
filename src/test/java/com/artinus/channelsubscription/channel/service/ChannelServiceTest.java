package com.artinus.channelsubscription.channel.service;

import com.artinus.channelsubscription.channel.domain.RegisterChannelRequest;
import com.artinus.channelsubscription.channel.entity.Channel;
import com.artinus.channelsubscription.channel.domain.ChannelType;
import com.artinus.channelsubscription.channel.mapper.ChannelMapper;
import com.artinus.channelsubscription.channel.repository.ChannelRepository;
import com.artinus.channelsubscription.common.exception.CommonApplicationException;
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
class ChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private ChannelMapper channelMapper;

    @InjectMocks
    private ChannelService channelService;

    @Test
    @Order(1)
    @DisplayName("이미 존재하는 채널 명 으로 채널을 생성할 수 없다.")
    void registerChannel_alreadyExists_throwsException() {
        // given
        RegisterChannelRequest request = new RegisterChannelRequest("Existing Channel", ChannelType.SUBSCRIBE_ONLY);

        when(channelRepository.findByNameAndAvailableTrue(request.name())).thenReturn(Optional.of(new Channel()));

        // when & then
        CommonApplicationException exception = assertThrows(CommonApplicationException.class, () -> channelService.registerChannel(request));

        assertEquals(CommonApplicationException.CHANNEL_ALREADY_EXISTS, exception);

        verify(channelRepository, times(1)).findByNameAndAvailableTrue(request.name());
        verify(channelRepository, never()).save(any());
    }

}
