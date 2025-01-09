package com.example.woowa.restaurant.category.mapper;

import com.example.woowa.restaurant.category.dto.request.CategoryCreateRequest;
import com.example.woowa.restaurant.category.dto.request.CategoryUpdateRequest;
import com.example.woowa.restaurant.category.dto.response.CategoryCreateResponse;
import com.example.woowa.restaurant.category.dto.response.CategoryFindResponse;
import com.example.woowa.restaurant.category.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
// 엄격한 unmappedTargetPolicy = ReportingPolicy.ERROR로 설정하여 매핑되지 않은 필드가 있을 경우 컴파일 에러를 발생시킴.
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CategoryMapper {

    Category toEntity(CategoryCreateRequest categoryCreateRequest);

    CategoryCreateResponse toCreateResponseDto(Category category);

    CategoryFindResponse toFindResponseDto(Category category);

    default void updateEntity(CategoryUpdateRequest categoryUpdateRequest, @MappingTarget Category category) {
        category.changeName(categoryUpdateRequest.getName());
    }

}
