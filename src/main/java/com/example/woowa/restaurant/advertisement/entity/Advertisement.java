package com.example.woowa.restaurant.advertisement.entity;

import com.example.woowa.common.base.BaseTimeEntity;
import com.example.woowa.restaurant.advertisement.converter.RateTypeConverter;
import com.example.woowa.restaurant.advertisement.converter.UnitTypeConverter;
import com.example.woowa.restaurant.advertisement.enums.RateType;
import com.example.woowa.restaurant.advertisement.enums.UnitType;
import com.example.woowa.restaurant.restaurant_advertisement.entity.RestaurantAdvertisement;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "advertisement")
@Entity
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE advertisement SET deleted_at = NOW() WHERE id = ?")
public class Advertisement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "advertisement", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RestaurantAdvertisement> restaurantAdvertisements = new HashSet<>();


    @Column(unique = true, nullable = false, length = 30)
    private String title;

    @Convert(converter = UnitTypeConverter.class)
    @Column(nullable = false)
    private UnitType unitType;

    @Convert(converter = RateTypeConverter.class)
    @Column(nullable = false)
    private RateType rateType;

    @Column(nullable = false)
    private Integer rate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer limitSize;

    @Column(nullable = false)
    private Integer currentSize;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    @Builder
    public Advertisement(String title, UnitType unitType, RateType rateType, Integer rate,
        String description, Integer limitSize) {
        this.title = title;
        this.unitType = unitType;
        this.rateType = rateType;
        this.rate = rate;
        this.description = description;
        this.limitSize = limitSize;
        this.currentSize = 0;
        this.deletedAt = null;
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    public void changeRateType(RateType rateType) {
        this.rateType = rateType;
    }

    public void changeRate(Integer rate) {
        this.rate = rate;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void incrementCurrentSize() {
        this.currentSize++;
    }

    public void decrementCurrentSize() {
        this.currentSize--;
    }

    public void restore() {
        this.deletedAt = null;
    }
}
