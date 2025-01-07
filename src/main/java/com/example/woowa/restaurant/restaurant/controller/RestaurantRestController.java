package com.example.woowa.restaurant.restaurant.controller;

import com.example.woowa.restaurant.restaurant.dto.request.RestaurantCreateRequest;
import com.example.woowa.restaurant.restaurant.dto.request.RestaurantUpdateRequest;
import com.example.woowa.restaurant.restaurant.dto.response.RestaurantCreateResponse;
import com.example.woowa.restaurant.restaurant.dto.response.RestaurantFindResponse;
import com.example.woowa.restaurant.restaurant.service.RestaurantService;
import java.util.List;
import java.util.Objects;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(value = "baemin/v1/")
@RestController
public class RestaurantRestController {

    private final RestaurantService restaurantService;

    // 가게 생성
    @PostMapping(value = "owners/{ownerId}/restaurants", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestaurantCreateResponse> createRestaurantByOwnerId(final @PathVariable Long ownerId,
        final @Valid @RequestBody RestaurantCreateRequest restaurantCreateRequest) {
        RestaurantCreateResponse newRestaurant = restaurantService.createRestaurantByOwnerId(ownerId,
            restaurantCreateRequest);
        return new ResponseEntity<>(newRestaurant, HttpStatus.CREATED);
    }

    // 사장님 아이디로 가게 전체 조회
    @GetMapping(value = "owners/{ownerId}/restaurants", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RestaurantFindResponse>> findAllRestaurantsByOwnerId(final @PathVariable Long ownerId) {
        List<RestaurantFindResponse> restaurants = restaurantService.findRestaurantsByOwnerId(ownerId);
        if (restaurants.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    // 가게 변경
    @PutMapping(value = "owners/{ownerId}/restaurants/{restaurantId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateRestaurantByOwnerIdAndRestaurantId(final @PathVariable Long ownerId, final @PathVariable Long restaurantId,
        final @Valid @RequestBody RestaurantUpdateRequest restaurantUpdateRequest) {
        restaurantService.updateRestaurantById(ownerId, restaurantId, restaurantUpdateRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 가게 삭제 (사장님 아이디 + 레스토랑 아이디)
    @DeleteMapping(value = "owners/{ownerId}/restaurants/{restaurantId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteRestaurantByOwnerIdAndRestaurantId(final @PathVariable Long ownerId,
        final @PathVariable Long restaurantId) {
        restaurantService.deleteRestaurantByOwnerIdAndRestaurantId(ownerId, restaurantId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 광고 아이디로 가게 전체 조회
    @GetMapping(value = "advertisements/{advertisementId}/restaurants", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RestaurantFindResponse>> findAllRestaurantsByAdvertisementId(final @PathVariable Long advertisementId) {
        List<RestaurantFindResponse> restaurants = restaurantService.findRestaurantsByAdvertisementId(advertisementId);
        if (restaurants.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    // 카테고리 아이디로 가게 전체 조회
    @GetMapping(value = "categories/{categoryId}/restaurants", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RestaurantFindResponse>> findAllRestaurantsByCategoryId(final @PathVariable Long categoryId) {
        List<RestaurantFindResponse> restaurants = restaurantService.findRestaurantsByCategoryId(categoryId);
        if (restaurants.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    // 가게 전체 조회
    // areaCodeId == null -> 전체 가게 목록 조회
    // areaCodeId != null -> 특정 지역 가게 조회
    @GetMapping(value = "restaurants", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RestaurantFindResponse>> findAllRestaurants(@RequestParam(required = false) Long areaCodeId) {
        if (Objects.nonNull(areaCodeId)) {
            return ResponseEntity.ok(restaurantService.findRestaurantByAreaCode(areaCodeId));
        }
        List<RestaurantFindResponse> restaurants = restaurantService.findRestaurants();
        if (restaurants.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    // 가게 단건 조회 (가게 하나만 조회)
    @GetMapping(value = "restaurants/{restaurantId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestaurantFindResponse> findRestaurantById(final @PathVariable Long restaurantId) {
        RestaurantFindResponse restaurant = restaurantService.findRestaurantById(restaurantId);

        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    // 가게 문 열기/닫기
    @PatchMapping(value = "owners/{ownerId}/restaurants/{restaurantId}")
    public ResponseEntity<String> changeRestaurantState(
            @PathVariable Long ownerId,
            @PathVariable Long restaurantId,
            @RequestParam(value = "isOpen") Boolean isOpen) {

        Boolean currentIsOpen = restaurantService.getRestaurantState(ownerId, restaurantId);

        if (currentIsOpen.equals(isOpen)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("가게 상태가 이미 "+ (isOpen ? "열림" : "닫힘") + "상태입니다.");
        }

        if (isOpen) {
            restaurantService.openRestaurant(ownerId, restaurantId);
        } else {
            restaurantService.closeRestaurant(ownerId, restaurantId);
        }

        return ResponseEntity.ok("가게 상태가 성공적으로 변경되었습니다.");
    }

    // 가게에 카테고리 추가
    @PatchMapping(value = "owners/{ownerId}/restaurants/{restaurantId}/categories/add")
    public ResponseEntity<Void> addCategory(
            @PathVariable Long ownerId,
            @PathVariable Long restaurantId,
            @RequestParam String categoryId) {
        restaurantService.addCategory(ownerId, restaurantId, Long.parseLong(categoryId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 가게에 카테고리 삭제
    @PatchMapping(value = "owners/{ownerId}/restaurants/{restaurantId}/categories/remove")
    public ResponseEntity<String> removeCategory(
            @PathVariable Long ownerId,
            @PathVariable Long restaurantId,
            @RequestParam String categoryId) {
        boolean removed = restaurantService.removeCategory(ownerId, restaurantId, Long.parseLong(categoryId));
        if (!removed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("이 가게는 해당 카테고리에 속하지 않습니다.");
        }

        return ResponseEntity.ok("카테고리가 성공적으로 삭제되었습니다.");
    }
}
