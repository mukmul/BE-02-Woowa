package com.example.woowa.restaurant.restaurant.mapper;

import com.example.woowa.restaurant.restaurant.dto.request.RestaurantCreateRequest;
import com.example.woowa.restaurant.restaurant.dto.request.RestaurantUpdateRequest;
import com.example.woowa.restaurant.restaurant.dto.response.RestaurantCreateResponse;
import com.example.woowa.restaurant.restaurant.dto.response.RestaurantFindResponse;
import com.example.woowa.restaurant.restaurant.entity.Restaurant;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RestaurantMapper {

    @Mapping(target = "categories",
            expression = "java(restaurant.getRestaurantCategories().stream().map(restaurantCategory -> restaurantCategory.getCategory().getName()).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "ownerId", expression = "java(restaurant.getOwner().getId())")
    @Mapping(target = "openingTime", source = "openingTime", qualifiedByName = "mapLocalDateTimeToLocalTime")
    @Mapping(target = "closingTime", source = "closingTime", qualifiedByName = "mapLocalDateTimeToLocalTime")
    RestaurantCreateResponse toCreateResponseDto(Restaurant restaurant);

    @Mapping(target = "categories",
            expression = "java(restaurant.getRestaurantCategories().stream().map(restaurantCategory -> restaurantCategory.getCategory().getName()).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "ownerId", expression = "java(restaurant.getOwner().getId())")
    @Mapping(target = "openingTime", source = "openingTime", qualifiedByName = "mapLocalDateTimeToLocalTime")
    @Mapping(target = "closingTime", source = "closingTime", qualifiedByName = "mapLocalDateTimeToLocalTime")
    RestaurantFindResponse toFindResponseDto(Restaurant restaurant);

    default Restaurant toEntity(RestaurantCreateRequest restaurantCreateRequest) {
        return Restaurant.createRestaurant(
                restaurantCreateRequest.getName(),
                restaurantCreateRequest.getBusinessNumber(),
                restaurantCreateRequest.getOpeningTime(),
                restaurantCreateRequest.getClosingTime(),
                restaurantCreateRequest.getIsOpen(),
                restaurantCreateRequest.getPhoneNumber(),
                restaurantCreateRequest.getDescription(),
                restaurantCreateRequest.getAddress());
    }

    default void updateEntity(RestaurantUpdateRequest restaurantUpdateRequest, @MappingTarget Restaurant restaurant) {
        restaurant.updateBusinessHours(restaurantUpdateRequest.getOpeningTime(), restaurantUpdateRequest.getClosingTime());
        restaurant.changePhoneNumber(restaurantUpdateRequest.getPhoneNumber());
        restaurant.changeAddress(restaurantUpdateRequest.getAddress());
        restaurant.changeDescription(restaurantUpdateRequest.getDescription());
    }

    // 사용자 정의 매핑 메서드
    @Named("mapLocalDateTimeToLocalTime")
    static java.time.LocalTime mapLocalDateTimeToLocalTime(java.time.LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalTime() : null;
    }
}
