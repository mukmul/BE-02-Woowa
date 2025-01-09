package com.example.woowa.order.review.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReviewStatus {
    REGISTERED("등록됨"),
    EDITED("수정됨"),
    DELETED("삭제됨");

    private final String description;
}
