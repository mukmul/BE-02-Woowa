package com.example.woowa.admin.controller;

import static org.mockito.ArgumentMatchers.any;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.woowa.RestDocsConfiguration;
import com.example.woowa.admin.converter.AdminMapper;
import com.example.woowa.admin.dto.AdminCreateRequest;
import com.example.woowa.admin.dto.AdminUpdateRequest;
import com.example.woowa.admin.service.AdminService;
import com.example.woowa.security.configuration.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs
@WebMvcTest(value = AdminController.class, excludeFilters = {
    @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = SecurityConfig.class
    ),
})
@ExtendWith(MockitoExtension.class)
@Import(RestDocsConfiguration.class)
@WithMockUser
class AdminControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  AdminMapper adminMapper = Mappers.getMapper(AdminMapper.class);

  @MockitoBean
  private AdminService adminService;

  @Test
  void createAdmin() throws Exception {
    AdminCreateRequest adminCreateRequest = new AdminCreateRequest("dev12", "Programmers12!");

    given(adminService.createAdmin(any())).willReturn(adminMapper.toAdminDto(adminMapper.toAdmin(adminCreateRequest)));

    mockMvc.perform(
            post("/api/v1/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminCreateRequest))
                .with(csrf().asHeader())
        )
        .andExpect(status().isCreated()) // isOk -> isCreated
            // Cause: AdminController에서 createAdmin 메서드가 리소스 생성에 성공하면 201(Created)을 반환하고 있습니다. 그러나 테스트에서는 200(OK)를 예상하고 있습니다.
        .andDo(print())
        .andDo(document("admins-create",
            requestFields(
                fieldWithPath("loginId").type(JsonFieldType.STRING).description("아이디"),
                fieldWithPath("loginPassword").type(JsonFieldType.STRING).description("비밀번호")
            ),
            responseFields(
                fieldWithPath("loginId").type(JsonFieldType.STRING).description("생성된 아이디")
            )
        ));
  }

  @Test
  void getAdminInfoByLoginId() throws Exception {
    AdminCreateRequest adminCreateRequest = new AdminCreateRequest("dev12", "Programmers12!");

    given(adminService.getAdminInfoByLoginId(any())).willReturn(adminMapper.toAdminDto(adminMapper.toAdmin(adminCreateRequest)));

    mockMvc.perform(
            get("/api/v1/admins/{loginId}",adminCreateRequest.getLoginId())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
        )
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("admins-find",
            pathParameters(
                parameterWithName("loginId").description("조회할 관리자 로그인 ID")
            ),
            responseFields(
                fieldWithPath("loginId").type(JsonFieldType.STRING).description("생성된 아이디")
            )
        ));
  }

  @Test
  void updateAdmin() throws Exception {
    AdminCreateRequest adminCreateRequest = new AdminCreateRequest("dev12", "Programmers12!");

    given(adminService.updateAdminPassword(anyString(), any())).willReturn(adminMapper.toAdminDto(adminMapper.toAdmin(adminCreateRequest)));

    AdminUpdateRequest adminUpdateRequest = new AdminUpdateRequest("Programmers123!");
    mockMvc.perform(
            put("/api/v1/admins/{loginId}", adminCreateRequest.getLoginId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminUpdateRequest))
                .with(csrf().asHeader())
        )
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("admins-update",
            pathParameters(
                parameterWithName("loginId").description("수정할 관리자 로그인 ID")
            ),
            requestFields(
                fieldWithPath("loginPassword").type(JsonFieldType.STRING).description("수정하려는 비밀 번호")
            ),
            responseFields(
                fieldWithPath("loginId").type(JsonFieldType.STRING).description("정보가 수정된 계정의 아이디")
            )
        ));
  }

  @Test
  void deleteAdmin() throws Exception {
    mockMvc.perform(
            delete("/api/v1/admins/{loginId}", "dev12")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
        )
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("admins-delete",
            pathParameters(
                parameterWithName("loginId").description("삭제할 관리자 로그인 ID")
            )
        ));
  }
}