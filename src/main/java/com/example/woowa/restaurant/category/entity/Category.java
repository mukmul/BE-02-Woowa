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

    @OneToMany(mappedBy = "category")
    private Set<RestaurantCategory> restaurantCategories = new HashSet<>();

    @Column(unique = true, nullable = false, length = 10)
    private String name;

    @Column(name = "is_deleted", nullable = true)
    private LocalDateTime isDeleted;

    @Builder
    public Category(String name) {
        this.name = name;
    }

    public void addRestaurantCategory(RestaurantCategory restaurantCategory) {
        if (restaurantCategories.add(restaurantCategory)) {
            restaurantCategory.setCategory(this);
        }
    }

    public void changeName(String name) {
        this.name = name;
    }
}
