package com.example.woowa.restaurant.category.entity;

import com.example.woowa.common.base.BaseTimeEntity;
import com.example.woowa.restaurant.restaurntat_category.entity.RestaurantCategory;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.CascadeType;
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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category")
@Entity
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantCategory> restaurantCategories = new ArrayList<>();

    @Column(unique = true, nullable = false, length = 10)
    private String name;

    @Builder
    public Category(String name) {
        this.name = name;
    }

    public void addRestaurantCategory(RestaurantCategory restaurantCategory) {
        if (!Objects.equals(restaurantCategory.getCategory().getId(), this.getId())) {
            restaurantCategory.setCategory(this);
        }
    }

    public void changeName(String name) {
        this.name = name;
    }

}
