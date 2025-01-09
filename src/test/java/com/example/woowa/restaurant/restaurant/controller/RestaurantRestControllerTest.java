package com.example.woowa.restaurant.restaurant.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.woowa.RestDocsConfiguration;
import com.example.woowa.restaurant.restaurant.dto.request.RestaurantCreateRequest;
import com.example.woowa.restaurant.restaurant.dto.request.RestaurantUpdateRequest;
import com.example.woowa.restaurant.restaurant.dto.response.RestaurantCreateResponse;
import com.example.woowa.restaurant.restaurant.dto.response.RestaurantFindResponse;
import com.example.woowa.restaurant.restaurant.service.RestaurantService;
import com.example.woowa.security.configuration.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
@WebMvcTest(value = RestaurantRestController.class, excludeFilters = {
        @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class
        )
})
@Import(RestDocsConfiguration.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser
class RestaurantRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RestaurantService restaurantService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("레스토랑을 생성한다.")
    void createRestaurantByOwnerIdOkTest() throws Exception {
        // Given
        long ownerId = 1L;
        RestaurantCreateRequest request = new RestaurantCreateRequest(
                "레스토랑이름",
                "760-15-00993",
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                false,
                "010-1234-5678",
                "테스트용 레스토랑",
                "서울특별시 강남구",
                List.of()
        );

        RestaurantCreateResponse response = new RestaurantCreateResponse(
                1L,
                ownerId,
                "레스토랑 이름",
                "760-15-00993",
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                false,
                "010-1234-5678",
                "테스트용 레스토랑",
                "서울특별시 강남구",
                List.of(),
                LocalDateTime.now()
        );

        given(restaurantService.createRestaurantByOwnerId(eq(ownerId), any())).willReturn(response);

        // When & Then
        mockMvc.perform(post("/baemin/v1/owners/{ownerId}/restaurants", ownerId)
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.ownerId").value(response.getOwnerId()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.businessNumber").value(response.getBusinessNumber()));

        then(restaurantService).should().createRestaurantByOwnerId(eq(ownerId), any());
    }

    @Test
    @DisplayName("레스토랑을 사장님을 사용하여 조회한다.")
    void findAllRestaurantsByOwnerIdOkTest() throws Exception {
        long ownerId1 = 1L;
        long ownerId2 = 2L;
        List<RestaurantFindResponse> responses = List.of(
                new RestaurantFindResponse(
                        ownerId1,
                        101L,
                        "맛있는 레스토랑",
                        "123-45-67890",
                        LocalTime.of(9, 0),
                        LocalTime.of(21, 0),
                        true,
                        "010-1234-5678",
                        "깔끔한 레스토랑입니다.",
                        4.5,
                        120,
                        "서울특별시 강남구",
                        List.of("한식", "분식"),
                        LocalDateTime.of(2025, 1, 8, 10, 0),
                        LocalDateTime.of(2025, 1, 9, 18, 30)
                ),
                new RestaurantFindResponse(
                        ownerId2,
                        102L,
                        "편안한 레스토랑",
                        "987-65-43210",
                        LocalTime.of(11, 0),
                        LocalTime.of(23, 0),
                        false,
                        "010-9876-5432",
                        "저렴한 가격으로 제공됩니다.",
                        3.8,
                        50,
                        "서울특별시 종로구",
                        List.of("중식", "양식"),
                        LocalDateTime.of(2025, 1, 7, 8, 0),
                        LocalDateTime.of(2025, 1, 7, 18, 0)
                )
        );

        given(restaurantService.findRestaurantsByOwnerId(ownerId1)).willReturn(responses);

        mockMvc.perform(get("/baemin/v1/owners/{ownerId}/restaurants", ownerId1)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        then(restaurantService).should().findRestaurantsByOwnerId(ownerId1);
    }

    @Test
    @DisplayName("레스토랑을 업데이트한다.")
    void updateRestaurantByOwnerIdAndRestaurantIdOkTest() throws Exception {
        long ownerId = 1L;
        long restaurantId = 2L;
        RestaurantUpdateRequest request = new RestaurantUpdateRequest(
                LocalTime.of(11, 0),
                LocalTime.of(23, 0),
                "010-1111-1111",
                "서울시 동작구",
                "테스트용 가게입니다.");

        doNothing().when(restaurantService).updateRestaurantById(eq(ownerId), eq(restaurantId), refEq(request));

        mockMvc.perform(put("/baemin/v1/owners/{ownerId}/restaurants/{restaurantId}", ownerId, restaurantId)
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        then(restaurantService).should().updateRestaurantById(eq(ownerId), eq(restaurantId), refEq(request));
    }

    @Test
    @DisplayName("레스토랑 업데이트 시 잘못된 데이터로 인해 400 오류가 발생한다.")
    void updateRestaurantWithInvalidDataTest() throws Exception {
        long ownerId = 1L;
        long restaurantId = 2L;
        RestaurantUpdateRequest request = new RestaurantUpdateRequest(
                LocalTime.of(11, 0),
                LocalTime.of(23, 0),
                "잘못된번호",
                "",
                "테스트용 가게입니다."
        );

        mockMvc.perform(put("/baemin/v1/owners/{ownerId}/restaurants/{restaurantId}", ownerId, restaurantId)
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("레스토랑을 삭제한다.")
    void deleteRestaurantByOwnerIdAndRestaurantIdOkTest() throws Exception {
        long ownerId = 1L;
        long restaurantId = 2L;

        mockMvc.perform(delete("/baemin/v1/owners/{ownerId}/restaurants/{restaurantId}", ownerId, restaurantId)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk());

        then(restaurantService).should().deleteRestaurantByOwnerIdAndRestaurantId(ownerId, restaurantId);
    }

    @Test
    @DisplayName("레스토랑을 광고를 사용하여 조회한다.")
    void findAllRestaurantsByAdvertisementIdOkTest() throws Exception {
        long advertisementId = 1L;
        List<RestaurantFindResponse> responses = List.of(
                new RestaurantFindResponse(
                        1L,
                        1L,
                        "레스토랑 1",
                        "123-45-67890",
                        LocalTime.of(10, 0),
                        LocalTime.of(22, 0),
                        true,
                        "010-1234-5678",
                        "레스토랑 1번 입니다.",
                        4.5,
                        20,
                        "서울시 강남구",
                        List.of(),
                        LocalDateTime.now(),
                        LocalDateTime.now()),
                new RestaurantFindResponse(
                        2L,
                        1L,
                        "레스토랑 2",
                        "223-45-67890",
                        LocalTime.of(9, 0),
                        LocalTime.of(21, 0),
                        false,
                        "010-9876-5432",
                        "레스토랑 2번 입니다.",
                        4.0,
                        15,
                        "서울시 서초구",
                        List.of(),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );

        given(restaurantService.findRestaurantsByAdvertisementId(advertisementId)).willReturn(responses);

        mockMvc.perform(get("/baemin/v1/advertisements/{advertisementId}/restaurants", advertisementId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        then(restaurantService).should().findRestaurantsByAdvertisementId(advertisementId);
    }

    @Test
    @DisplayName("레스토랑을 카테고리를 사용하여 조회한다.")
    void findAllRestaurantsByCategoryIdOkTest() throws Exception {
        long categoryId = 1L;
        List<RestaurantFindResponse> responses = List.of(
                new RestaurantFindResponse(
                        1L,
                        1L,
                        "레스토랑 1",
                        "123-45-67890",
                        LocalTime.of(10, 0),
                        LocalTime.of(22, 0),
                        true,
                        "010-1234-5678",
                        "레스토랑 1번 입니다.",
                        4.5,
                        20,
                        "서울시 강남구",
                        List.of(),
                        LocalDateTime.now(),
                        LocalDateTime.now()),
                new RestaurantFindResponse(
                        2L,
                        1L,
                        "레스토랑 2",
                        "223-45-67890",
                        LocalTime.of(9, 0),
                        LocalTime.of(21, 0),
                        false,
                        "010-9876-5432",
                        "레스토랑 2번 입니다.",
                        4.0,
                        15,
                        "서울시 서초구",
                        List.of(),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );

        given(restaurantService.findRestaurantsByCategoryId(categoryId)).willReturn(responses);

        mockMvc.perform(get("/baemin/v1/categories/{categoryId}/restaurants", categoryId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        then(restaurantService).should().findRestaurantsByCategoryId(categoryId);
    }


    @Test
    @DisplayName("레스토랑을 조회한다.")
    void findAllRestaurantsOkTest() throws Exception {
        List<RestaurantFindResponse> responses = List.of(
                new RestaurantFindResponse(
                        1L,
                        1L,
                        "레스토랑 1",
                        "123-45-67890",
                        LocalTime.of(10, 0),
                        LocalTime.of(22, 0),
                        true,
                        "010-1234-5678",
                        "레스토랑 1번 입니다.",
                        4.5,
                        20,
                        "서울시 강남구",
                        List.of(),
                        LocalDateTime.now(),
                        LocalDateTime.now()),
                new RestaurantFindResponse(
                        2L,
                        1L,
                        "레스토랑 2",
                        "223-45-67890",
                        LocalTime.of(9, 0),
                        LocalTime.of(21, 0),
                        false,
                        "010-9876-5432",
                        "레스토랑 2번 입니다.",
                        4.0,
                        15,
                        "서울시 서초구",
                        List.of(),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );

        given(restaurantService.findRestaurants()).willReturn(responses);

        mockMvc.perform(get("/baemin/v1/restaurants")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        then(restaurantService).should().findRestaurants();
    }

    @Test
    @DisplayName("레스토랑을 단건 조회한다.")
    void findRestaurantByIdOkTest() throws Exception {
        long restaurantId = 1L;
        RestaurantFindResponse response = new RestaurantFindResponse(
                restaurantId,
                1L,
                "레스토랑 1",
                "123-45-67890",
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                true,
                "010-1234-5678",
                "서울시 강남구",
                4.5,
                20,
                "서울시 강남구",
                List.of("한식"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        given(restaurantService.findRestaurantById(restaurantId)).willReturn(response);

        mockMvc.perform(get("/baemin/v1/restaurants/{restaurantId}", restaurantId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        then(restaurantService).should().findRestaurantById(restaurantId);
    }

    @Test
    @DisplayName("레스토랑을 오픈한다.")
    void changeRestaurantStateOkTest() throws Exception {
        long ownerId = 1L;
        long restaurantId = 2L;
        boolean isOpen = true;

        mockMvc.perform(patch("/baemin/v1/owners/{ownerId}/restaurants/{restaurantId}", ownerId, restaurantId)
                        .param("isOpen", String.valueOf(isOpen))
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("가게 상태가 성공적으로 변경되었습니다."));

        then(restaurantService).should().changeRestaurantState(ownerId, restaurantId, isOpen);
    }

    @Test
    @DisplayName("레스토랑의 카테고리를 삭제한다.")
    void removeCategoryOkTest() throws Exception {
        long ownerId = 1L;
        long restaurantId = 2L;
        String categoryIdResponse = "3";

        given(restaurantService.removeCategory(ownerId, restaurantId, Long.parseLong(categoryIdResponse)))
                .willReturn(true);

        mockMvc.perform(patch("/baemin/v1/owners/{ownerId}/restaurants/{restaurantId}/categories/remove", ownerId, restaurantId)
                        .param("categoryId", categoryIdResponse)
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("카테고리가 성공적으로 삭제되었습니다."));

        then(restaurantService).should().removeCategory(ownerId, restaurantId, Long.parseLong(categoryIdResponse));
    }

}
