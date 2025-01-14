package com.example.woowa.restaurant.category.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CategoryCreateRequest {

    @Size(min = 1, max = 10, message = "카테고리명은 1자이상 10자이하여야 합니다.")
    @NotNull(message = "카테고리명은 null일 수 없습니다.")
    private final String name;

    @JsonCreator
    public CategoryCreateRequest(@JsonProperty String name) {
        this.name = name;
    }

}
