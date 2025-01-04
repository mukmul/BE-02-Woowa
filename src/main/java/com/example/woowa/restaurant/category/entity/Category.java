package com.example.woowa.restaurant.category.entity;

import com.example.woowa.common.base.BaseTimeEntity;
import com.example.woowa.restaurant.restaurntat_category.entity.RestaurantCategory;

import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.Column;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category")
@Entity
@SQLDelete(sql = "UPDATE category SET is_deleted = NOW() WHERE id = ?")
@SQLRestriction("is_deleted IS NULL")
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 논리 삭제 시, orphanRemoval = true 설정 삭제 필요.
    // isDeleted 추가.
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantCategory> restaurantCategories = new ArrayList<>();

    @Column(unique = true, nullable = false, length = 10)
    private String name;

    @Column(name = "is_deleted", nullable = true)
    private LocalDateTime isDeleted;

    @Builder
    public Category(String name) {
        this.name = name;
    }

    // restaurantCategories.add(restaurantCategory); restaurantCategories 리스트에 실제로 restaurantCategory가 추가 필요.
    public void addRestaurantCategory(RestaurantCategory restaurantCategory) {
        if (!Objects.equals(restaurantCategory.getCategory().getId(), this.getId())) {
            restaurantCategory.setCategory(this);
        }
    }
    // 유효성 검사를 추가하여 잘못된 값이 설정되지 않도록 방지
    public void changeName(String name) {
        this.name = name;
    }

}
