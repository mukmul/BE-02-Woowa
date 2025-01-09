package com.example.woowa.restaurant.restaurant.service;

import com.example.woowa.common.exception.ErrorMessage;
import com.example.woowa.common.exception.NotFoundException;
import com.example.woowa.delivery.entity.AreaCode;
import com.example.woowa.delivery.entity.DeliveryArea;
import com.example.woowa.delivery.service.AreaCodeService;
import com.example.woowa.delivery.service.DeliveryAreaService;
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
import com.example.woowa.restaurant.restaurntat_category.entity.RestaurantCategoryId;
import com.example.woowa.restaurant.restaurntat_category.repository.RestaurantCategoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantCategoryRepository restaurantCategoryRepository;

    private final CategoryService categoryService;
    private final OwnerService ownerService;
    private final AreaCodeService areaCodeService;
    private final DeliveryAreaService deliveryAreaService;
    private final RestaurantMapper restaurantMapper;

    @Transactional
    public RestaurantCreateResponse createRestaurantByOwnerId(Long ownerId,
        RestaurantCreateRequest restaurantCreateRequest) {
        Owner owner = ownerService.findOwnerEntityById(ownerId);

        Restaurant restaurant = restaurantMapper.toEntity(restaurantCreateRequest);

        owner.addRestaurant(restaurant);

        restaurantCreateRequest.getCategoryIds().forEach(categoryId -> {
            Category category = categoryService.findCategoryEntityById(categoryId);
            RestaurantCategory restaurantCategory = new RestaurantCategory(restaurant, category);
        });

        restaurantRepository.save(restaurant);

        return restaurantMapper.toCreateResponseDto(restaurant);
    }

    public List<RestaurantFindResponse> findRestaurants() {
        return restaurantRepository.findAll().stream()
            .map(restaurantMapper::toFindResponseDto)
            .collect(Collectors.toList());
    }

    public List<RestaurantFindResponse> findRestaurantsByOwnerId(Long ownerId) {
        Owner owner = ownerService.findOwnerEntityById(ownerId);
        return restaurantRepository.findByOwner(owner).stream().
            map(restaurantMapper::toFindResponseDto).
            collect(Collectors.toList());
    }

    public RestaurantFindResponse findRestaurantById(Long restaurantId) {
        return restaurantMapper.toFindResponseDto(findRestaurantEntityById(restaurantId));
    }

    public List<RestaurantFindResponse> findRestaurantsByAdvertisementId(Long advertisementId) {
        return restaurantRepository.findByAdvertisementId(advertisementId).stream()
            .map(restaurantMapper::toFindResponseDto)
            .collect(Collectors.toList());
    }

    public List<RestaurantFindResponse> findRestaurantsByCategoryId(Long categoryId) {
        return restaurantRepository.findByCategoryId(categoryId).stream()
            .map(restaurantMapper::toFindResponseDto)
            .collect(Collectors.toList());
    }

    public Boolean getRestaurantState(Long ownerId, Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findByIdAndOwnerId(restaurantId, ownerId);

        if (restaurant == null) {
            throw new IllegalArgumentException("해당 가게가 존재하지 않습니다.");
        }

        return restaurant.getIsOpen();
    }


    @Transactional
    public void deleteRestaurantByOwnerIdAndRestaurantId(Long ownerId, Long restaurantId) {
        Owner owner = ownerService.findOwnerEntityById(ownerId);
        Restaurant restaurant = findRestaurantEntityByOwnerIdAndRestaurantId(ownerId, restaurantId);

        owner.removeRestaurant(restaurant);
    }

    @Transactional
    public void updateRestaurantById(Long ownerId, Long restaurantId,
        RestaurantUpdateRequest restaurantUpdateRequest) {
        Restaurant restaurant = findRestaurantEntityByOwnerIdAndRestaurantId(ownerId, restaurantId);
        restaurantMapper.updateEntity(restaurantUpdateRequest, restaurant);
    }

    @Transactional
    public void changeRestaurantState(Long ownerId, Long restaurantId, Boolean isOpen) {
        // 레스토랑 엔티티 조회
        Restaurant restaurant = findRestaurantEntityByOwnerIdAndRestaurantId(ownerId, restaurantId);

        // 현재 상태와 요청된 상태 비교
        if (restaurant.getIsOpen().equals(isOpen)) {
            throw new IllegalArgumentException("가게 상태가 이미 " + (isOpen ? "열림" : "닫힘") + " 상태입니다.");
        }

        // 상태 변경
        if (isOpen) {
            restaurant.openRestaurant();
        } else {
            restaurant.closeRestaurant();
        }
    }


    @Transactional
    public void addCategory(Long ownerId, Long restaurantId, Long categoryId) {
        Restaurant restaurant = findRestaurantEntityByOwnerIdAndRestaurantId(ownerId, restaurantId);
        Category category = categoryService.findCategoryEntityById(categoryId);

        RestaurantCategory restaurantCategory = new RestaurantCategory(restaurant, category);
    }

    @Transactional
    public boolean removeCategory(Long ownerId, Long restaurantId, Long categoryId) {
        findRestaurantEntityByOwnerIdAndRestaurantId(ownerId, restaurantId);
        categoryService.findCategoryEntityById(categoryId);

        RestaurantCategory restaurantCategory = restaurantCategoryRepository.findById(
                        new RestaurantCategoryId(restaurantId, categoryId))
                .orElse(null);

        if (restaurantCategory == null) {
            return false;
        }

        restaurantCategoryRepository.delete(restaurantCategory);
        return true;
    }


    // 사장님이 가게를 가지고 있는지 확인하는 validation
    public Restaurant findRestaurantEntityByOwnerIdAndRestaurantId(Long ownerId, Long restaurantId) {
        Restaurant restaurant = findRestaurantEntityById(restaurantId);

        if (!restaurant.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("사장님은 해당 가게를 소유하고 있지 않습니다.");
        }

        return restaurant;
    }


    @Transactional
    public void setDeliveryArea(Long restaurantId, Long areaCodeId, Integer deleiveryFee) {
        Restaurant restaurant = findRestaurantEntityById(restaurantId);
        AreaCode areaCode = areaCodeService.findEntityById(areaCodeId);

        DeliveryArea deliveryArea = new DeliveryArea(areaCode, restaurant, deleiveryFee);
    }

    public Restaurant findRestaurantEntityById(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_FOUND_RESTAURANT.getMessage()));
    }

    public List<RestaurantFindResponse> findRestaurantsIsPermittedIsFalse() {
        return restaurantRepository.findRestaurantByIsPermittedIsFalse().stream()
            .map(restaurantMapper::toFindResponseDto)
            .collect(Collectors.toList());
    }

    public List<RestaurantFindResponse> findRestaurantByAreaCode(Long areaCodeId) {
        AreaCode areaCode = areaCodeService.findEntityById(areaCodeId);
        return deliveryAreaService.findDeliveryAreaEntityWithRestaurant(areaCode).stream()
                .map(deliveryArea -> restaurantMapper.toFindResponseDto(
                        deliveryArea.getRestaurant()))
                .collect(Collectors.toList());
    }

}
