package com.artinus.channelsubscription.subscription.controller;

import com.artinus.channelsubscription.base.ControllerBaseTest;
import com.artinus.channelsubscription.channel.domain.ChannelType;
import com.artinus.channelsubscription.channel.application.port.input.RegisterChannelCommand;
import com.artinus.channelsubscription.channel.domain.RegisteredChannel;
import com.artinus.channelsubscription.channel.application.service.ChannelService;
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

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.ResourceSnippetParameters.builder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
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
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = channelService.registerChannel(registerChannelCommand);

        SubscribeRequest subscribeRequest = new SubscribeRequest("010-1234-5678", registeredChannel.id(), SubscriptionStatus.NONE);

        SubscriptionStatus previousStatus = SubscriptionStatus.NONE;
        performSubscribeApi(subscribeRequest, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(2)
    @DisplayName("신규 회원 구독하기(일반 구독), 성공")
    void subscribeRegularInitializeSuccess() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = channelService.registerChannel(registerChannelCommand);

        SubscribeRequest subscribeRequest = new SubscribeRequest("010-1234-5678", registeredChannel.id(), SubscriptionStatus.REGULAR);

        SubscriptionStatus previousStatus = SubscriptionStatus.NONE;
        performSubscribeApi(subscribeRequest, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(3)
    @DisplayName("신규 회원 구독하기(프리미엄 구독), 성공")
    void subscribePremiumInitializeSuccess() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = channelService.registerChannel(registerChannelCommand);

        SubscribeRequest subscribeRequest = new SubscribeRequest("010-1234-5678", registeredChannel.id(), SubscriptionStatus.PREMIUM);

        SubscriptionStatus previousStatus = SubscriptionStatus.NONE;
        performSubscribeApi(subscribeRequest, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(4)
    @DisplayName("기존 회원 구독하기(구독 안함 -> 프리미엄 구독), 성공")
    void subscribeNoneToPremiumSuccess() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = channelService.registerChannel(registerChannelCommand);

        String phoneNumber = "010-1234-5678";
        SubscriptionStatus previousStatus = SubscriptionStatus.NONE;
        SubscribeRequest initialSubscribeRequest = new SubscribeRequest(phoneNumber, registeredChannel.id(), previousStatus);

        subscriptionService.subscribe(initialSubscribeRequest);

        SubscribeRequest subscribeRequest = new SubscribeRequest(phoneNumber, registeredChannel.id(), SubscriptionStatus.PREMIUM);

        performSubscribeApi(subscribeRequest, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(5)
    @DisplayName("기존 회원 구독하기(구독 안함 -> 일반 구독), 성공")
    void subscribeNoneToRegularSuccess() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = channelService.registerChannel(registerChannelCommand);

        String phoneNumber = "010-1234-5678";
        SubscriptionStatus previousStatus = SubscriptionStatus.NONE;
        SubscribeRequest initialSubscribeRequest = new SubscribeRequest(phoneNumber, registeredChannel.id(), previousStatus);

        subscriptionService.subscribe(initialSubscribeRequest);

        SubscribeRequest subscribeRequest = new SubscribeRequest(phoneNumber, registeredChannel.id(), SubscriptionStatus.REGULAR);

        performSubscribeApi(subscribeRequest, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(6)
    @DisplayName("기존 회원 구독하기(일반 구독 -> 프리미엄 구독), 성공")
    void subscribeRegularToPremiumSuccess() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = channelService.registerChannel(registerChannelCommand);

        String phoneNumber = "010-1234-5678";
        SubscriptionStatus previousStatus = SubscriptionStatus.REGULAR;
        SubscribeRequest initialSubscribeRequest = new SubscribeRequest(phoneNumber, registeredChannel.id(), previousStatus);

        subscriptionService.subscribe(initialSubscribeRequest);

        SubscribeRequest subscribeRequest = new SubscribeRequest(phoneNumber, registeredChannel.id(), SubscriptionStatus.PREMIUM);

        performSubscribeApi(subscribeRequest, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(7)
    @DisplayName("구독하기, 실패 (올바르지 않은 채널 타입)")
    void subscribeFailure_invalidChannelType() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.UNSUBSCRIBE_ONLY);
        RegisteredChannel registeredChannel = channelService.registerChannel(registerChannelCommand);

        SubscribeRequest subscribeRequest = new SubscribeRequest("010-1234-5678", registeredChannel.id(), SubscriptionStatus.REGULAR);

        FieldDescriptor[] requestDescriptors = new FieldDescriptor[]{
                fieldWithPath("phone_number").description("전화번호"),
                fieldWithPath("channel_id").description("채널 ID"),
                fieldWithPath("operation").description("변경할 구독 상태 [NONE, REGULAR, PREMIUM]")
        };

        mockMvc.perform(
                        post("/subscriptions/subscribe")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(toJson(subscribeRequest))
                )
                .andExpectAll(baseAssertion(MessageCode.INVALID_STATUS_TRANSITION))
                .andDo(
                        restDocs.document(
                                requestFields(requestDescriptors),
                                responseFields(baseResponseFields),
                                resource(
                                        builder().
                                                description("구독하기").
                                                requestFields(requestDescriptors).
                                                responseFields(baseResponseFields)
                                                .build()
                                )
                        )
                );
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

    @Transactional
    @Test
    @Order(8)
    @DisplayName("구독해제(프리미엄 구독 -> 일반 구독), 성공")
    void unsubscribePremiumToRegularSuccess() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = channelService.registerChannel(registerChannelCommand);

        String phoneNumber = "010-1234-5678";
        SubscriptionStatus previousStatus = SubscriptionStatus.PREMIUM;
        SubscribeRequest initialSubscribeRequest = new SubscribeRequest(phoneNumber, registeredChannel.id(), previousStatus);

        subscriptionService.subscribe(initialSubscribeRequest);

        SubscribeRequest unsubscribeRequest = new SubscribeRequest(phoneNumber, registeredChannel.id(), SubscriptionStatus.REGULAR);

        performUnsubscribeApi(unsubscribeRequest, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(9)
    @DisplayName("구독해제(프리미엄 구독 -> 구독 안함), 성공")
    void unsubscribePremiumToNoneSuccess() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = channelService.registerChannel(registerChannelCommand);

        String phoneNumber = "010-1234-5678";
        SubscriptionStatus previousStatus = SubscriptionStatus.PREMIUM;
        SubscribeRequest initialSubscribeRequest = new SubscribeRequest(phoneNumber, registeredChannel.id(), previousStatus);

        subscriptionService.subscribe(initialSubscribeRequest);

        SubscribeRequest unsubscribeRequest = new SubscribeRequest(phoneNumber, registeredChannel.id(), SubscriptionStatus.NONE);

        performUnsubscribeApi(unsubscribeRequest, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(10)
    @DisplayName("구독해제(일반 구독 -> 구독 안함), 성공")
    void unsubscribeRegularToNoneSuccess() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = channelService.registerChannel(registerChannelCommand);

        String phoneNumber = "010-1234-5678";
        SubscriptionStatus previousStatus = SubscriptionStatus.REGULAR;
        SubscribeRequest initialSubscribeRequest = new SubscribeRequest(phoneNumber, registeredChannel.id(), previousStatus);

        subscriptionService.subscribe(initialSubscribeRequest);

        SubscribeRequest unsubscribeRequest = new SubscribeRequest(phoneNumber, registeredChannel.id(), SubscriptionStatus.NONE);

        performUnsubscribeApi(unsubscribeRequest, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(11)
    @DisplayName("구독해제, 실패 (올바르지 않은 채널 타입)")
    void unsubscribeFailure_invalidChannelType() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_ONLY);
        RegisteredChannel registeredChannel = channelService.registerChannel(registerChannelCommand);

        String phoneNumber = "010-1234-5678";
        SubscribeRequest initialSubscribeRequest = new SubscribeRequest(phoneNumber, registeredChannel.id(), SubscriptionStatus.REGULAR);

        subscriptionService.subscribe(initialSubscribeRequest);

        SubscribeRequest unsubscribeRequest = new SubscribeRequest(phoneNumber, registeredChannel.id(), SubscriptionStatus.NONE);

        FieldDescriptor[] requestDescriptors = new FieldDescriptor[]{
                fieldWithPath("phone_number").description("전화번호"),
                fieldWithPath("channel_id").description("채널 ID"),
                fieldWithPath("operation").description("변경할 구독 상태 [NONE, REGULAR, PREMIUM]")
        };

        mockMvc.perform(
                        post("/subscriptions/unsubscribe")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(toJson(unsubscribeRequest))
                )
                .andExpectAll(baseAssertion(MessageCode.INVALID_STATUS_TRANSITION))
                .andDo(
                        restDocs.document(
                                requestFields(requestDescriptors),
                                responseFields(baseResponseFields),
                                resource(
                                        builder().
                                                description("구독해제").
                                                requestFields(requestDescriptors).
                                                responseFields(baseResponseFields)
                                                .build()
                                )
                        )
                );

    }

    public void performUnsubscribeApi(SubscribeRequest unsubscribeRequest, RegisteredChannel registeredChannel, SubscriptionStatus previousStatus) throws Exception {
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
                        post("/subscriptions/unsubscribe")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(toJson(unsubscribeRequest))
                )
                .andExpectAll(baseAssertion(MessageCode.SUCCESS))
                .andExpect(jsonPath("$.data.subscription_id", notNullValue()))
                .andExpectAll(
                        jsonPath("$.data.phone_number", notNullValue()),
                        jsonPath("$.data.phone_number", is(unsubscribeRequest.phoneNumber()))
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
                        jsonPath("$.data.status", is(unsubscribeRequest.operation().name()))
                )
                .andDo(
                        restDocs.document(
                                requestFields(requestDescriptors),
                                responseFields(responseDescriptors),
                                resource(
                                        builder().
                                                description("구독해제").
                                                requestFields(requestDescriptors).
                                                responseFields(responseDescriptors)
                                                .build()
                                )
                        )
                );
    }

    @Transactional
    @Test
    @Order(12)
    @DisplayName("구독 이력 조회, 성공")
    void getSubscriptionHistorySuccess() throws Exception {
        final String phoneNumber = "010-1234-5678";

        AtomicInteger count = new AtomicInteger(0);

        IntStream.rangeClosed(1, 20).forEach(i -> {
            RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("ChannelJpaEntity" + i, ChannelType.SUBSCRIBE_UNSUBSCRIBE);
            RegisteredChannel registeredChannel = channelService.registerChannel(registerChannelCommand);

            SubscribeRequest subscribeRequest = new SubscribeRequest(phoneNumber, registeredChannel.id(), SubscriptionStatus.REGULAR);
            subscriptionService.subscribe(subscribeRequest);
            count.addAndGet(1);

            SubscribeRequest unsubscribeRequest = new SubscribeRequest(phoneNumber, registeredChannel.id(), SubscriptionStatus.NONE);
            subscriptionService.unsubscribe(unsubscribeRequest);
            count.addAndGet(1);
        });

        String legend = LocalDate.now().toString();

        FieldDescriptor[] responseDescriptors = ArrayUtils.addAll(baseResponseFields,
                fieldWithPath("data." + legend + "[]").description(legend + "일자 구독 이력 목록"),
                fieldWithPath("data." + legend + "[].subscription_id").description("구독 이력 ID"),
                fieldWithPath("data." + legend + "[].phone_number").description("휴대폰 번호"),
                fieldWithPath("data." + legend + "[].channel_id").description("채널 ID"),
                fieldWithPath("data." + legend + "[].channel_name").description("채널명"),
                fieldWithPath("data." + legend + "[].previous_status").description("이전 구독 상태 [NONE, REGULAR, PREMIUM]"),
                fieldWithPath("data." + legend + "[].status").description("현재 구독 상태 [NONE, REGULAR, PREMIUM]"),
                fieldWithPath("data." + legend + "[].created_at").description("생성일시")
        );


        mockMvc.perform(
                        get("/subscriptions")
                                .param("phoneNumber", phoneNumber)
                )
                .andExpectAll(baseAssertion(MessageCode.SUCCESS))
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data." + legend, notNullValue()))
                .andExpect(jsonPath("$.data." + legend + ".length()", is(count.get())))
                .andExpect(jsonPath("$.data." + legend + "[*].subscription", notNullValue()))
                .andExpect(jsonPath("$.data." + legend + "[*].phone_number", notNullValue()))
                .andExpect(jsonPath("$.data." + legend + "[*].channel_id", notNullValue()))
                .andExpect(jsonPath("$.data." + legend + "[*].channel_name", notNullValue()))
                .andExpect(jsonPath("$.data." + legend + "[*].previous_status", notNullValue()))
                .andExpect(jsonPath("$.data." + legend + "[*].status", notNullValue()))
                .andExpect(jsonPath("$.data." + legend + "[*].created_at", notNullValue()))
                .andDo(
                        restDocs.document(
                                queryParameters(parameterWithName("phoneNumber").description("전화번호")),
                                responseFields(responseDescriptors)
                        )
                );


    }

}
