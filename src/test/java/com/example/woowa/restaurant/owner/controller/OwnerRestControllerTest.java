package com.example.woowa.restaurant.owner.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.woowa.RestDocsConfiguration;
import com.example.woowa.common.exception.NotFoundException;
import com.example.woowa.restaurant.owner.dto.request.OwnerCreateRequest;
import com.example.woowa.restaurant.owner.dto.request.OwnerUpdateRequest;
import com.example.woowa.restaurant.owner.dto.response.OwnerCreateResponse;
import com.example.woowa.restaurant.owner.dto.response.OwnerFindResponse;
import com.example.woowa.restaurant.owner.service.OwnerService;
import com.example.woowa.security.configuration.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs
@WebMvcTest(value = OwnerRestController.class, excludeFilters = {
        @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class
        )
})
@Import(RestDocsConfiguration.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser
class OwnerRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    OwnerService ownerService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("사장님을 생성한다.")
    void createOwnerOkTest() throws Exception {
        OwnerCreateRequest request = new OwnerCreateRequest(
                "Aabcd123456",
                "tT@!123456789",
                "테스트",
                "010-1234-5678"
        );
        OwnerCreateResponse response = new OwnerCreateResponse(
                1L,
                "Aabcd123456",
                "tT@!123456789",
                "테스트",
                "010-1234-5678",
                LocalDateTime.now()
        );

        given(ownerService.createOwner(request)).willReturn(response);

        mockMvc.perform(post("/baemin/v1/owners")
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("사장님을 생성할 때 이름을 누락하면 400 응답이 발생한다. ")
    void createOwnerBadRequestTest() throws Exception {
        OwnerCreateRequest request = new OwnerCreateRequest(
                "Aabcd123456",
                "tT@!123456789",
                "",
                "010-1234-5678"
        );

        mockMvc.perform(post("/baemin/v1/owners")
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("사장님을 조회한다.")
    void findAllOwnersOkTest() throws Exception {
        List<OwnerFindResponse> responses = List.of(
                new OwnerFindResponse(
                    1L,
                    "Aabcd123456",
                    "tT@!123456789",
                    "테스트",
                    "010-1234-5678",
                    LocalDateTime.of(2025, 1, 7, 8, 0),
                    LocalDateTime.of(2025, 1, 7, 8, 0)
                ),
                new OwnerFindResponse(
                    2L,
                    "Aabcd123457",
                    "tT@!123456780",
                    "테스트",
                    "010-1234-5679",
                    LocalDateTime.of(2025, 1, 7, 8, 0),
                    LocalDateTime.of(2025, 1, 7, 8, 0)
                )
        );

        given(ownerService.findOwners()).willReturn(responses);

        mockMvc.perform(get("/baemin/v1/owners")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());
        then(ownerService).should().findOwners();
    }

    @Test
    @DisplayName("사장님을 단건 조회한다.")
    void findOwnerByIdOkTest() throws Exception {
        long ownerId = 1L;
        OwnerFindResponse response = new OwnerFindResponse(
                ownerId,
                "Aabcd123456",
                "tT@!123456789",
                "테스트",
                "010-1234-5678",
                LocalDateTime.of(2025, 1, 7, 8, 0),
                LocalDateTime.of(2025, 1, 7, 8, 0)
        );

        given(ownerService.findOwnerById(ownerId)).willReturn(response);

        mockMvc.perform(get("/baemin/v1/owners/{ownerId}", ownerId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        then(ownerService).should().findOwnerById(ownerId);
    }

    @Test
    @DisplayName("존재하지 않는 사장님을 조회하면 404 응답이 발생한다.")
    void findOwnerByIdNotFoundTest() throws Exception {
        long wrongOwnerId = -1L;

        given(ownerService.findOwnerById(wrongOwnerId)).willThrow(NotFoundException.class);

        mockMvc.perform(get("/baemin/v1/owners/{ownerId}", wrongOwnerId)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isNotFound());

        then(ownerService).should().findOwnerById(wrongOwnerId);
    }

    @Test
    @DisplayName("사장님을 삭제한다.")
    void deleteOwnerByIdOkTest() throws Exception {
        long ownerId = 1L;

        mockMvc.perform(delete("/baemin/v1/owners/{ownerId}", ownerId)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk());

        then(ownerService).should().deleteOwnerById(ownerId);
    }

    @Test
    @DisplayName("존재하지 않는 사장님을 삭제하면 404 응답이 발생한다.")
    void deleteOwnerByIdNotFoundTest() throws Exception {
        long wrongOwnerId = -1L;

        doThrow(NotFoundException.class).when(ownerService).deleteOwnerById(wrongOwnerId);

        mockMvc.perform(delete("/baemin/v1/owners/{ownerId}", wrongOwnerId)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isNotFound());

        then(ownerService).should().deleteOwnerById(wrongOwnerId);
    }

    @Test
    @DisplayName("사장님 정보를 변경한다.")
    void updateOwnerByIdOkTest() throws Exception {
        long ownerId = 1L;
        OwnerUpdateRequest request = new OwnerUpdateRequest("newPassword123", "홍길동", "010-1111-1111");

        mockMvc.perform(put("/baemin/v1/owners/{ownerId}", ownerId)
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
