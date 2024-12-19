package com.example.woowa.admin.service;
// 비즈니스 로직을 처리하는 서비스 계층으로, 컨트롤러에서 전달받은 요청을 실제로 처리
// 일반적으로 비즈니스 로직의 결과(엔티티, DTO 등)만 반환) / ResponseEntity 필요 X
import com.example.woowa.admin.converter.AdminMapper;
import com.example.woowa.admin.dto.AdminCreateRequest;
import com.example.woowa.admin.dto.AdminFindResponse;
import com.example.woowa.admin.dto.AdminUpdateRequest;
import com.example.woowa.admin.entity.Admin;
import com.example.woowa.admin.repository.AdminRepository;
import com.example.woowa.restaurant.restaurant.entity.Restaurant;
import com.example.woowa.restaurant.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AdminService {

    private final RestaurantService restaurantService;
    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    // AdminCreateRequest를 받아 새로운 관리자를 생성
    @Transactional
    public AdminFindResponse createAdmin(AdminCreateRequest adminCreateRequest) {
        Admin admin = adminMapper.toAdmin(adminCreateRequest);
        admin = adminRepository.save(admin);
        return adminMapper.toAdminDto(admin);
    }
    // 로그인 아이디로 관리자 조회 - 컨트롤러를 통해 클라이언트에게 데이터 반환
    // findAdmin -> getAdminInfoByLoginId
    public AdminFindResponse getAdminInfoByLoginId(String loginId) {
        Admin admin = getAdminByLoginId(loginId);
        return adminMapper.toAdminDto(admin);
    }
    // 로그인 아이디로 관리자를 찾아 비밀번호를 업데이트
    // updateAdmin -> updateAdminPassword
    @Transactional
    public AdminFindResponse updateAdminPassword(String loginId, AdminUpdateRequest adminUpdateRequest) {
        Admin admin = getAdminByLoginId(loginId);
        admin.changePassword(adminUpdateRequest.getLoginPassword());
        return adminMapper.toAdminDto(admin);
    }
    // 로그인 아이디로 관리자를 삭제
    // deleteAdmin -> removeAdminByLoginId
    @Transactional
    public void removeAdminByLoginId(String loginId) {
        Admin admin = getAdminByLoginId(loginId);
        adminRepository.delete(admin);
    }
    // 로그인 아이디로 관리자 정보를 조회 - 비즈니스 로직에서 엔티티 조작에 사용 - 외부에 노출되지 않는 내부 처리용
    // findAdminEntity -> getAdminByLoginId
    private Admin getAdminByLoginId(String loginId) {
        return adminRepository.findByLoginId(loginId).orElseThrow(()-> new RuntimeException("admin not existed"));
    }
    // 특정 식당에 권한을 부여
    // permitRestaurant -> authorizeRestaurant
    @Transactional
    public void authorizeRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantService.findRestaurantEntityById(restaurantId);
        restaurant.setPermitted();
    }
}
