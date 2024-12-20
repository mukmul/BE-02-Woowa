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
    public ResponseEntity<Void> addMenuGroup(@PathVariable Long restaurantId,
            @RequestBody @Validated
            MenuGroupSaveRequest request) {
        log.info("Request to create MenuGroup for Restaurant ID: {}, INFO: {}", restaurantId, request);
        Long menuGroupId = menuGroupService.addMenuGroup(restaurantId, request);
        log.info("Create MenuGroup ID: {}", menuGroupId);

        // 메뉴 그룹 단건 조회 api
        return ResponseEntity.created(URI.create("/api/v1/menu-groups/" + menuGroupId))
                .build();
    }

    // 메뉴 그룹 단건 조회
    @GetMapping("/api/v1/menu-groups/{menuGroupId}")
    public ResponseEntity<MenuGroupResponse> findMenuGroup(@PathVariable Long menuGroupId) {
        log.info("Request to find MenuGroup ID: {}", menuGroupId);
        MenuGroupResponse response = menuGroupService.findMenuById(menuGroupId);
        log.info("Successfully found MenuGroup Response: {}", response);

        return ResponseEntity.ok(response);
    }

    // 가게 메뉴 그룹 조회
    @GetMapping("/api/v1/restaurant/{restaurantId}/menu-groups")
    public ResponseEntity<MenuGroupListResponse> findMenuGroupList(
            @PathVariable Long restaurantId) {
        log.info("Request to find MenuGroup List for Restaurant ID: {}", restaurantId);
        MenuGroupListResponse response = menuGroupService.findMenuGroupByRestaurant(restaurantId);
        log.info("Successfully found MenuGroup List Response: {}", response);

        return ResponseEntity.ok(response);
    }

    // 메뉴 그룹 업데이트
    @PatchMapping("/api/v1/menu-groups/{menuGroupId}")
    public ResponseEntity<Void> updateMenuGroup(@PathVariable Long menuGroupId,
            @RequestBody @Validated
            MenuGroupUpdateRequest request) {
        log.info("Request to update MenuGroup ID: {}, INFO: {}", menuGroupId, request);
        menuGroupService.updateMenuGroup(menuGroupId, request);
        log.info("Successfully updated MenuGroup ID: {}", menuGroupId);
        return ResponseEntity.ok().build();
    }

    // 메뉴 그룹 삭제
    @DeleteMapping("/api/v1/menu-groups/{menuGroupId}")
    public ResponseEntity<Void> deleteMenuGroup(@PathVariable Long menuGroupId) {
        log.info("Request to delete MenuGroup ID: {}", menuGroupId);
        menuGroupService.deleteMenuGroup(menuGroupId);
        log.info("Successfully deleted MenuGroup ID: {}", menuGroupId);

        return ResponseEntity.ok().build();
    }
}
