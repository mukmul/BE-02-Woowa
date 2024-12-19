package com.example.woowa.admin.controller;
// HTTP 요청을 처리하는 컨트롤러로, 관리자를 생성, 조회, 수정, 삭제, 특정 작업(식당 권한 부여)을 수행할 수 있는 엔드포인트(API)를 제공
// ResponseEntity<> 수정 완료
import com.example.woowa.admin.dto.AdminCreateRequest;
import com.example.woowa.admin.dto.AdminFindResponse;
import com.example.woowa.admin.dto.AdminUpdateRequest;
import com.example.woowa.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<AdminFindResponse> createAdmin(@RequestBody @Valid AdminCreateRequest adminCreateRequest) {
    AdminFindResponse response = adminService.createAdmin(adminCreateRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
  // 특정 로그인 아이디에 해당하는 관리자 조회
  @GetMapping("/{loginId}")
  public ResponseEntity<AdminFindResponse> findAdmin(@PathVariable String loginId) {
    AdminFindResponse response = adminService.findAdmin(loginId);
    return ResponseEntity.ok(response);
  }

  // 특정 로그인 아이디의 관리자 정보를 수정 (비밀번호)
  @PutMapping("/{loginId}")
  public ResponseEntity<AdminFindResponse> updateAdmin(@PathVariable String loginId, @RequestBody @Valid AdminUpdateRequest adminUpdateRequest) {
    AdminFindResponse response = adminService.updateAdmin(loginId, adminUpdateRequest);
    return ResponseEntity.ok(response);
  }

  // 특정 로그인 아이디의 관리자를 삭제
  @DeleteMapping("/{loginId}")
  public ResponseEntity<String> deleteAdmin(@PathVariable String loginId) {
    adminService.deleteAdmin(loginId);
    return ResponseEntity.ok("delete id - " + loginId);
  }


  // 특정 식당에 권한을 부여
  @PatchMapping("/permit/restaurants/{restaurantId}")
  public ResponseEntity<String> permitRestaurant(@PathVariable Long restaurantId) {
    adminService.permitRestaurant(restaurantId);
    return ResponseEntity.ok("restaurant id(" + restaurantId + ") permitted.");
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
