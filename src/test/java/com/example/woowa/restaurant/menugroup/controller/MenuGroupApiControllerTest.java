package com.example.woowa.restaurant.menugroup.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.woowa.RestDocsConfiguration;
import com.example.woowa.common.exception.NotFoundException;
import com.example.woowa.restaurant.menugroup.dto.MenuGroupListResponse;
import com.example.woowa.restaurant.menugroup.dto.MenuGroupResponse;
import com.example.woowa.restaurant.menugroup.dto.MenuGroupSaveRequest;
import com.example.woowa.restaurant.menugroup.dto.MenuGroupUpdateRequest;
import com.example.woowa.restaurant.menugroup.service.MenuGroupService;
import com.example.woowa.security.configuration.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs
@WebMvcTest(value = MenuGroupApiController.class, excludeFilters = {
        @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class
        )
})
@Import(RestDocsConfiguration.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser
class MenuGroupApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    MenuGroupService menuGroupService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("레스토랑에 메뉴 그룹을 추가한다.")
    void addMenuGroupOkTest() throws Exception {
        MenuGroupSaveRequest menuGroupSaveRequest = new MenuGroupSaveRequest("볶음밥류", "맛있는 볶음밥");
        long restaurantId = 1L;
        long menuGroupId = 2L;

        given(menuGroupService.addMenuGroup(restaurantId, menuGroupSaveRequest)).willReturn(
                menuGroupId);

        mockMvc.perform(post("/api/v1/restaurant/{restaurantId}/menu-groups", restaurantId)
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menuGroupSaveRequest)))
                .andDo(print())
                .andExpect(status().isCreated());

        then(menuGroupService).should().addMenuGroup(restaurantId, menuGroupSaveRequest);
    }

    @Test
    @DisplayName("메뉴 그룹명을 누락하면 상태코드 400 응답이 발생한다.")
    void addMenuGroupEmptyTitleTest() throws Exception {
        long restaurantId = 1L;
        MenuGroupSaveRequest menuGroupSaveRequest = new MenuGroupSaveRequest("", "맛있는 볶음밥");

        mockMvc.perform(post("/api/v1/restaurant/{restaurantId}/menu-groups", restaurantId)
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menuGroupSaveRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        then(menuGroupService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("존재하지 않는 레스토랑에 메뉴 그룹을 추가하려 하면 상태코드 404 응답이 발생한다.")
    void addMenuGroupNotFoundRestaurantTest() throws Exception {
        MenuGroupSaveRequest request = new MenuGroupSaveRequest("볶음밥류", "맛있는 볶음밥");
        long wrongRestaurantId = -1L;
        given(menuGroupService.addMenuGroup(wrongRestaurantId, request)).willThrow(
                NotFoundException.class);

        mockMvc.perform(post("/api/v1/restaurant/{restaurantId}/menu-groups", wrongRestaurantId)
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound());

        then(menuGroupService).should().addMenuGroup(wrongRestaurantId, request);
    }

    @Test
    @DisplayName("메뉴 그룹을 단건 조회한다.")
    void findMenuGroupOkTest() throws Exception {
        long menuGroupId = 1L;
        MenuGroupResponse response = new MenuGroupResponse(menuGroupId, "김밥류", "맛있는 김밥류");
        given(menuGroupService.findMenuById(menuGroupId)).willReturn(response);

        mockMvc.perform(get("/api/v1/menu-groups/{menuGroupId}", menuGroupId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(menuGroupId))
                .andExpect(jsonPath("title").value(response.getTitle()))
                .andExpect(jsonPath("description").value(response.getDescription()));

        then(menuGroupService).should().findMenuById(menuGroupId);
    }

    @Test
    @DisplayName("존재하지 않는 메뉴 그룹을 단건 조회하면 상태코드 404 응답이 발생한다.")
    void findMenuGroupNotFoundTest() throws Exception {
        long wrongMenuGroupId = -1L;
        given(menuGroupService.findMenuById(wrongMenuGroupId)).willThrow(NotFoundException.class);

        mockMvc.perform(get("/api/v1/menu-groups/{menuGroupId}", wrongMenuGroupId))
                .andDo(print())
                .andExpect(status().isNotFound());

        then(menuGroupService).should().findMenuById(wrongMenuGroupId);
    }

    @Test
    @DisplayName("레스토랑의 메뉴 그룹을 조회한다.")
    void findMenuGroupListOkTest() throws Exception {
        long restaurantId = 1L;
        List<MenuGroupResponse> menuGroupResponses = List.of(
                new MenuGroupResponse(1L, "김밥류", "맛있는 김밥"),
                new MenuGroupResponse(2L, "찌개류", "맛있는 찌개"),
                new MenuGroupResponse(2L, "면류", "맛있는 면")
        );

        given(menuGroupService.findMenuGroupByRestaurant(restaurantId)).willReturn(
                new MenuGroupListResponse(menuGroupResponses));

        mockMvc.perform(
                        get("/api/v1/restaurant/{restaurantId}/menu-groups", restaurantId)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("menuGroups.length()").value(menuGroupResponses.size()));

        then(menuGroupService).should().findMenuGroupByRestaurant(restaurantId);
    }

    @Test
    @DisplayName("존재하지 않는 레스토랑의 메뉴 그룹을 조회하면 상태코드 404 응답이 발생한다.")
    void findMenuGroupListNotFoundRestaurantTest() throws Exception {
        long wrongRestaurantId = -1L;
        given(menuGroupService.findMenuGroupByRestaurant(wrongRestaurantId)).willThrow(
                NotFoundException.class);

        mockMvc.perform(get("/api/v1/restaurant/{restaurantId}/menu-groups", wrongRestaurantId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound());

        then(menuGroupService).should().findMenuGroupByRestaurant(wrongRestaurantId);
    }

    @Test
    @DisplayName("메뉴 그룹 정보를 업데이트 한다.")
    void updateMenuGroupOkTest() throws Exception {
        long menuGroupId = 1L;
        MenuGroupUpdateRequest request = new MenuGroupUpdateRequest("사이드류",
                "맛있는 사이드 메뉴");

        mockMvc.perform(patch("/api/v1/menu-groups/{menuGroupId}", menuGroupId)
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        then(menuGroupService).should().updateMenuGroup(menuGroupId, request);
    }

    @Test
    @DisplayName("메뉴 그룹 정보를 업데이트하는데 메뉴명을 누락하면 상태코드 400 응답이 발생한다.")
    void updateMenuGroupEmptyTitleTest() throws Exception {
        long menuGroupId = 1L;
        MenuGroupUpdateRequest request = new MenuGroupUpdateRequest("",
                "맛있는 사이드 메뉴");

        mockMvc.perform(patch("/api/v1/menu-groups/{menuGroupId}", menuGroupId)
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        then(menuGroupService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("존재하지 않는 메뉴 그룹 정보를 업데이트하려 하면 상태코드 404 응답이 발생한다.")
    void updateMenuGroupNotFoundTest() throws Exception {
        long wrongMenuGroupId = -1L;
        MenuGroupUpdateRequest request = new MenuGroupUpdateRequest("사이드류",
                "맛있는 사이드 메뉴");
        doThrow(NotFoundException.class).when(menuGroupService)
                .updateMenuGroup(wrongMenuGroupId, request);

        mockMvc.perform(patch("/api/v1/menu-groups/{menuGroupId}", wrongMenuGroupId)
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound());

        then(menuGroupService).should().updateMenuGroup(wrongMenuGroupId, request);
    }

    @Test
    @DisplayName("메뉴 그룹을 삭제한다.")
    void deleteMenuGroupOkTest() throws Exception {
        long menuGroupId = 1L;

        mockMvc.perform(delete("/api/v1/menu-groups/{menuGroupId}", menuGroupId)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk());

        then(menuGroupService).should().deleteMenuGroup(menuGroupId);
    }

    @Test
    @DisplayName("메뉴 그룹 하단에 메뉴가 존재해서 409 응답이 발생한다.")
    void deleteMenuGroupWithMenusConflictTest() throws Exception {
        long menuGroupId = 1L;

        doThrow(new IllegalStateException("해당 메뉴 그룹에 메뉴가 존재하여 삭제할 수 없습니다."))
                .when(menuGroupService).deleteMenuGroup(menuGroupId);

        mockMvc.perform(delete("/api/v1/menu-groups/{menuGroupId}", menuGroupId)
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isConflict()); // 409 상태 코드

        then(menuGroupService).should().deleteMenuGroup(menuGroupId);
    }

    @Test
    @DisplayName("존재하지 않는 메뉴 그룹을 삭제하려 하면 상태코드 404 응답이 발생한다.")
    void deleteMenuGroupNotFoundTest() throws Exception {
        long wrongMenuGroupId = -1L;
        doThrow(NotFoundException.class).when(menuGroupService).deleteMenuGroup(wrongMenuGroupId);

        mockMvc.perform(delete("/api/v1/menu-groups/{menuGroupId}", wrongMenuGroupId)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isNotFound());

        then(menuGroupService).should().deleteMenuGroup(wrongMenuGroupId);
    }
}