package com.example.woowa.delivery.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rider_area_code")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RiderAreaCode {

    @EmbeddedId
    private RiderAreaCodeKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("riderId") // RiderAreaCodeKey의 riderId와 매핑
    @JoinColumn(name = "rider_id", nullable = false)
    private Rider rider;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("areaCodeId") // RiderAreaCodeKey의 areaCodeId와 매핑
    @JoinColumn(name = "area_code_id", nullable = false)
    private AreaCode areaCode;

    @Builder
    public RiderAreaCode(Rider rider, AreaCode areaCode) {
        this.rider = rider;
        this.areaCode = areaCode;
        this.id = new RiderAreaCodeKey(rider.getId(), areaCode.getId());
    }
}

