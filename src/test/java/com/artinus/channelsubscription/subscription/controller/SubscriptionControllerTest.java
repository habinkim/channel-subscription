package com.artinus.channelsubscription.subscription.controller;

import com.artinus.channelsubscription.base.ControllerBaseTest;
import com.artinus.channelsubscription.subscription.domain.SubscribeRequest;
import com.artinus.channelsubscription.subscription.entity.SubscriptionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionControllerTest extends ControllerBaseTest {

    @Transactional
    @Test
    @Order(1)
    @DisplayName("구독하기, 성공")
    void subscribeChannelSuccess() {

        new SubscribeRequest("01012345678", 1L, SubscriptionStatus.NONE);

    }


}
