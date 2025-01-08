package com.example.woowa.restaurant.menugroup.controller;

import com.example.woowa.restaurant.menugroup.dto.MenuGroupListResponse;
import com.example.woowa.restaurant.menugroup.dto.MenuGroupResponse;
import com.example.woowa.restaurant.menugroup.dto.MenuGroupSaveRequest;
import com.example.woowa.restaurant.menugroup.dto.MenuGroupUpdateRequest;
import com.example.woowa.restaurant.menugroup.service.MenuGroupService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MenuGroupApiController {

    private final MenuGroupService menuGroupService;
    private static final Logger log = LoggerFactory.getLogger(MenuGroupApiController.class);

    // 메뉴 그룹 생성
    @PostMapping("/api/v1/restaurant/{restaurantId}/menu-groups")
    public ResponseEntity<MenuGroupResponse> addMenuGroup(@PathVariable Long restaurantId,
            @RequestBody @Validated
            MenuGroupSaveRequest request) {
        Long menuGroupId = menuGroupService.addMenuGroup(restaurantId, request);
        MenuGroupResponse response = menuGroupService.findMenuById(menuGroupId);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 메뉴 그룹 단건 조회
    @GetMapping("/api/v1/menu-groups/{menuGroupId}")
    public ResponseEntity<MenuGroupResponse> findMenuGroup(@PathVariable Long menuGroupId) {
        MenuGroupResponse response = menuGroupService.findMenuById(menuGroupId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 가게 메뉴 그룹 조회
    @GetMapping("/api/v1/restaurant/{restaurantId}/menu-groups")
    public ResponseEntity<MenuGroupListResponse> findMenuGroupList(
            @PathVariable Long restaurantId) {
        MenuGroupListResponse response = menuGroupService.findMenuGroupByRestaurant(restaurantId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 메뉴 그룹 업데이트
    @PatchMapping("/api/v1/menu-groups/{menuGroupId}")
    public ResponseEntity<Void> updateMenuGroup(@PathVariable Long menuGroupId,
            @RequestBody @Validated
            MenuGroupUpdateRequest request) {
        menuGroupService.updateMenuGroup(menuGroupId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 메뉴 그룹 삭제
    @DeleteMapping("/api/v1/menu-groups/{menuGroupId}")
    public ResponseEntity<Void> deleteMenuGroup(@PathVariable Long menuGroupId) {
        menuGroupService.deleteMenuGroup(menuGroupId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
