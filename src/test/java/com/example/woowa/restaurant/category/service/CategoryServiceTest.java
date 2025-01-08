package com.example.woowa.restaurant.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.woowa.common.exception.NotFoundException;
import com.example.woowa.config.JpaAuditingConfiguration;
import com.example.woowa.restaurant.category.dto.request.CategoryCreateRequest;
import com.example.woowa.restaurant.category.dto.request.CategoryUpdateRequest;
import com.example.woowa.restaurant.category.dto.response.CategoryCreateResponse;
import com.example.woowa.restaurant.category.dto.response.CategoryFindResponse;
import com.example.woowa.restaurant.category.entity.Category;
import com.example.woowa.restaurant.category.mapper.CategoryMapper;
import com.example.woowa.restaurant.category.repository.CategoryRepository;
import com.example.woowa.restaurant.owner.repository.OwnerRepository;
import com.example.woowa.restaurant.restaurant.repository.RestaurantRepository;
import com.example.woowa.restaurant.restaurntat_category.repository.RestaurantCategoryRepository;
import com.example.woowa.security.role.repository.RoleRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.woowa.security.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaAuditingConfiguration.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class CategoryServiceTest {

    @Autowired
    CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

    @Test
    @DisplayName("새로운 카테고리를 생성한다.")
    void testCreateCategory() {
        // Mocked
        CategoryRepository mockedCategoryRepository = mock(CategoryRepository.class);
        CategoryService categoryService = new CategoryService(mockedCategoryRepository, categoryMapper);


        // Given
        CategoryCreateRequest categoryCreateRequest = new CategoryCreateRequest("한식");
        Category korean = makeCategories("한식").get(0);
        CategoryCreateResponse manualConversion = new CategoryCreateResponse(korean.getId(),
            korean.getName(), korean.getCreatedAt());
        when(mockedCategoryRepository.save(any(Category.class))).thenReturn(korean);

        // When
        CategoryCreateResponse result = categoryService.createCategory(categoryCreateRequest);

        // Then
        verify(mockedCategoryRepository, times(1)).save(any(Category.class));
        assertThat(result).usingRecursiveComparison().isEqualTo(manualConversion);
    }

    @Test
    @DisplayName("모든 카테고리를 반환한다.")
    void testFindCategories() {
        // Mocked
        CategoryRepository mockedCategoryRepository = mock(CategoryRepository.class);
        CategoryService categoryService = new CategoryService(mockedCategoryRepository, categoryMapper);


        // Given
        List<Category> testers = makeCategories("한식", "중식");
        List<CategoryFindResponse> manualConversion = testers.stream()
            .map(category ->
                new CategoryFindResponse(category.getId(), category.getName(),
                    category.getCreatedAt(), category.getUpdatedAt()))
            .collect(Collectors.toList());
        when(mockedCategoryRepository.findAll()).thenReturn(testers);

        // When
        List<CategoryFindResponse> result = categoryService.findCategories();

        // Then
        verify(mockedCategoryRepository, times(1)).findAll();
        for (int i = 0; i < result.size(); i++)
            assertThat(result.get(i)).usingRecursiveComparison().isEqualTo(manualConversion.get(i));

    }

    @Test
    @DisplayName("요청한 아이디를 가진 카테고리를 반환한다.")
    void testFindCategoryById() {
        // Mocked
        CategoryRepository mockedCategoryRepository = mock(CategoryRepository.class);
        CategoryService categoryService = new CategoryService(mockedCategoryRepository, categoryMapper);

        // Given
        Category korean = makeCategories("한식").get(0);
        CategoryFindResponse manualConversion =
            new CategoryFindResponse(korean.getId(), korean.getName(), korean.getCreatedAt(), korean.getUpdatedAt());
        when(mockedCategoryRepository.findById(1L)).thenReturn(Optional.of(korean));

        // When
        CategoryFindResponse result = categoryService.findCategoryById(1L);

        // Then
        verify(mockedCategoryRepository, times(1)).findById(anyLong());
        assertThat(result).usingRecursiveComparison().isEqualTo(manualConversion);

    }

    @Test
    @DisplayName("저장된 카테고리명을 요청된 이름으로 변경한다.")
    void testUpdateCategoryById() {
        // Given
        CategoryService categoryService = new CategoryService(categoryRepository, categoryMapper);
        CategoryCreateResponse beforeUpdating = categoryService.createCategory(
            new CategoryCreateRequest("한식"));

        // When
        CategoryUpdateRequest categoryUpdateRequest = new CategoryUpdateRequest("중식");
        categoryService.updateCategoryById(beforeUpdating.getId(), categoryUpdateRequest);
        CategoryFindResponse afterUpdating = categoryService.findCategoryById(
            beforeUpdating.getId());

        // Then
        assertThat(afterUpdating.getId()).isEqualTo(beforeUpdating.getId());
        assertThat(afterUpdating.getName()).isEqualTo(categoryUpdateRequest.getName());
    }

    @Test
    @DisplayName("이미 존재하는 이름으로 카테고리를 생성하면 예외를 발생시킨다.")
    void testCreateDuplicateCategory() {
        // Mocked
        CategoryRepository mockedCategoryRepository = mock(CategoryRepository.class);
        CategoryService categoryService = new CategoryService(mockedCategoryRepository, categoryMapper);

        // Given
        CategoryCreateRequest categoryCreateRequest = new CategoryCreateRequest("한식");
        when(mockedCategoryRepository.existsByName("한식")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> categoryService.createCategory(categoryCreateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 카테고리 이름입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 카테고리를 ID로 조회하면 예외를 발생시킨다.")
    void testFindNonExistingCategoryById() {
        // Mocked
        CategoryRepository mockedCategoryRepository = mock(CategoryRepository.class);
        CategoryService categoryService = new CategoryService(mockedCategoryRepository, categoryMapper);

        // Given
        Long nonExistingId = 999L;
        when(mockedCategoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.findCategoryById(nonExistingId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("존재하지 않는 카테고리 ID");
    }

    @Test
    @DisplayName("카테고리 이름을 중복된 이름으로 업데이트하면 예외를 발생시킨다.")
    void testUpdateCategoryToDuplicateName() {
        // Mocked
        CategoryRepository mockedCategoryRepository = mock(CategoryRepository.class);
        CategoryService categoryService = new CategoryService(mockedCategoryRepository, categoryMapper);

        // Given
        Category korean = new Category("한식");
        when(mockedCategoryRepository.findById(1L)).thenReturn(Optional.of(korean));
        when(mockedCategoryRepository.existsByName("중식")).thenReturn(true);

        // When & Then
        CategoryUpdateRequest updateRequest = new CategoryUpdateRequest("중식");
        assertThatThrownBy(() -> categoryService.updateCategoryById(1L, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 이름으로는 변경할 수 없습니다.");
    }

    @Test
    @DisplayName("카테고리를 삭제한다.")
    void testDeleteCategoryById() {
        // Mocked
        CategoryRepository mockedCategoryRepository = mock(CategoryRepository.class);
        CategoryService categoryService = new CategoryService(mockedCategoryRepository, categoryMapper);

        // Given: 삭제할 카테고리 준비
        Long categoryId = 1L;
        Category korean = new Category("한식");
        when(mockedCategoryRepository.findById(categoryId)).thenReturn(Optional.of(korean));

        // When: 카테고리 삭제
        categoryService.deleteCategoryById(categoryId);

        // Then: 카테고리가 실제로 삭제되었는지 검증
        verify(mockedCategoryRepository, times(1)).delete(korean);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리를 삭제하려고 하면 예외를 발생시킨다.")
    void testDeleteNonExistingCategoryById() {
        // Mocked
        CategoryRepository mockedCategoryRepository = mock(CategoryRepository.class);
        CategoryService categoryService = new CategoryService(mockedCategoryRepository, categoryMapper);

        // Given
        Long nonExistingId = 999L;
        when(mockedCategoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.deleteCategoryById(nonExistingId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("존재하지 않는 카테고리 ID");
    }
    
    public static List<Category> makeCategories(String... name) {
        return Arrays.stream(name).map(Category::new).collect(Collectors.toList());
    }

}