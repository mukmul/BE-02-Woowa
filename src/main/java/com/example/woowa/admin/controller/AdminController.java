package com.example.woowa.admin.controller;
// HTTP 요청을 처리하는 컨트롤러로, 관리자를 생성, 조회, 수정, 삭제, 특정 작업(식당 권한 부여)을 수행할 수 있는 엔드포인트(API)를 제공

import com.example.woowa.admin.dto.AdminCreateRequest;
import com.example.woowa.admin.dto.AdminFindResponse;
import com.example.woowa.admin.dto.AdminUpdateRequest;
import com.example.woowa.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admins")
public class AdminController {
  private final AdminService adminService;

  // 관리자 생성
  @PostMapping
  // ResponseEntity<>로 감싸기
  // 예외처리 추가하기
  public AdminFindResponse createAdmin(@RequestBody @Valid AdminCreateRequest adminCreateRequest) {
    return adminService.createAdmin(adminCreateRequest);
  }
  // 특정 로그인 아이디에 해당하는 관리자 조회
  @GetMapping("/{loginId}")
  public AdminFindResponse findAdmin(@PathVariable String loginId) {
    return adminService.findAdmin(loginId);
  }

  // 특정 로그인 아이디의 관리자 정보를 수정 (비밀번호)
  @PutMapping("/{loginId}")
  public AdminFindResponse updateAdmin(@PathVariable String loginId, @RequestBody @Valid
      AdminUpdateRequest adminUpdateRequest) {
    return adminService.updateAdmin(loginId, adminUpdateRequest);
  }

  // 특정 로그인 아이디의 관리자를 삭제
  @DeleteMapping("/{loginId}")
  public String deleteAdmin(@PathVariable String loginId) {
    adminService.deleteAdmin(loginId);
    return "delete id - " + loginId;
  }

  // 특정 식당에 권한을 부여
  @PatchMapping("/permit/restaurants/{restaurantId}")
  public String permitRestaurant(@PathVariable Long restaurantId) {
    adminService.permitRestaurant(restaurantId);
    return "restaurant id(" + restaurantId + ") permitted.";
  }

}

// 전체 동작 흐름
//관리자 생성
//POST /api/v1/admins → AdminController → AdminService의 createAdmin 호출
//DTO(AdminCreateRequest) → 엔티티(Admin) 변환 후, DB에 저장.
//변환 후 결과를 DTO(AdminFindResponse)로 반환.

//관리자 조회
//GET /api/v1/admins/{loginId} → AdminController → AdminService의 findAdmin 호출
//로그인 아이디로 관리자 조회 후, DTO로 반환.

//관리자 수정
//PUT /api/v1/admins/{loginId} → AdminController → AdminService의 updateAdmin 호출
//비밀번호를 수정하고, 변환 후 DTO로 반환.

//관리자 삭제
//DELETE /api/v1/admins/{loginId} → AdminController → AdminService의 deleteAdmin 호출
//로그인 아이디로 관리자 조회 후, 삭제 수행.

//식당 권한 부여
//PATCH /api/v1/admins/permit/restaurants/{restaurantId} → AdminController → AdminService의 permitRestaurant 호출
//특정 식당에 권한을 부여합니다.
