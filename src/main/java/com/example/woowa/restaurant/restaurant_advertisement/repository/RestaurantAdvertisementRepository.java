package com.example.woowa.restaurant.restaurant_advertisement.repository;

import com.example.woowa.restaurant.advertisement.entity.Advertisement;
import com.example.woowa.restaurant.restaurant.entity.Restaurant;
import com.example.woowa.restaurant.restaurant_advertisement.entity.RestaurantAdvertisement;
import com.example.woowa.restaurant.restaurant_advertisement.entity.RestaurantAdvertisementId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RestaurantAdvertisementRepository extends JpaRepository<RestaurantAdvertisement, RestaurantAdvertisementId> {
    boolean existsByAdvertisementAndRestaurant(Advertisement advertisement, Restaurant restaurant);
    @Modifying
    @Query("DELETE FROM RestaurantAdvertisement ra WHERE ra.advertisement = :advertisement")
    void deleteByAdvertisement(@Param("advertisement") Advertisement advertisement);
}
