package com.artinus.channelsubscription.subscription.controller;

import com.artinus.channelsubscription.common.response.Response;
import com.artinus.channelsubscription.common.response.ResponseMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ResponseMapper responseMapper;

    @GetMapping("/{channelId}/subscriptions")
    public ResponseEntity<Response<?>> getChannelSubscriptionHistory(
            @PathVariable(name = "channelId") @NotNull Long channelId,
            @RequestParam(name = "date") LocalDate date) {
        return responseMapper.ok();
    }

}
