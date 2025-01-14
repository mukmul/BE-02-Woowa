package com.example.woowa.restaurant.menu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class MainMenuStatusUpdateRequest {

    @NotNull(message = "메인 메뉴 설정 여부를 입력해주세요")
    private final Boolean isMainMenu;

    public MainMenuStatusUpdateRequest(@JsonProperty("isMainMenu") Boolean isMainMenu) {
        this.isMainMenu = isMainMenu;
    }

}
