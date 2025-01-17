package com.example.woowa.delivery.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AreaCode {

    @OneToMany(mappedBy = "areaCode", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<DeliveryArea> deliveryAreaList = new ArrayList<>();
    @OneToMany(mappedBy = "areaCode", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<RiderAreaCode> riderAreaCodeList = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String code;
    @Column(nullable = false)
    private String defaultAddress;
    @Column(nullable = false)
    private boolean isAbolish;

    public AreaCode(String code, String defaultAddress, boolean isAbolish) {
        this.code = code;
        this.defaultAddress = defaultAddress;
        this.isAbolish = isAbolish;
    }

    public List<DeliveryArea> getDeliveryAreas()
    {
        return this.deliveryAreaList;
    }
    public void addDeliveryArea(DeliveryArea deliveryArea) {
        this.deliveryAreaList.add(deliveryArea);
    }
    public void removeDeliveryArea(DeliveryArea deliveryArea) {this.deliveryAreaList.remove(deliveryArea);}

    public void addRiderAreaCode(RiderAreaCode riderAreaCode) {
        this.riderAreaCodeList.add(riderAreaCode);
    }
    public void removeRiderAreaCode(RiderAreaCode riderAreaCode)
    {
        this.riderAreaCodeList.remove(riderAreaCode);
    }


}
