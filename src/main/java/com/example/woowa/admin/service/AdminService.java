package com.example.woowa.admin.service;

import com.example.woowa.admin.converter.AdminMapper;
import com.example.woowa.admin.dto.AdminCreateRequest;
import com.example.woowa.admin.dto.AdminFindResponse;
import com.example.woowa.admin.dto.AdminUpdateRequest;
import com.example.woowa.admin.entity.Admin;
import com.example.woowa.admin.repository.AdminRepository;
import com.example.woowa.restaurant.restaurant.entity.Restaurant;
import com.example.woowa.restaurant.restaurant.service.RestaurantService;
import com.example.woowa.security.user.service.UserService;
import com.example.woowa.security.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AdminService {

    private final RestaurantService restaurantService;
    private final UserService userService;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminMapper adminMapper;

    @Transactional
    public AdminFindResponse createAdmin(AdminCreateRequest adminCreateRequest) {

        /**
         * loginId 중복 여부 check
         */
        boolean isExist = adminRepository.existsAdminByLoginId(adminCreateRequest.getLoginId());
        if (isExist) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        Admin admin = adminMapper.toAdmin(adminCreateRequest);
        admin.changePassword(passwordEncoder.encode(admin.getPassword()));
        admin = adminRepository.save(admin);

        userService.createUser(admin, UserRole.ROLE_ADMIN);

        return adminMapper.toAdminDto(admin);
    }

    public AdminFindResponse findAdmin(String loginId) {
        Admin admin = findAdminEntity(loginId);
        return adminMapper.toAdminDto(admin);
    }

    @Transactional
    public AdminFindResponse updateAdmin(String loginId, AdminUpdateRequest adminUpdateRequest) {
        Admin admin = findAdminEntity(loginId);
        admin.changePassword(adminUpdateRequest.getLoginPassword());
        userService.syncUser(admin);
        return adminMapper.toAdminDto(admin);
    }

    @Transactional
    public void deleteAdmin(String loginId) {
        Admin admin = findAdminEntity(loginId);
        adminRepository.delete(admin);
        userService.deleteUser(admin.getLoginId());
    }

    private Admin findAdminEntity(String loginId) {
        return adminRepository.findByLoginId(loginId).orElseThrow(()-> new RuntimeException("admin not existed"));
    }

    @Transactional
    public void permitRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantService.findRestaurantEntityById(restaurantId);
        restaurant.setPermitted();
    }

}
