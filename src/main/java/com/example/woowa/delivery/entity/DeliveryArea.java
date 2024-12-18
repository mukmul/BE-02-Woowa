package com.example.woowa.delivery.entity;

import com.example.woowa.restaurant.restaurant.entity.Restaurant;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delivery_area")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DeliveryArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_code_id", nullable = false)
    private AreaCode areaCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private int deliveryFee;

    public DeliveryArea(AreaCode areaCode, Restaurant restaurant) {
        this.areaCode = areaCode;
        this.restaurant = restaurant;
        this.deliveryFee = 0;
    }

    public DeliveryArea(AreaCode areaCode, Restaurant restaurant, int deliveryFee) {
        this.areaCode = areaCode;
        this.restaurant = restaurant;
        this.deliveryFee = deliveryFee;
        areaCode.addDeliveryArea(this);
        restaurant.addDeliveryArea(this);
    }

    public void setRestaurant(Restaurant restaurant) {
        if (Objects.nonNull(this.restaurant)) {
            this.restaurant.getDeliveryAreas().remove(this);
        }
        this.restaurant = restaurant;
        this.restaurant.getDeliveryAreas().add(this);
    }

}
