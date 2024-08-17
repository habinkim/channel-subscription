package com.artinus.channelsubscription.channel.controller;

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
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.IntStream;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.ResourceSnippetParameters.builder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class ChannelControllerTest extends ControllerBaseTest {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Transactional
    @Test
    @Order(1)
    @DisplayName("채널 등록, 성공")
    void registerChannelSuccess() throws Exception {
        RegisterChannelCommand request = new RegisterChannelCommand("KT 통신사", ChannelType.SUBSCRIBE_UNSUBSCRIBE);

        FieldDescriptor[] requestDescriptors = new FieldDescriptor[]{
                fieldWithPath("name").description("채널명"),
                fieldWithPath("type").description("채널 유형 [SUBSCRIBE_UNSUBSCRIBE, SUBSCRIBE_ONLY, UNSUBSCRIBE_ONLY]")
        };

        FieldDescriptor[] responseDescriptors = ArrayUtils.addAll(
                baseResponseFields,
                fieldWithPath("data.id").description("채널 ID"),
                fieldWithPath("data.name").description("채널명"),
                fieldWithPath("data.type").description("채널 유형 [SUBSCRIBE_UNSUBSCRIBE, SUBSCRIBE_ONLY, UNSUBSCRIBE_ONLY]"),
                fieldWithPath("data.created_at").description("생성일시"),
                fieldWithPath("data.updated_at").description("수정일시")
        );

        mockMvc.perform(
                        post("/channels")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(toJson(request))
                )
                .andExpectAll(baseAssertion(MessageCode.CREATED))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpectAll(
                        jsonPath("$.data.name", notNullValue()),
                        jsonPath("$.data.name", is("KT 통신사"))
                )
                .andExpectAll(
                        jsonPath("$.data.type", notNullValue()),
                        jsonPath("$.data.type", is(ChannelType.SUBSCRIBE_UNSUBSCRIBE.name()))
                )
                .andExpectAll(
                        jsonPath("$.data.created_at", notNullValue()),
                        jsonPath("$.data.updated_at", notNullValue())
                )
                .andDo(
                        restDocs.document(
                                requestFields(requestDescriptors),
                                responseFields(responseDescriptors),
                                resource(
                                        builder().
                                                description("채널 등록").
                                                requestFields(requestDescriptors).
                                                responseFields(responseDescriptors)
                                                .build()
                                )
                        )
                );
    }

    @Transactional
    @Test
    @Order(2)
    @DisplayName("채널 이력 조회, 성공")
    void getChannelSubscriptionHistorySuccess() throws Exception {
        RegisterChannelCommand registerChannelCommand = new RegisterChannelCommand("홈쇼핑 고객센터", ChannelType.SUBSCRIBE_UNSUBSCRIBE);
        RegisteredChannel registeredChannel = channelService.registerChannel(registerChannelCommand);

        IntStream.rangeClosed(1, 20).forEach(i -> {
            String phoneNumber = "010-1234-" + String.format("%04d", i);
            SubscribeRequest subscribeRequest = new SubscribeRequest(phoneNumber, registeredChannel.id(), SubscriptionStatus.REGULAR);
            subscriptionService.subscribe(subscribeRequest);
        });

        ParameterDescriptor pathParameterDescriptor = parameterWithName("channelId").description("채널 ID");

        ParameterDescriptor queryParameterDescriptor = parameterWithName("date").description("조회일자 [yyyyMMdd]");


        FieldDescriptor[] responseDescriptors = ArrayUtils.addAll(baseResponseFields,
                fieldWithPath("data.[]").description("구독 이력 목록"),
                fieldWithPath("data.[].subscription_id").description("구독 이력 ID"),
                fieldWithPath("data.[].phone_number").description("휴대폰 번호"),
                fieldWithPath("data.[].channel_id").description("채널 ID"),
                fieldWithPath("data.[].channel_name").description("채널명"),
                fieldWithPath("data.[].previous_status").description("이전 구독 상태 [NONE, REGULAR, PREMIUM]"),
                fieldWithPath("data.[].status").description("현재 구독 상태 [NONE, REGULAR, PREMIUM]"),
                fieldWithPath("data.[].created_at").description("생성일시")
        );

        mockMvc.perform(
                        get("/channels/{channelId}/subscriptions", registeredChannel.id())
                                .param("date", DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now()))
                )
                .andExpectAll(baseAssertion(MessageCode.SUCCESS))
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data.length()", is(20)))
                .andExpect(jsonPath("$.data[*].subscription_id", notNullValue()))
                .andExpect(jsonPath("$.data[*].phone_number", notNullValue()))
                .andExpect(jsonPath("$.data[*].channel_id", notNullValue()))
                .andExpect(jsonPath("$.data[*].channel_name", notNullValue()))
                .andExpect(jsonPath("$.data[*].previous_status", notNullValue()))
                .andExpect(jsonPath("$.data[*].status", notNullValue()))
                .andExpect(jsonPath("$.data[*].created_at", notNullValue()))
                .andDo(
                        restDocs.document(
                                pathParameters(pathParameterDescriptor),
                                queryParameters(queryParameterDescriptor),
                                responseFields(responseDescriptors),
                                resource(
                                        builder().
                                                description("채널 구독 이력 조회").
                                                pathParameters(pathParameterDescriptor).
                                                queryParameters(queryParameterDescriptor).
                                                responseFields(responseDescriptors)
                                                .build()
                                )
                        )
                );

    }

}
