package com.example.woowa.restaurant.category.entity;

import com.example.woowa.common.base.BaseTimeEntity;
import com.example.woowa.restaurant.restaurntat_category.entity.RestaurantCategory;

import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.*;

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
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RestaurantCategory> restaurantCategories = new HashSet<>();

    @Column(unique = true, nullable = false, length = 10)
    private String name;

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
