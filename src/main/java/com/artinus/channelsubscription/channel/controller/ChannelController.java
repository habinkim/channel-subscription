package com.artinus.channelsubscription.channel.controller;

import com.artinus.channelsubscription.channel.domain.RegisterChannelRequest;
import com.artinus.channelsubscription.channel.domain.RegisteredChannel;
import com.artinus.channelsubscription.channel.service.ChannelService;
import com.artinus.channelsubscription.common.response.MessageCode;
import com.artinus.channelsubscription.common.response.Response;
import com.artinus.channelsubscription.common.response.ResponseMapper;
import com.artinus.channelsubscription.subscription.domain.SubscriptionHistory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ResponseMapper responseMapper;

    private final ChannelService channelService;

    @PostMapping
    public ResponseEntity<Response<RegisteredChannel>> registerChannel(@Valid @RequestBody RegisterChannelRequest request) {
        RegisteredChannel registeredChannel = channelService.registerChannel(request);
        return responseMapper.ok(MessageCode.CREATED, registeredChannel);
    }

    @GetMapping("/{channelId}/subscriptions")
    public ResponseEntity<Response<List<SubscriptionHistory>>> getChannelSubscriptionHistory(
            @PathVariable(name = "channelId") @NotNull Long channelId,
            @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyyMMdd") LocalDate date) {
        List<SubscriptionHistory> histories = channelService.getChannelSubscriptionHistory(channelId, date);
        return responseMapper.ok(MessageCode.SUCCESS, histories);
    }

}
