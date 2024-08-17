package com.artinus.channelsubscription.channel.adapter.rest;

import com.artinus.channelsubscription.channel.application.port.input.GetChannelHistoryUseCase;
import com.artinus.channelsubscription.channel.application.port.input.RegisterChannelUseCase;
import com.artinus.channelsubscription.channel.application.port.input.RegisterChannelCommand;
import com.artinus.channelsubscription.channel.domain.RegisteredChannel;
import com.artinus.channelsubscription.common.response.MessageCode;
import com.artinus.channelsubscription.common.response.Response;
import com.artinus.channelsubscription.common.response.ResponseMapper;
import com.artinus.channelsubscription.common.stereotype.WebAdapter;
import com.artinus.channelsubscription.subscription.domain.SubscriptionHistory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@WebAdapter
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ResponseMapper responseMapper;

    private final RegisterChannelUseCase registerChannelUseCase;
    private final GetChannelHistoryUseCase getChannelHistoryUseCase;

    @PostMapping
    public ResponseEntity<Response<RegisteredChannel>> registerChannel(@Valid @RequestBody RegisterChannelCommand command) {
        RegisteredChannel registeredChannel = registerChannelUseCase.registerChannel(command);
        return responseMapper.ok(MessageCode.CREATED, registeredChannel);
    }

    @GetMapping("/{channelId}/subscriptions")
    public ResponseEntity<Response<List<SubscriptionHistory>>> getChannelSubscriptionHistory(
            @PathVariable(name = "channelId") @NotNull Long channelId,
            @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyyMMdd") LocalDate date) {
        List<SubscriptionHistory> histories = getChannelHistoryUseCase.getChannelSubscriptionHistory(channelId, date);
        return responseMapper.ok(MessageCode.SUCCESS, histories);
    }

}
