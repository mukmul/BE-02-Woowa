package com.example.woowa.delivery.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RiderAreaCodeKey implements Serializable {

    private Long rider;

    private Long areaCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RiderAreaCodeKey that = (RiderAreaCodeKey) o;
        return Objects.equals(rider, that.rider) && Objects.equals(areaCode, that.areaCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rider, areaCode);
    }
}
