package com.example.woowa.order.review.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.woowa.RestDocsConfiguration;
import com.example.woowa.order.review.dto.ReviewCreateRequest;
import com.example.woowa.order.review.dto.ReviewFindResponse;
import com.example.woowa.order.review.dto.ReviewUpdateRequest;
import com.example.woowa.order.review.service.ReviewService;
import com.example.woowa.security.configuration.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs
@WebMvcTest(value = ReviewController.class, excludeFilters = {
        @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class
        ),
})
@Import(RestDocsConfiguration.class)
@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @Test
    @WithMockUser(username = "tester")
    void createReview() throws Exception {

        ReviewFindResponse reviewFindResponse = new ReviewFindResponse(1L, "정말정말 맛있습니다.", 5);

        given(reviewService.createReview(anyString(), anyLong(), any())).willReturn(reviewFindResponse);
        ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest(reviewFindResponse.getContent(),
                reviewFindResponse.getScoreType());

        mockMvc.perform(
                        post("/api/v1/reviews/{orderId}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reviewCreateRequest))
                                .with(csrf().asHeader())
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("reviews-create",
                        pathParameters(
                                parameterWithName("orderId").description("리뷰할 주문 ID")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("scoreType").type(JsonFieldType.NUMBER).description("평점")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("생성된 리뷰 아이디"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("scoreType").type(JsonFieldType.NUMBER).description("평점")
                        )
                ));
    }

    @Test
    void findReview() throws Exception {
        ReviewFindResponse reviewFindResponse = new ReviewFindResponse(1L, "정말정말 맛있습니다.", 5);

        given(reviewService.findReview(anyLong())).willReturn(reviewFindResponse);

        mockMvc.perform(
                        get("/api/v1/reviews/{id}", reviewFindResponse.getId())
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("reviews-find",
                        pathParameters(
                                parameterWithName("id").description("조회할 리뷰 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("생성된 리뷰 아이디"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("scoreType").type(JsonFieldType.NUMBER).description("평점")
                        )
                ));
    }

    @Test
    void findUserReview() throws Exception {
        List<ReviewFindResponse> result = new ArrayList<>();

        ReviewFindResponse reviewFindResponse1 = new ReviewFindResponse(1L, "정말정말 맛있습니다.", 5);
        ReviewFindResponse reviewFindResponse2 = new ReviewFindResponse(2L, "정말정말 맛없습니다.", 1);

        result.add(reviewFindResponse1);
        result.add(reviewFindResponse2);

        given(reviewService.findUserReview(anyString())).willReturn(result);

        mockMvc.perform(
                        get("/api/v1/reviews/user/{loginId}", "dev12")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("reviews-user-find",
                        pathParameters(
                                parameterWithName("loginId").description("고객 로그인 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("생성된 리뷰 아이디"),
                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("[].scoreType").type(JsonFieldType.NUMBER).description("평점")
                        )
                ));
    }

    @Test
    @WithMockUser(username = "tester")
    void updateReview() throws Exception {
        ReviewFindResponse reviewFindResponse = new ReviewFindResponse(1L, "정말정말 맛있습니다.", 5);

        given(reviewService.updateReview(any(), anyLong(), any())).willReturn(reviewFindResponse);

        ReviewUpdateRequest reviewUpdateRequest = new ReviewUpdateRequest("정말정말 맛없습니다.", 1);

        mockMvc.perform(
                        put("/api/v1/reviews/{id}", reviewFindResponse.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reviewUpdateRequest))
                                .with(csrf().asHeader())
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("reviews-update",
                        pathParameters(
                                parameterWithName("id").description("수정할 리뷰 ID")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("scoreType").type(JsonFieldType.NUMBER).description("평점")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("수정된 리뷰 아이디"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("scoreType").type(JsonFieldType.NUMBER).description("평점")
                        )
                ));

    }

    @Test
    @WithMockUser(username = "tester")
    void deleteReview() throws Exception {
        mockMvc.perform(
                        delete("/api/v1/reviews/{id}", 1)
                                .with(csrf().asHeader())
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("reviews-delete",
                                pathParameters(
                                        parameterWithName("id").description("삭제할 리뷰 ID")
                                )
                        )
                )
                .andExpect(content().string("delete id - 1")
                );
    }
}