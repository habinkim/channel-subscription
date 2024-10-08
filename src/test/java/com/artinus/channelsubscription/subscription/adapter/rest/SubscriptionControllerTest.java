package com.artinus.channelsubscription.subscription.adapter.rest;

import com.artinus.channelsubscription.base.ControllerBaseTest;
import com.artinus.channelsubscription.channel.application.port.input.RegisterChannelUseCase;
import com.artinus.channelsubscription.channel.domain.ChannelType;
import com.artinus.channelsubscription.channel.application.port.input.RegisterChannelCommand;
import com.artinus.channelsubscription.channel.domain.RegisteredChannel;
import com.artinus.channelsubscription.channel.application.service.ChannelService;
import com.artinus.channelsubscription.common.response.MessageCode;
import com.artinus.channelsubscription.subscription.application.port.input.SubscribeCommand;
import com.artinus.channelsubscription.subscription.application.port.input.SubscribeUseCase;
import com.artinus.channelsubscription.subscription.application.port.input.UnsubscribeUseCase;
import com.artinus.channelsubscription.subscription.domain.SubscriptionStatus;
import com.artinus.channelsubscription.subscription.application.service.SubscriptionService;
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
    private RegisterChannelUseCase registerChannelUseCase;

    @Autowired
    private SubscribeUseCase subscribeUseCase;

    @Autowired
    private UnsubscribeUseCase unsubscribeUseCase;

    @Transactional
    @Test
    @Order(1)
    @DisplayName("신규 회원 구독하기(구독 안함), 성공")
    void subscribeInitializeNoneSuccess() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = registerChannelUseCase.registerChannel(registerChannelCommand);

        SubscribeCommand subscribeCommand = new SubscribeCommand("010-1234-5678", registeredChannel.id(), SubscriptionStatus.NONE);

        SubscriptionStatus previousStatus = SubscriptionStatus.NONE;
        performSubscribeApi(subscribeCommand, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(2)
    @DisplayName("신규 회원 구독하기(일반 구독), 성공")
    void subscribeRegularInitializeSuccess() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = registerChannelUseCase.registerChannel(registerChannelCommand);

        SubscribeCommand subscribeCommand = new SubscribeCommand("010-1234-5678", registeredChannel.id(), SubscriptionStatus.REGULAR);

        SubscriptionStatus previousStatus = SubscriptionStatus.NONE;
        performSubscribeApi(subscribeCommand, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(3)
    @DisplayName("신규 회원 구독하기(프리미엄 구독), 성공")
    void subscribePremiumInitializeSuccess() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = registerChannelUseCase.registerChannel(registerChannelCommand);

        SubscribeCommand subscribeCommand = new SubscribeCommand("010-1234-5678", registeredChannel.id(), SubscriptionStatus.PREMIUM);

        SubscriptionStatus previousStatus = SubscriptionStatus.NONE;
        performSubscribeApi(subscribeCommand, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(4)
    @DisplayName("기존 회원 구독하기(구독 안함 -> 프리미엄 구독), 성공")
    void subscribeNoneToPremiumSuccess() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = registerChannelUseCase.registerChannel(registerChannelCommand);

        String phoneNumber = "010-1234-5678";
        SubscriptionStatus previousStatus = SubscriptionStatus.NONE;
        SubscribeCommand initialSubscribeCommand = new SubscribeCommand(phoneNumber, registeredChannel.id(), previousStatus);

        subscribeUseCase.subscribe(initialSubscribeCommand);

        SubscribeCommand subscribeCommand = new SubscribeCommand(phoneNumber, registeredChannel.id(), SubscriptionStatus.PREMIUM);

        performSubscribeApi(subscribeCommand, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(5)
    @DisplayName("기존 회원 구독하기(구독 안함 -> 일반 구독), 성공")
    void subscribeNoneToRegularSuccess() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = registerChannelUseCase.registerChannel(registerChannelCommand);

        String phoneNumber = "010-1234-5678";
        SubscriptionStatus previousStatus = SubscriptionStatus.NONE;
        SubscribeCommand initialSubscribeCommand = new SubscribeCommand(phoneNumber, registeredChannel.id(), previousStatus);

        subscribeUseCase.subscribe(initialSubscribeCommand);

        SubscribeCommand subscribeCommand = new SubscribeCommand(phoneNumber, registeredChannel.id(), SubscriptionStatus.REGULAR);

        performSubscribeApi(subscribeCommand, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(6)
    @DisplayName("기존 회원 구독하기(일반 구독 -> 프리미엄 구독), 성공")
    void subscribeRegularToPremiumSuccess() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = registerChannelUseCase.registerChannel(registerChannelCommand);

        String phoneNumber = "010-1234-5678";
        SubscriptionStatus previousStatus = SubscriptionStatus.REGULAR;
        SubscribeCommand initialSubscribeCommand = new SubscribeCommand(phoneNumber, registeredChannel.id(), previousStatus);

        subscribeUseCase.subscribe(initialSubscribeCommand);

        SubscribeCommand subscribeCommand = new SubscribeCommand(phoneNumber, registeredChannel.id(), SubscriptionStatus.PREMIUM);

        performSubscribeApi(subscribeCommand, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(7)
    @DisplayName("구독하기, 실패 (올바르지 않은 채널 타입)")
    void subscribeFailure_invalidChannelType() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.UNSUBSCRIBE_ONLY);
        RegisteredChannel registeredChannel = registerChannelUseCase.registerChannel(registerChannelCommand);

        SubscribeCommand subscribeCommand = new SubscribeCommand("010-1234-5678", registeredChannel.id(), SubscriptionStatus.REGULAR);

        FieldDescriptor[] requestDescriptors = new FieldDescriptor[]{
                fieldWithPath("phone_number").description("전화번호"),
                fieldWithPath("channel_id").description("채널 ID"),
                fieldWithPath("operation").description("변경할 구독 상태 [NONE, REGULAR, PREMIUM]")
        };

        mockMvc.perform(
                        post("/subscriptions/subscribe")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(toJson(subscribeCommand))
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

    public void performSubscribeApi(SubscribeCommand subscribeCommand, RegisteredChannel registeredChannel, SubscriptionStatus previousStatus) throws Exception {
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
                                .content(toJson(subscribeCommand))
                )
                .andExpectAll(baseAssertion(MessageCode.CREATED))
                .andExpect(jsonPath("$.data.subscription_id", notNullValue()))
                .andExpectAll(
                        jsonPath("$.data.phone_number", notNullValue()),
                        jsonPath("$.data.phone_number", is(subscribeCommand.phoneNumber()))
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
                        jsonPath("$.data.status", is(subscribeCommand.operation().name()))
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
        RegisteredChannel registeredChannel = registerChannelUseCase.registerChannel(registerChannelCommand);

        String phoneNumber = "010-1234-5678";
        SubscriptionStatus previousStatus = SubscriptionStatus.PREMIUM;
        SubscribeCommand initialSubscribeCommand = new SubscribeCommand(phoneNumber, registeredChannel.id(), previousStatus);

        subscribeUseCase.subscribe(initialSubscribeCommand);

        SubscribeCommand unsubscribeCommand = new SubscribeCommand(phoneNumber, registeredChannel.id(), SubscriptionStatus.REGULAR);

        performUnsubscribeApi(unsubscribeCommand, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(9)
    @DisplayName("구독해제(프리미엄 구독 -> 구독 안함), 성공")
    void unsubscribePremiumToNoneSuccess() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = registerChannelUseCase.registerChannel(registerChannelCommand);

        String phoneNumber = "010-1234-5678";
        SubscriptionStatus previousStatus = SubscriptionStatus.PREMIUM;
        SubscribeCommand initialSubscribeCommand = new SubscribeCommand(phoneNumber, registeredChannel.id(), previousStatus);

        subscribeUseCase.subscribe(initialSubscribeCommand);

        SubscribeCommand unsubscribeCommand = new SubscribeCommand(phoneNumber, registeredChannel.id(), SubscriptionStatus.NONE);

        performUnsubscribeApi(unsubscribeCommand, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(10)
    @DisplayName("구독해제(일반 구독 -> 구독 안함), 성공")
    void unsubscribeRegularToNoneSuccess() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = registerChannelUseCase.registerChannel(registerChannelCommand);

        String phoneNumber = "010-1234-5678";
        SubscriptionStatus previousStatus = SubscriptionStatus.REGULAR;
        SubscribeCommand initialSubscribeCommand = new SubscribeCommand(phoneNumber, registeredChannel.id(), previousStatus);

        subscribeUseCase.subscribe(initialSubscribeCommand);

        SubscribeCommand unsubscribeCommand = new SubscribeCommand(phoneNumber, registeredChannel.id(), SubscriptionStatus.NONE);

        performUnsubscribeApi(unsubscribeCommand, registeredChannel, previousStatus);

    }

    @Transactional
    @Test
    @Order(11)
    @DisplayName("구독해제, 실패 (올바르지 않은 채널 타입)")
    void unsubscribeFailure_invalidChannelType() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_ONLY);
        RegisteredChannel registeredChannel = registerChannelUseCase.registerChannel(registerChannelCommand);

        String phoneNumber = "010-1234-5678";
        SubscribeCommand initialSubscribeCommand = new SubscribeCommand(phoneNumber, registeredChannel.id(), SubscriptionStatus.REGULAR);

        subscribeUseCase.subscribe(initialSubscribeCommand);

        SubscribeCommand unsubscribeCommand = new SubscribeCommand(phoneNumber, registeredChannel.id(), SubscriptionStatus.NONE);

        FieldDescriptor[] requestDescriptors = new FieldDescriptor[]{
                fieldWithPath("phone_number").description("전화번호"),
                fieldWithPath("channel_id").description("채널 ID"),
                fieldWithPath("operation").description("변경할 구독 상태 [NONE, REGULAR, PREMIUM]")
        };

        mockMvc.perform(
                        post("/subscriptions/unsubscribe")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(toJson(unsubscribeCommand))
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

    public void performUnsubscribeApi(SubscribeCommand unsubscribeCommand, RegisteredChannel registeredChannel, SubscriptionStatus previousStatus) throws Exception {
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
                                .content(toJson(unsubscribeCommand))
                )
                .andExpectAll(baseAssertion(MessageCode.SUCCESS))
                .andExpect(jsonPath("$.data.subscription_id", notNullValue()))
                .andExpectAll(
                        jsonPath("$.data.phone_number", notNullValue()),
                        jsonPath("$.data.phone_number", is(unsubscribeCommand.phoneNumber()))
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
                        jsonPath("$.data.status", is(unsubscribeCommand.operation().name()))
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
            RegisteredChannel registeredChannel = registerChannelUseCase.registerChannel(registerChannelCommand);

            SubscribeCommand subscribeCommand = new SubscribeCommand(phoneNumber, registeredChannel.id(), SubscriptionStatus.REGULAR);
            subscribeUseCase.subscribe(subscribeCommand);
            count.addAndGet(1);

            SubscribeCommand unsubscribeCommand = new SubscribeCommand(phoneNumber, registeredChannel.id(), SubscriptionStatus.NONE);
            unsubscribeUseCase.unsubscribe(unsubscribeCommand);
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
