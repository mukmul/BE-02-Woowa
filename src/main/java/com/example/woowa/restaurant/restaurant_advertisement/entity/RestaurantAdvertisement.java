package com.example.woowa.restaurant.restaurant_advertisement.entity;

import com.example.woowa.restaurant.advertisement.entity.Advertisement;
import com.example.woowa.restaurant.restaurant.entity.Restaurant;
import java.util.Objects;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(RestaurantAdvertisementId.class)
@Table(name = "restaurant_advertisement")
@Entity
public class RestaurantAdvertisement {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advertisement_id", nullable = false)
    private Advertisement advertisement;

    public RestaurantAdvertisement(Restaurant restaurant, Advertisement advertisement) {
        if (!Advertisement.isAvailable(advertisement))
            throw new RuntimeException("해당 광고에는 더이상 가게를 포함할 수 없습니다.");

        setRestaurant(restaurant);
        setAdvertisement(advertisement);
    }

    public void setRestaurant(Restaurant restaurant) {
        if (Objects.nonNull(this.restaurant)) {
            this.restaurant.getRestaurantAdvertisements().remove(this);
        }
        this.restaurant = restaurant;
        this.restaurant.getRestaurantAdvertisements().add(this);
    }

    public void setAdvertisement(Advertisement advertisement) {
        if (Objects.nonNull(this.advertisement)) {
            this.advertisement.getRestaurantAdvertisements().remove(this);
        }
        this.advertisement = advertisement;
        this.advertisement.getRestaurantAdvertisements().add(this);
        this.advertisement.incrementCurrentSize();
    }

}
