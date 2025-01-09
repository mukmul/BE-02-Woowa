package com.example.woowa.restaurant.advertisement.validate;

public class AdvertisementValidator {
    public static void validateRate(Integer rate) {
        if (rate == null || rate < 0) {
            throw new IllegalArgumentException("비율은 0 이하가 될 수 없습니다.");
        }
    }

    public static void validateLimitSize(Integer limitSize) {
        if (limitSize == null || limitSize < 10 || limitSize > 50) {
            throw new IllegalArgumentException("가게 광고 제한 수는 10애서 50 사이 입니다.");
        }
    }

    public static void validateCurrentSizeNotBelowZero(Integer currentSize) {
        if (currentSize <= 0) {
            throw new IllegalStateException("가게 광고 수가 0보다 작을 수 없습니다.");
        }
    }

    public static void isAvailable(Integer currentSize, Integer limitSize) {
        if (currentSize == null || limitSize == null) {
            throw new IllegalArgumentException("Current size 와 limit size 는 null이 될 수 없습니다.");
        }
        if (currentSize > limitSize) {
            throw new IllegalStateException("광고에 더 이상 레스토랑을 추가할 수 없습니다.");
        }
    }
}
