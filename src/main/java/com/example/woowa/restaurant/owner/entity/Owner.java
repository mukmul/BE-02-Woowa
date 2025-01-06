package com.example.woowa.restaurant.owner.entity;

import com.example.woowa.common.base.BaseLoginEntity;
import com.example.woowa.restaurant.restaurant.entity.Restaurant;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "owner")
@Entity
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql="UPDATE owner SET deleted_at = NOW() WHERE id = ?")
public class Owner extends BaseLoginEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private LocalDateTime deleteAt;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Restaurant> restaurants = new ArrayList<>();

    @Builder
    public Owner(String loginId, String password, String name, String phoneNumber) {
        super(loginId, password, name, phoneNumber);
        deleteAt = null;
    }

    public void addRestaurant(Restaurant restaurant) {
        restaurant.setOwner(this);
    }

    public void removeRestaurant(Restaurant restaurant) {
        getRestaurants().remove(restaurant);
    }

    public void changePassword(String loginPassword) {
        super.changePassword(loginPassword);
    }
}
