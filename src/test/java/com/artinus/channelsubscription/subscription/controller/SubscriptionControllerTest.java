package com.artinus.channelsubscription.subscription.controller;

import com.artinus.channelsubscription.base.ControllerBaseTest;
import com.artinus.channelsubscription.channel.domain.ChannelType;
import com.artinus.channelsubscription.channel.domain.RegisterChannelRequest;
import com.artinus.channelsubscription.channel.domain.RegisteredChannel;
import com.artinus.channelsubscription.channel.service.ChannelService;
import com.artinus.channelsubscription.common.response.MessageCode;
import com.artinus.channelsubscription.subscription.domain.SubscribeRequest;
import com.artinus.channelsubscription.subscription.domain.SubscriptionStatus;
import com.artinus.channelsubscription.subscription.service.SubscriptionService;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.transaction.annotation.Transactional;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.ResourceSnippetParameters.builder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class SubscriptionControllerTest extends ControllerBaseTest {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Transactional
    @Test
    @Order(1)
    @DisplayName("신규 회원 구독하기(구독 안함), 성공")
    void subscribeInitializeNoneSuccess() throws Exception {
        RegisterChannelRequest registerChannelRequest = new RegisterChannelRequest("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = channelService.registerChannel(registerChannelRequest);

        SubscribeRequest subscribeRequest = new SubscribeRequest("010-1234-5678", registeredChannel.id(), SubscriptionStatus.NONE);

        SubscriptionStatus previousStatus = SubscriptionStatus.NONE;
        performSubscribeApi(subscribeRequest, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(2)
    @DisplayName("신규 회원 구독하기(일반 구독), 성공")
    void subscribeRegularInitializeSuccess() throws Exception {
        RegisterChannelRequest registerChannelRequest = new RegisterChannelRequest("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = channelService.registerChannel(registerChannelRequest);

        SubscribeRequest subscribeRequest = new SubscribeRequest("010-1234-5678", registeredChannel.id(), SubscriptionStatus.REGULAR);

        SubscriptionStatus previousStatus = SubscriptionStatus.NONE;
        performSubscribeApi(subscribeRequest, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(3)
    @DisplayName("신규 회원 구독하기(프리미엄 구독), 성공")
    void subscribePremiumInitializeSuccess() throws Exception {
        RegisterChannelRequest registerChannelRequest = new RegisterChannelRequest("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = channelService.registerChannel(registerChannelRequest);

        SubscribeRequest subscribeRequest = new SubscribeRequest("010-1234-5678", registeredChannel.id(), SubscriptionStatus.PREMIUM);

        SubscriptionStatus previousStatus = SubscriptionStatus.NONE;
        performSubscribeApi(subscribeRequest, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(4)
    @DisplayName("기존 회원 구독하기(일반 구독 -> 프리미엄 구독), 성공")
    void subscribeRegularToPremiumSuccess() throws Exception {
        RegisterChannelRequest registerChannelRequest = new RegisterChannelRequest("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = channelService.registerChannel(registerChannelRequest);

        String phoneNumber = "010-1234-5678";
        SubscriptionStatus previousStatus = SubscriptionStatus.REGULAR;
        SubscribeRequest initialSubscribeRequest = new SubscribeRequest(phoneNumber, registeredChannel.id(), previousStatus);

        subscriptionService.subscribe(initialSubscribeRequest);

        SubscribeRequest subscribeRequest = new SubscribeRequest(phoneNumber, registeredChannel.id(), SubscriptionStatus.PREMIUM);

        performSubscribeApi(subscribeRequest, registeredChannel, previousStatus);

    }

    public void performSubscribeApi(SubscribeRequest subscribeRequest, RegisteredChannel registeredChannel, SubscriptionStatus previousStatus) throws Exception {
        FieldDescriptor[] requestDescriptors = new FieldDescriptor[]{
                fieldWithPath("phone_number").description("전화번호"),
                fieldWithPath("channel_id").description("채널 ID"),
                fieldWithPath("operation").description("변경할 구독 상태 [NONE, REGULAR, PREMIUM]")
        };

        FieldDescriptor[] responseDescriptors = ArrayUtils.addAll(
                baseResponseFields,
                fieldWithPath("data.subscription_id").description("구독 이력 ID"),
                fieldWithPath("data.phone_number").description("휴대폰 번호"),
                fieldWithPath("data.channel_id").description("채널 ID"),
                fieldWithPath("data.channel_name").description("채널명"),
                fieldWithPath("data.previous_status").description("이전 구독 상태 [NONE, REGULAR, PREMIUM]"),
                fieldWithPath("data.status").description("현재 구독 상태 [NONE, REGULAR, PREMIUM]"),
                fieldWithPath("data.created_at").description("생성일시")
        );

        mockMvc.perform(
                        post("/subscriptions/subscribe")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(toJson(subscribeRequest))
                )
                .andExpectAll(baseAssertion(MessageCode.CREATED))
                .andExpect(jsonPath("$.data.subscription_id", notNullValue()))
                .andExpectAll(
                        jsonPath("$.data.phone_number", notNullValue()),
                        jsonPath("$.data.phone_number", is(subscribeRequest.phoneNumber()))
                )
                .andExpectAll(
                        jsonPath("$.data.channel_id", notNullValue()),
                        jsonPath("$.data.channel_id", is(registeredChannel.id()))
                )
                .andExpectAll(
                        jsonPath("$.data.channel_name", notNullValue()),
                        jsonPath("$.data.channel_name", is(registeredChannel.name()))
                )
                .andExpectAll(
                        jsonPath("$.data.previous_status", notNullValue()),
                        jsonPath("$.data.previous_status", is(previousStatus.name()))
                )
                .andExpectAll(
                        jsonPath("$.data.status", notNullValue()),
                        jsonPath("$.data.status", is(subscribeRequest.operation().name()))
                )
                .andDo(
                        restDocs.document(
                                requestFields(requestDescriptors),
                                responseFields(responseDescriptors),
                                resource(
                                        builder().
                                                description("구독하기").
                                                requestFields(requestDescriptors).
                                                responseFields(responseDescriptors)
                                                .build()
                                )
                        )
                );
    }


}
