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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admins")
public class AdminController {
  // 로그 추가
  private static final Logger log = LoggerFactory.getLogger(AdminController.class);
  private final AdminService adminService;

  // 관리자 생성
  @PostMapping
  public ResponseEntity<AdminFindResponse> createAdmin(@RequestBody @Valid AdminCreateRequest adminCreateRequest) {
    log.info("Received request to create admin with loginId: {}", adminCreateRequest.getLoginId());
    AdminFindResponse response = adminService.createAdmin(adminCreateRequest);
    log.info("Successfully created admin with loginId: {}", adminCreateRequest.getLoginId());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
  // 특정 로그인 아이디에 해당하는 관리자 조회
  @GetMapping("/{loginId}")
  public ResponseEntity<AdminFindResponse> getAdminInfoByLoginId(@PathVariable String loginId) {
    log.info("Fetching admin info for loginId: {}", loginId);
    AdminFindResponse response = adminService.getAdminInfoByLoginId(loginId);
    log.info("Successfully fetched admin info for loginId: {}", loginId);
    return ResponseEntity.ok(response);
  }

  // 특정 로그인 아이디의 관리자 정보를 수정 (비밀번호)
  @PutMapping("/{loginId}")
  public ResponseEntity<AdminFindResponse> updateAdminPassword(@PathVariable String loginId, @RequestBody @Valid AdminUpdateRequest adminUpdateRequest) {
    log.info("Received request to update password for admin with loginId: {}", loginId);
    AdminFindResponse response = adminService.updateAdminPassword(loginId, adminUpdateRequest);
    log.info("Successfully updated password for admin with loginId: {}", loginId);
    return ResponseEntity.ok(response);
  }

  // 특정 로그인 아이디의 관리자를 삭제
  @DeleteMapping("/{loginId}")
  public ResponseEntity<String> removeAdminByLoginId(@PathVariable String loginId) {
    log.info("Received request to delete admin with loginId: {}", loginId);
    adminService.removeAdminByLoginId(loginId);
    log.info("Successfully deleted admin with loginId: {}", loginId);
    return ResponseEntity.ok("delete id - " + loginId);
  }


  // 특정 식당에 권한을 부여
  @PatchMapping("/permit/restaurants/{restaurantId}")
  public ResponseEntity<String> authorizeRestaurant(@PathVariable Long restaurantId) {
    log.info("Received request to authorize restaurant with id: {}", restaurantId);
    adminService.authorizeRestaurant(restaurantId);
    log.info("Successfully authorized restaurant with id: {}", restaurantId);
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
