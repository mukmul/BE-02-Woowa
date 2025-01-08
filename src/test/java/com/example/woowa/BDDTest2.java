package com.example.woowa;

import com.example.woowa.admin.dto.AdminCreateRequest;
import com.example.woowa.admin.service.AdminService;
import com.example.woowa.common.exception.NotFoundException;
import com.example.woowa.customer.customer.dto.CustomerAddressCreateRequest;
import com.example.woowa.customer.customer.dto.CustomerCreateRequest;
import com.example.woowa.customer.customer.dto.CustomerFindResponse;
import com.example.woowa.customer.customer.dto.CustomerGradeCreateRequest;
import com.example.woowa.customer.customer.service.CustomerGradeService;
import com.example.woowa.customer.customer.service.CustomerService;
import com.example.woowa.customer.voucher.dto.VoucherFindResponse;
import com.example.woowa.customer.voucher.service.VoucherService;
import com.example.woowa.delivery.dto.DeliveryResponse;
import com.example.woowa.delivery.dto.RiderCreateRequest;
import com.example.woowa.delivery.entity.AreaCode;
import com.example.woowa.delivery.enums.DeliveryStatus;
import com.example.woowa.delivery.repository.AreaCodeRepository;
import com.example.woowa.delivery.service.DeliveryService;
import com.example.woowa.delivery.service.RiderService;
import com.example.woowa.order.order.dto.cart.CartSaveRequest;
import com.example.woowa.order.order.dto.customer.OrderListCustomerRequest;
import com.example.woowa.order.order.dto.customer.OrderSaveRequest;
import com.example.woowa.order.order.dto.restaurant.OrderAcceptRequest;
import com.example.woowa.order.order.dto.restaurant.OrderListRestaurantRequest;
import com.example.woowa.order.order.dto.statistics.OrderStatisticsRequest;
import com.example.woowa.order.order.enums.PaymentType;
import com.example.woowa.order.order.service.OrderService;
import com.example.woowa.order.review.dto.ReviewCreateRequest;
import com.example.woowa.order.review.dto.ReviewFindResponse;
import com.example.woowa.order.review.service.ReviewService;
import com.example.woowa.restaurant.advertisement.dto.request.AdvertisementCreateRequest;
import com.example.woowa.restaurant.advertisement.dto.request.AdvertisementUpdateRequest;
import com.example.woowa.restaurant.advertisement.dto.response.AdvertisementCreateResponse;
import com.example.woowa.restaurant.advertisement.dto.response.AdvertisementFindResponse;
import com.example.woowa.restaurant.advertisement.enums.RateType;
import com.example.woowa.restaurant.advertisement.enums.UnitType;
import com.example.woowa.restaurant.advertisement.service.AdvertisementService;
import com.example.woowa.restaurant.category.dto.request.CategoryCreateRequest;
import com.example.woowa.restaurant.category.dto.response.CategoryFindResponse;
import com.example.woowa.restaurant.category.service.CategoryService;
import com.example.woowa.restaurant.menu.dto.MenuSaveRequest;
import com.example.woowa.restaurant.menu.service.MenuService;
import com.example.woowa.restaurant.menugroup.dto.MenuGroupSaveRequest;
import com.example.woowa.restaurant.menugroup.service.MenuGroupService;
import com.example.woowa.restaurant.owner.dto.request.OwnerCreateRequest;
import com.example.woowa.restaurant.owner.dto.response.OwnerFindResponse;
import com.example.woowa.restaurant.owner.service.OwnerService;
import com.example.woowa.restaurant.restaurant.dto.request.RestaurantCreateRequest;
import com.example.woowa.restaurant.restaurant.dto.response.RestaurantCreateResponse;
import com.example.woowa.restaurant.restaurant.dto.response.RestaurantFindResponse;
import com.example.woowa.restaurant.restaurant.service.RestaurantService;
import com.example.woowa.security.role.entity.Role;
import com.example.woowa.security.role.repository.RoleRepository;
import com.example.woowa.security.user.entity.UserRole;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class BDDTest2 {

    @Autowired
    AdminService adminService;

    @Autowired
    AdvertisementService advertisementService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    OwnerService ownerService;

    @Autowired
    CustomerService customerService;

    @Autowired
    CustomerGradeService customerGradeService;

    @Autowired
    RiderService riderService;

    @Autowired
    VoucherService voucherService;

    @Autowired
    OrderService orderService;

    @Autowired
    RestaurantService restaurantService;

    @Autowired
    DeliveryService deliveryService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AreaCodeRepository areaCodeRepository;

    @Autowired
    MenuService menuService;

    @Autowired
    MenuGroupService menuGroupService;

    @Autowired
    ReviewService reviewService;

    RestaurantCreateResponse restaurant;
    AdvertisementCreateResponse advertisement;
    Long menuGroupId;
    Long menuId;
    CustomerFindResponse customer;
    Long orderId;
    Long areaCodeId;
    Long riderId;
    Long deliveryId;

    @Test
    @DisplayName("관리자를 생성한다.")
    @Order(0)
    void _0() {
        AdminCreateRequest adminCreateRequest = new AdminCreateRequest("admin11111", "admin11111!");
        adminService.createAdmin(adminCreateRequest);

        roleRepository.save(new Role(UserRole.ROLE_OWNER.toString()));
        roleRepository.save(new Role(UserRole.ROLE_ADMIN.toString()));
        roleRepository.save(new Role(UserRole.ROLE_RIDER.toString()));
        roleRepository.save(new Role(UserRole.ROLE_CUSTOMER.toString()));
    }

    @Test
    @DisplayName("카테고리를 생성한다.")
    @Order(1)
    void _1() {
        CategoryCreateRequest western = new CategoryCreateRequest("양식");
        CategoryCreateRequest japanese = new CategoryCreateRequest("일식");
        categoryService.createCategory(western);
        categoryService.createCategory(japanese);
    }


    @Test
    @DisplayName("사장님이 회원가입한다.")
    @Order(2)
    void _2() {
        OwnerCreateRequest ownerCreateRequest = new OwnerCreateRequest("owner11111",
            "owner11111!", "사장님", "010-1234-1234");
        ownerService.createOwner(ownerCreateRequest);
    }

    @Test
    @DisplayName("사장님이 가게를 생성한다.")
    @Order(3)
    void _3() {
        List<OwnerFindResponse> owners = ownerService.findOwners();
        List<Long> categoryIds = categoryService.findCategories().stream()
            .map(CategoryFindResponse::getId).collect(Collectors.toList());
        RestaurantCreateRequest restaurantCreateRequest = new RestaurantCreateRequest("test 가게 2",
            "760-15-00993", LocalTime.now(), LocalTime.now().plusHours(5), true, "010-1111-1234",
            "테스트용 가게", "서울특별시 동작구 상도동", categoryIds);
        restaurant = restaurantService.createRestaurantByOwnerId(
            owners.getFirst().getId(), restaurantCreateRequest);
    }

    @Test
    @DisplayName("어드민이 가게 등록을 승인한다.")
    @Order(4)
    void _4() {
        List<RestaurantFindResponse> restauransNotPermitted = restaurantService.findRestaurantsIsPermittedIsFalse();
        Long newRestaurant = restauransNotPermitted.getLast().getId();
        adminService.permitRestaurant(newRestaurant);
    }

    @Test
    @DisplayName("어드민이 광고 상품을 등록한다")
    @Order(5)
    void _5() {
        AdvertisementCreateRequest specialEvent = new AdvertisementCreateRequest("배달 특가 이벤트",
                UnitType.MONTHLY.getType(),
                RateType.FLAT.getType(),
                100000,
                "테스트용 배달 이벤트 광고",
                20);
        AdvertisementCreateRequest openList = new AdvertisementCreateRequest(
                "오픈리스트2",
                UnitType.PER_ORDER.getType(),
                RateType.PERCENT.getType(),
                10,
                "오픈리스트 광고",
                10);
        advertisement = advertisementService.createAdvertisement(specialEvent);
        System.out.println("등록된 advertisement ID: " + advertisement.getId());
        assertThat(advertisement.getTitle()).isEqualTo("배달 특가 이벤트");
        assertThat(advertisement.getDescription()).isEqualTo("테스트용 배달 이벤트 광고");
        advertisementService.createAdvertisement(openList);
    }


    @Test
    @DisplayName("어드민이 광고 정보를 수정한다.")
    @Order(6)
    void _6() {
        AdvertisementFindResponse advertisementToUpdate = advertisementService.findAdvertisements()
                .stream()
                .filter(ad -> ad.getTitle().equals("배달 특가 이벤트"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("광고가 등록되지 않았습니다."));

        AdvertisementUpdateRequest updateRequest = new AdvertisementUpdateRequest(
                "수정된 광고 이름2",
                UnitType.PER_ORDER.getType(),
                RateType.PERCENT.getType(),
                15,
                "수정된 광고 설명"
        );
        Long advertisementId = advertisementToUpdate.getId();
        advertisementService.updateAdvertisementById(advertisementId, updateRequest);

        AdvertisementFindResponse updatedAdvertisement = advertisementService.findAdvertisementById(advertisementId);
        assertThat(updatedAdvertisement.getTitle()).isEqualTo("수정된 광고 이름2");
        assertThat(updatedAdvertisement.getDescription()).isEqualTo("수정된 광고 설명");
    }

    @Test
    @DisplayName("광고에 가게를 추가한다.")
    @Order(7)
    void _7() {
        Long restaurantId = restaurant.getId();  // 기존에 생성된 가게 ID 사용
        System.out.println("restaurantId" + restaurantId);
        Long advertisementId = advertisement.getId();
        advertisementService.includeRestaurantInAdvertisement(advertisementId, restaurantId);

        AdvertisementFindResponse advertisementWithRestaurant = advertisementService.findAdvertisementById(advertisementId);
        assertThat(advertisementWithRestaurant.getCurrentSize()).isGreaterThan(0);  // 광고에 가게가 추가된 것을 확인
    }

    @Test
    @DisplayName("광고에서 가게를 제거한다.")
    @Order(8)
    void _8() {
        Long advertisementId = advertisement.getId();
        Long restaurantId = restaurant.getId();  // 기존에 추가한 가게 ID 사용
        advertisementService.excludeRestaurantOutOfAdvertisement(advertisementId, restaurantId);

        AdvertisementFindResponse advertisementWithoutRestaurant = advertisementService.findAdvertisementById(advertisementId);
        assertThat(advertisementWithoutRestaurant.getCurrentSize()).isEqualTo(0);  // 가게가 제거된 것을 확인
    }

    @Test
    @DisplayName("광고를 삭제한다.")
    @Order(9)
    void _9() {
        Long advertisementId = 2L;
        advertisementService.deleteAdvertisementById(advertisementId);

        assertThatThrownBy(() -> advertisementService.findAdvertisementById(advertisementId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("광고를 삭제했을 때 연결된 데이터도 삭제되었는지 확인한다.")
    @Order(10)
    void _10() {
        // Given: 가게와 광고 생성 및 등록

        RestaurantCreateRequest restaurantCreateRequest = new RestaurantCreateRequest(
                "Test Restaurant4", "760-15-00993",
                LocalTime.of(9, 0), LocalTime.of(22, 0), true,
                "010-1234-5678", "테스트용 가게", "서울특별시 강남구", List.of(1L)
        );
        Long restaurantId = restaurantService.createRestaurantByOwnerId(1L, restaurantCreateRequest).getId();

        AdvertisementCreateRequest advertisementCreateRequest = new AdvertisementCreateRequest(
                "울트라콜 광고666", UnitType.MONTHLY.getType(), RateType.FLAT.getType(),
                88000, "가게 광고 테스트", 10
        );
        Long advertisementId = advertisementService.createAdvertisement(advertisementCreateRequest).getId();

        advertisementService.includeRestaurantInAdvertisement(advertisementId, restaurantId);

        // When: 광고 삭제
        advertisementService.deleteAdvertisementById(advertisementId);

    }
}
