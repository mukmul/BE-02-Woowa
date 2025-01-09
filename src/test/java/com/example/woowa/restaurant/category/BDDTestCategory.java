package com.example.woowa.restaurant.category;

import com.example.woowa.admin.dto.AdminCreateRequest;
import com.example.woowa.admin.service.AdminService;
import com.example.woowa.restaurant.category.dto.request.CategoryCreateRequest;
import com.example.woowa.restaurant.category.dto.request.CategoryUpdateRequest;
import com.example.woowa.restaurant.category.dto.response.CategoryFindResponse;
import com.example.woowa.restaurant.category.service.CategoryService;
import com.example.woowa.restaurant.owner.dto.request.OwnerCreateRequest;
import com.example.woowa.restaurant.owner.dto.response.OwnerFindResponse;
import com.example.woowa.restaurant.owner.service.OwnerService;
import com.example.woowa.restaurant.restaurant.dto.request.RestaurantCreateRequest;
import com.example.woowa.restaurant.restaurant.dto.response.RestaurantCreateResponse;
import com.example.woowa.restaurant.restaurant.service.RestaurantService;
import com.example.woowa.security.role.entity.Role;
import com.example.woowa.security.role.repository.RoleRepository;
import com.example.woowa.security.user.entity.UserRole;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BDDTestCategory {

    @Autowired
    AdminService adminService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CategoryService categoryService;

    @Autowired
    OwnerService ownerService;

    @Autowired
    RestaurantService restaurantService;

    RestaurantCreateResponse restaurant;

    @Test
    @DisplayName("관리자를 생성한다.")
    @Order(1)
    void _1() {
        roleRepository.save(new Role(UserRole.ROLE_OWNER.getRoleName()));
        roleRepository.save(new Role(UserRole.ROLE_ADMIN.getRoleName()));
        roleRepository.save(new Role(UserRole.ROLE_RIDER.getRoleName()));
        roleRepository.save(new Role(UserRole.ROLE_CUSTOMER.getRoleName()));

        AdminCreateRequest adminCreateRequest = new AdminCreateRequest("admin11111", "admin11111!");
        adminService.createAdmin(adminCreateRequest);
    }

    @Test
    @DisplayName("카테고리를 생성한다.")
    @Order(2)
    void _2() {
        CategoryCreateRequest korean = new CategoryCreateRequest("한식");
        CategoryCreateRequest chinese = new CategoryCreateRequest("중식");
        CategoryCreateRequest japanese = new CategoryCreateRequest("일식");
        categoryService.createCategory(korean);
        categoryService.createCategory(chinese);
        categoryService.createCategory(japanese);
    }


    @Test
    @DisplayName("중복된 이름으로 카테고리를 생성 시 예외를 발생시킨다.")
    @Order(3)
    void _3() {
        CategoryCreateRequest duplicateKorean = new CategoryCreateRequest("한식");

        assertThatThrownBy(() -> categoryService.createCategory(duplicateKorean))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 카테고리 이름입니다.");
    }

    @Test
    @DisplayName("카테고리 단건을 조회한다.")
    @Order(4)
    void _4() {
        List<CategoryFindResponse> categories = categoryService.findCategories();
        CategoryFindResponse category = categories.getFirst(); // 첫 번째 카테고리 조회

        CategoryFindResponse foundCategory = categoryService.findCategoryById(category.getId());
        assertThat(foundCategory.getName()).isEqualTo(category.getName());
    }

    @Test
    @DisplayName("없는 카테고리를 단건 조회 시 예외 발생시킨다.")
    @Order(5)
    void _5() {
        Long nonExistingCategoryId = 999L;
        assertThatThrownBy(() -> categoryService.findCategoryById(nonExistingCategoryId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("존재하지 않는 카테고리 ID");
    }

    @Test
    @DisplayName("카테고리 전체를 조회한다.")
    @Order(6)
    void _6() {
        List<CategoryFindResponse> categories = categoryService.findCategories();
        assertThat(categories.size()).isGreaterThanOrEqualTo(3); // 최소 3개의 카테고리 생성
    }

    @Test
    @DisplayName("카테고리를 변경한다.")
    @Order(7)
    void _7() {
        List<CategoryFindResponse> categories = categoryService.findCategories();

        assertThat(categories).isNotEmpty();

        CategoryFindResponse categoryToUpdate = categories.getFirst();

        CategoryUpdateRequest updateRequest = new CategoryUpdateRequest("업데이트 카테고리");
        CategoryFindResponse updatedCategory = categoryService.updateCategoryById(categoryToUpdate.getId(), updateRequest);

        assertThat(updatedCategory.getName()).isEqualTo("업데이트 카테고리");
    }

    @Test
    @DisplayName("사장님이 회원가입한다.")
    @Order(8)
    void _8() {
        OwnerCreateRequest ownerCreateRequest = new OwnerCreateRequest("owner11111",
                "owner11111!", "사장님", "010-1234-1234");
        ownerService.createOwner(ownerCreateRequest);
    }

    @Test
    @DisplayName("사장님이 가게를 생성한다.")
    @Order(9)
    void _9() {
        List<OwnerFindResponse> owners = ownerService.findOwners();
        List<Long> categoryIds = categoryService.findCategories().stream()
                .map(CategoryFindResponse::getId).collect(Collectors.toList());
        RestaurantCreateRequest restaurantCreateRequest = new RestaurantCreateRequest("test 가게",
                "760-15-00993", LocalTime.now(), LocalTime.now().plusHours(5), true, "010-1111-1234",
                "테스트용 가게", "서울특별시 동작구 상도동", categoryIds);
        restaurant = restaurantService.createRestaurantByOwnerId(
                owners.getFirst().getId(), restaurantCreateRequest);
    }
}
