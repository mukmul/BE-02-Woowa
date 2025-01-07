package com.example.woowa.delivery.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RiderAreaCodeKey implements Serializable {

    private Long riderId;
    private Long areaCodeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RiderAreaCodeKey that = (RiderAreaCodeKey) o;
        return Objects.equals(riderId, that.riderId) &&
                Objects.equals(areaCodeId, that.areaCodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(riderId, areaCodeId);
    }
}
