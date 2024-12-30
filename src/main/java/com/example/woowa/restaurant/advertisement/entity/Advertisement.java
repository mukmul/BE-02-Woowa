package com.example.woowa.restaurant.advertisement.entity;

import com.example.woowa.common.base.BaseTimeEntity;
import com.example.woowa.restaurant.advertisement.converter.RateTypeConverter;
import com.example.woowa.restaurant.advertisement.converter.UnitTypeConverter;
import com.example.woowa.restaurant.advertisement.enums.RateType;
import com.example.woowa.restaurant.advertisement.enums.UnitType;
import com.example.woowa.restaurant.restaurant_advertisement.entity.RestaurantAdvertisement;
import java.util.ArrayList;
import java.util.List;
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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "advertisement")
@Entity
public class Advertisement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "advertisement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantAdvertisement> restaurantAdvertisements = new ArrayList<>();

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
    // @Size(max = 1000, message = "설명은 최대 1000자까지 입력 가능합니다.") 너무 긴 데이터 입력을 방지하는 로직 추가
    private String description;

    // @Min 제약 추가해서 음수 허용 되지 않도록 수정.
    // @Min(value = 0, message = "현재 크기는 음수가 될 수 없습니다.")
    @Column(nullable = false)
    private Integer limitSize;

    @Column(nullable = false)
    private Integer currentSize;

    // 필드 검증 로직 추가. rate, limitSize
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
    }
    /*
    if (restaurantAdvertisements.contains(restaurantAdvertisement)) {
        throw new IllegalStateException("이미 리스트에 존재하는 RestaurantAdvertisement입니다.");
    }
    if (!this.isAvailable()) {
        throw new IllegalStateException("더 이상 RestaurantAdvertisement를 추가할 수 없습니다. 최대 크기를 초과했습니다.");
    }
    restaurantAdvertisements.add(restaurantAdvertisement);
    restaurantAdvertisement.setAdvertisement(this);
    this.incrementCurrentSize();
     */
    public void addRestaurantAdvertisement(RestaurantAdvertisement restaurantAdvertisement) {
        restaurantAdvertisement.setAdvertisement(this);
    }

    /*
    if (!restaurantAdvertisements.contains(restaurantAdvertisement)) {
        throw new IllegalStateException("해당 광고가 리스트에 존재하지 않습니다.");
    }
    restaurantAdvertisement.markAsDeleted(); // 논리 삭제 플래그 설정
    this.decrementCurrentSize();
     */

    public void removeRestaurantAdvertisement(RestaurantAdvertisement restaurantAdvertisement) {
        this.getRestaurantAdvertisements().remove(restaurantAdvertisement);
        this.decrementCurrentSize();
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
    // currentSize가 limitSize보다 클 경우, 방지 로직 추가
    /*
    if (!this.isAvailable()) {
        throw new IllegalStateException("최대 크기를 초과할 수 없습니다.");
    }
     */
    public void incrementCurrentSize() {
        this.currentSize++;
    }

    public void decrementCurrentSize() {
        this.currentSize--;
    }

    /*
    정적 메서드 -> 인스턴스 메서드로 수정.
    why? 정적 메서드인 경우 특정 객체에 대한 책임이 외부 정적 메서드로 분산되어, 객체지향 설계 원칙을 위반할 가능성 있음.
     */
    public static boolean isAvailable(Advertisement advertisement) {
        return advertisement.currentSize < advertisement.limitSize;
    }

}
