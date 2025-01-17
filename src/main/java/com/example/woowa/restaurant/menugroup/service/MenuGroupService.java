package com.example.woowa.restaurant.menugroup.service;

import com.example.woowa.common.exception.ErrorMessage;
import com.example.woowa.common.exception.NotFoundException;
import com.example.woowa.restaurant.menu.repository.MenuRepository;
import com.example.woowa.restaurant.menugroup.mapper.MenuGroupMapper;
import com.example.woowa.restaurant.menugroup.dto.MenuGroupListResponse;
import com.example.woowa.restaurant.menugroup.dto.MenuGroupResponse;
import com.example.woowa.restaurant.menugroup.dto.MenuGroupSaveRequest;
import com.example.woowa.restaurant.menugroup.dto.MenuGroupUpdateRequest;
import com.example.woowa.restaurant.menugroup.entity.MenuGroup;
import com.example.woowa.restaurant.menugroup.repository.MenuGroupRepository;
import com.example.woowa.restaurant.restaurant.entity.Restaurant;
import com.example.woowa.restaurant.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuGroupService {

    private final MenuGroupRepository menuGroupRepository;
    private final RestaurantService restaurantService;
    private final MenuGroupMapper mapper;
    private final MenuRepository menuRepository;

    // menuGroupId 예외 처리
    public MenuGroup findMenuGroupEntityById(Long menuGroupId) {
        return menuGroupRepository.findById(menuGroupId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_FOUND_MENU_GROUP.getMessage()));
    }

    public MenuGroupResponse findMenuById(Long menuGroupId) {
        return mapper.toMenuGroupResponse(findMenuGroupEntityById(menuGroupId));
    }

    public MenuGroupListResponse findMenuGroupByRestaurant(Long restaurantId) {
        Restaurant findRestaurant = restaurantService.findRestaurantEntityById(restaurantId);
        return mapper.toMenuGroupListResponse(menuGroupRepository.findByRestaurant(findRestaurant));
    }

    @Transactional
    public Long addMenuGroup(Long restaurantId, MenuGroupSaveRequest request) {
        Restaurant findRestaurant = restaurantService.findRestaurantEntityById(restaurantId);

        boolean exists = menuGroupRepository.existsByRestaurantIdAndTitle(restaurantId, request.getTitle());
        if (exists) {
            throw new IllegalArgumentException("이미 존재하는 메뉴 그룹입니다.");
        }

        MenuGroup menuGroup = MenuGroup.createMenuGroup(findRestaurant, request.getTitle(), request.getDescription());
        return menuGroupRepository.save(menuGroup).getId();
    }


    @Transactional
    public void updateMenuGroup(Long menuGroupId, MenuGroupUpdateRequest request) {
        findMenuGroupEntityById(menuGroupId).update(request.getTitle(), request.getDescription());
    }

    @Transactional
    public void deleteMenuGroup(Long menuGroupId) {
        MenuGroup findMenuGroup = findMenuGroupEntityById(menuGroupId);

        // 메뉴 그룹에 속한 메뉴가 있는지 확인
        boolean hasMenus = menuRepository.existsByMenuGroup(findMenuGroup);
        if (hasMenus) {
            throw new IllegalStateException("해당 메뉴 그룹에 메뉴가 존재하여 삭제할 수 없습니다.");
        }

        // 메뉴가 없으면 삭제
        menuGroupRepository.delete(findMenuGroup);
    }

}
