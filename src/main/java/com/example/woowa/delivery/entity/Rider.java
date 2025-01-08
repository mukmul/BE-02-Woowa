package com.example.woowa.delivery.entity;

import com.example.woowa.common.base.BaseLoginEntity;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rider")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rider extends BaseLoginEntity {

    @OneToMany(mappedBy = "rider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<Delivery> deliveryList = new ArrayList<>();
    @OneToMany(mappedBy = "rider", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<RiderAreaCode> riderAreaCodeList = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDelivery;

    @Builder
    private Rider(String loginId, String loginPassword, String name, String phoneNumber) {
        super(loginId, loginPassword, name, phoneNumber);
        this.isDelivery = false;
    }
    public void update(String name, String phoneNumber) {
        changeName(name);
        changePhoneNumber(phoneNumber);
    }

    public static Rider createRider(String loginId, String loginPassword, String name,
        String phoneNumber) {
        return new Rider(loginId, loginPassword, name, phoneNumber);
    }

    public void changeIsDelivery(boolean isDelivery) {
        this.isDelivery = isDelivery;
    }

    public void addDelivery(Delivery delivery) {
        this.deliveryList.add(delivery);
    }

    public void removeDelivery(Delivery delivery) {
        this.deliveryList.remove(delivery);
    }
    public void removeRiderAreaCode(RiderAreaCode riderAreaCode)
    {
            this.riderAreaCodeList.remove(riderAreaCode);
    }

    public void addRiderAreaCode(RiderAreaCode riderAreaCode) {
        this.riderAreaCodeList.add(riderAreaCode);
    }
}
