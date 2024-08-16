package com.artinus.channelsubscription.channel.controller;

import com.artinus.channelsubscription.base.ControllerBaseTest;
import com.artinus.channelsubscription.channel.domain.RegisterChannelRequest;
import com.artinus.channelsubscription.channel.domain.ChannelType;
import com.artinus.channelsubscription.common.response.MessageCode;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
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

class ChannelControllerTest extends ControllerBaseTest {

    @Transactional
    @Test
    @Order(1)
    @DisplayName("채널 등록, 성공")
    void registerChannelSuccess() throws Exception {
        RegisterChannelRequest request = new RegisterChannelRequest("KT 통신사", ChannelType.SUBSCRIBE_UNSUBSCRIBE);

        FieldDescriptor[] requestDescriptors = new FieldDescriptor[] {
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

}
