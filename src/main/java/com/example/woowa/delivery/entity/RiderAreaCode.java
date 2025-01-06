package com.example.woowa.delivery.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rider_area_code")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(RiderAreaCodeKey.class)
public class RiderAreaCode {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_id", nullable = false)

    private Rider rider;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_code_id", nullable = false)
    private AreaCode areaCode;

    @Builder
    public RiderAreaCode(Rider rider, AreaCode areaCode) {
        this.rider = rider;
        this.areaCode = areaCode;
        rider.addRiderAreaCode(this);
        areaCode.addRiderArea(this);
    }
}
