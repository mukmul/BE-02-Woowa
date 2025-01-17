package com.example.woowa.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    INVALID_INPUT_VALUE("잘못된 데이터를 입력하였습니다."),
    DUPLICATE_LOGIN_ID("중복된 로그인 ID 입니다."),
    NOTFOUND_LOGIN_ID("없는 로그인 ID 입니다."),
    NOT_FOUND_DATA("데이터가 없습니다."),
    FAIL_TO_DELETE("삭제에 실패했습니다"),
    FAIL_TO_RETRIEVE("검색에 실패했습니다"),
    FAIL_TO_SAVE("저장에 실패했습니다"),
    FAIL_TO_UPDATE("업데이트에 실패했습니다"),
    //AreaCode
    NOT_FOUND_AREA_CODE("없는 지역 정보 입니다."),
    NOT_FOUND_AREA_CODE_ADDRESS("없는 행정구역 입니다."),
    //Delivery

    ALREADY_RECEIVE_DELIVERY("이미 처리된 배달 요청 입니다."),

    INVALID_PERIOD_VALUE("조회 기간의 시작일은 마감일 이전이어야 합니다."),

    NOT_ORDERABLE_AREA("배달 가능 지역이 아닙니다."),
    INVALID_ORDER_STATUS_CODE("잘못된 주문상태 code입니다."),

    NOT_FOUND_ORDER("존재하지 않는 Order 입니다."),
    NOT_FOUND_MENU("존재하지 않는 Menu 입니다."),
    NOT_FOUND_MENU_GROUP("존재하지 않는 MenuGroup 입니다."),
    NOT_FOUND_RESTAURANT("존재하지 않는 Restaurant 입니다."),
    NOT_FOUND_DELIVERY("없는 배달 정보 입니다."),
    NOT_FOUND_RIDER("없는 라이더 정보 입니다."),
    NOT_FOUND_CUSTOMER("존재하지 않는 회원입니다.");

    private final String message;
}
