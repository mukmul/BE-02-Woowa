package com.example.woowa.restaurant.category.service;

import com.example.woowa.common.exception.NotFoundException;
import com.example.woowa.restaurant.category.dto.request.CategoryCreateRequest;
import com.example.woowa.restaurant.category.dto.request.CategoryUpdateRequest;
import com.example.woowa.restaurant.category.dto.response.CategoryCreateResponse;
import com.example.woowa.restaurant.category.dto.response.CategoryFindResponse;
import com.example.woowa.restaurant.category.entity.Category;
import com.example.woowa.restaurant.category.mapper.CategoryMapper;
import com.example.woowa.restaurant.category.repository.CategoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryCreateResponse createCategory(CategoryCreateRequest categoryCreateRequest) {
        if (categoryRepository.existsByName(categoryCreateRequest.getName())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다.");
        }
        Category category = categoryRepository
            .save(categoryMapper.toEntity(categoryCreateRequest));
        return categoryMapper.toCreateResponseDto(category);
    }

    public List<CategoryFindResponse> findCategories() {
        return categoryRepository.findAll().stream()
            .map(categoryMapper::toFindResponseDto)
            .collect(Collectors.toList());
    }

    public CategoryFindResponse findCategoryById(Long categoryId) {
        return categoryMapper.toFindResponseDto(categoryRepository.findById(categoryId)
            .orElseThrow(() -> new NotFoundException(String.format("존재하지 않는 카테고리 ID %d 입니다.", categoryId))));
    }

    @Transactional
    public CategoryFindResponse updateCategoryById(Long categoryId, CategoryUpdateRequest categoryUpdateRequest) {
        Category category = findCategoryEntityById(categoryId);
        // 이름 중복 방지
        if (!category.getName().equals(categoryUpdateRequest.getName()) &&
                categoryRepository.existsByName(categoryUpdateRequest.getName())) {
            throw new IllegalArgumentException("해당 이름으로는 변경할 수 없습니다. 이미 존재하는 카테고리 이름입니다.");
        }
        category.changeName(categoryUpdateRequest.getName());
        return categoryMapper.toFindResponseDto(category);
    }

    @Transactional
    public void deleteCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("존재하지 않는 카테고리 ID %d 입니다.", categoryId)));
        categoryRepository.delete(category);
    }

    public Category findCategoryEntityById(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new NotFoundException(String.format("존재하지 않는 카테고리 ID %d 입니다.", categoryId)));
    }

}

