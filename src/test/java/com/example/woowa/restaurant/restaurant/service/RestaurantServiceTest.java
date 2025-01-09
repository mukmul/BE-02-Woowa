package com.example.woowa.restaurant.restaurant.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

import com.example.woowa.common.exception.NotFoundException;
import com.example.woowa.restaurant.category.entity.Category;
import com.example.woowa.restaurant.category.service.CategoryService;
import com.example.woowa.restaurant.owner.entity.Owner;
import com.example.woowa.restaurant.owner.service.OwnerService;
import com.example.woowa.restaurant.restaurant.dto.request.RestaurantCreateRequest;
import com.example.woowa.restaurant.restaurant.dto.request.RestaurantUpdateRequest;
import com.example.woowa.restaurant.restaurant.dto.response.RestaurantCreateResponse;
import com.example.woowa.restaurant.restaurant.dto.response.RestaurantFindResponse;
import com.example.woowa.restaurant.restaurant.entity.Restaurant;
import com.example.woowa.restaurant.restaurant.mapper.RestaurantMapper;
import com.example.woowa.restaurant.restaurant.repository.RestaurantRepository;
import com.example.woowa.restaurant.restaurntat_category.entity.RestaurantCategory;
import com.example.woowa.restaurant.restaurntat_category.repository.RestaurantCategoryRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantCategoryRepository restaurantCategoryRepository;

    @Mock
    private OwnerService ownerService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private RestaurantMapper restaurantMapper;

    private RestaurantService restaurantService;

    private Restaurant restaurant;

    private Owner owner;

    @BeforeEach
    void init() {
        restaurantService = new RestaurantService(
                restaurantRepository,
                restaurantCategoryRepository,
                categoryService,
                ownerService,
                null,
                null,
                restaurantMapper
        );

        owner = new Owner("Aabcd123456", "tT@!123456789", "홍길동", "010-1234-5678");
        restaurant = Restaurant.createRestaurant(
                "레스토랑 이름",
                "760-15-00993",
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                false,
                "010-1234-5678",
                "테스트용 레스토랑",
                "서울특별시 강남구"
        );

        // 연관 관계 설정
        owner.addRestaurant(restaurant);
        restaurant.setOwner(owner);
    }


    @Test
    @DisplayName("레스토랑을 생성한다.")
    void createRestaurantByOwnerIdTest() {
        // Given
        Long ownerId = 1L;
        RestaurantCreateRequest request = new RestaurantCreateRequest(
                "레스토랑 이름", "760-15-00993", LocalTime.of(10, 0), LocalTime.of(22, 0),
                false, "010-1234-5678", "테스트 레스토랑", "서울 강남구", Collections.emptyList()
        );
        RestaurantCreateResponse response = new RestaurantCreateResponse(
                1L, ownerId, "레스토랑 이름", "760-15-00993", LocalTime.of(10, 0), LocalTime.of(22, 0),
                false, "010-1234-5678", "테스트 레스토랑", "서울 강남구", List.of(), LocalDateTime.now()
        );

        given(ownerService.findOwnerEntityById(ownerId)).willReturn(owner);
        given(restaurantMapper.toEntity(request)).willReturn(restaurant);
        given(restaurantRepository.save(any())).willReturn(restaurant);
        given(restaurantMapper.toCreateResponseDto(restaurant)).willReturn(response);

        // When
        RestaurantCreateResponse result = restaurantService.createRestaurantByOwnerId(ownerId, request);

        // Then
        assertThat(result).isEqualTo(response);
        verify(ownerService).findOwnerEntityById(ownerId);
        verify(restaurantRepository).save(any());
        verify(restaurantMapper).toCreateResponseDto(restaurant);
    }

    @Test
    @DisplayName("존재하지 않는 레스토랑 조회 시 예외 발생")
    void findRestaurantByIdNotFoundTest() {
        // Given
        Long wrongRestaurantId = -1L;

        given(restaurantRepository.findById(wrongRestaurantId)).willReturn(Optional.empty());

        // When // Then
        assertThatThrownBy(() -> restaurantService.findRestaurantById(wrongRestaurantId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 Restaurant 입니다.");
    }

    @Test
    @DisplayName("레스토랑 단건을 조회한다.")
    void findRestaurantByIdTest() {
        // Given
        Long restaurantId = 1L; // 조회할 레스토랑 ID
        RestaurantFindResponse response = new RestaurantFindResponse(
                restaurantId,
                owner.getId(),
                restaurant.getName(),
                restaurant.getBusinessNumber(),
                restaurant.getOpeningTime(),
                restaurant.getClosingTime(),
                restaurant.getIsOpen(),
                restaurant.getPhoneNumber(),
                restaurant.getDescription(),
                restaurant.getAverageReviewScore(),
                restaurant.getReviewCount(),
                restaurant.getAddress(),
                List.of("한식", "양식"), // 카테고리 리스트 (예시)
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(restaurantMapper.toFindResponseDto(restaurant)).willReturn(response);

        // When
        RestaurantFindResponse result = restaurantService.findRestaurantById(restaurantId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(restaurantId);
        assertThat(result.getName()).isEqualTo(restaurant.getName());
        assertThat(result.getCategories()).contains("한식", "양식");
        verify(restaurantRepository).findById(restaurantId);
        verify(restaurantMapper).toFindResponseDto(restaurant);
    }

    @Test
    @DisplayName("존재하지 않는 레스토랑을 조회하려 하면 NotFoundException이 발생한다.")
    void findRestaurantByNonExistentIdTest() {
        // Given
        Long wrongRestaurantId = -1L;
        given(restaurantRepository.findById(wrongRestaurantId)).willReturn(Optional.empty());

        // When // Then
        assertThatThrownBy(() -> restaurantService.findRestaurantById(wrongRestaurantId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 Restaurant 입니다.");
    }

    @Test
    @DisplayName("사장님의 모든 레스토랑을 조회한다.")
    void findRestaurantsByOwnerIdTest() {
        // Given
        Long ownerId = 1L; // 사장님 ID
        List<Restaurant> restaurants = List.of(
                Restaurant.createRestaurant(
                        "레스토랑하나", "760-15-00991", LocalTime.of(9, 0), LocalTime.of(21, 0),
                        true, "010-1234-5678", "설명1", "서울 강남구"
                ),
                Restaurant.createRestaurant(
                        "레스토랑둘", "760-15-00991", LocalTime.of(10, 0), LocalTime.of(22, 0),
                        false, "010-2345-6789", "설명2", "서울 서초구"
                )
        );

        List<RestaurantFindResponse> responses = restaurants.stream()
                .map(r -> new RestaurantFindResponse(
                        r.getId(),
                        ownerId,
                        r.getName(),
                        r.getBusinessNumber(),
                        r.getOpeningTime(),
                        r.getClosingTime(),
                        r.getIsOpen(),
                        r.getPhoneNumber(),
                        r.getDescription(),
                        r.getAverageReviewScore(),
                        r.getReviewCount(),
                        r.getAddress(),
                        List.of("한식", "양식"), // 카테고리 리스트 예시
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )).toList();

        given(ownerService.findOwnerEntityById(ownerId)).willReturn(owner);
        given(restaurantRepository.findByOwner(owner)).willReturn(restaurants);
        given(restaurantMapper.toFindResponseDto(any())).willAnswer(invocation -> {
            Restaurant restaurant = invocation.getArgument(0);
            return new RestaurantFindResponse(
                    restaurant.getId(),
                    ownerId,
                    restaurant.getName(),
                    restaurant.getBusinessNumber(),
                    restaurant.getOpeningTime(),
                    restaurant.getClosingTime(),
                    restaurant.getIsOpen(),
                    restaurant.getPhoneNumber(),
                    restaurant.getDescription(),
                    restaurant.getAverageReviewScore(),
                    restaurant.getReviewCount(),
                    restaurant.getAddress(),
                    List.of("한식", "양식"),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
        });

        // When
        List<RestaurantFindResponse> result = restaurantService.findRestaurantsByOwnerId(ownerId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(restaurants.size());
        assertThat(result.get(0).getName()).isEqualTo("레스토랑하나");
        assertThat(result.get(1).getName()).isEqualTo("레스토랑둘");
        verify(ownerService).findOwnerEntityById(ownerId);
        verify(restaurantRepository).findByOwner(owner);
    }
}
