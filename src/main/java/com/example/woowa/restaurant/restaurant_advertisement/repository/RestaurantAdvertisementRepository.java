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
//    @Modifying
//    @Query("DELETE FROM RestaurantAdvertisement ra WHERE ra.advertisement = :advertisement")
//    void deleteByAdvertisement(@Param("advertisement") Advertisement advertisement);
    @Modifying
    @Query("UPDATE RestaurantAdvertisement ra SET ra.deletedAt = CURRENT_TIMESTAMP WHERE ra.advertisement = :advertisement")
    void deleteByAdvertisement(@Param("advertisement") Advertisement advertisement);

    @Query("SELECT ra FROM RestaurantAdvertisement ra WHERE ra.advertisement = :advertisement AND ra.restaurant = :restaurant")
    RestaurantAdvertisement findByAdvertisementAndRestaurant(@Param("advertisement") Advertisement advertisement, @Param("restaurant") Restaurant restaurant);
}
