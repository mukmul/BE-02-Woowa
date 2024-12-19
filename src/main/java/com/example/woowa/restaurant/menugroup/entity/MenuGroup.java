package com.example.woowa.restaurant.menugroup.entity;

import com.example.woowa.common.base.BaseTimeEntity;
import com.example.woowa.restaurant.menu.entity.Menu;
import com.example.woowa.restaurant.restaurant.entity.Restaurant;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Entity
@Table(name = "menu_groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MenuGroup extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    // 메뉴 그룹이 삭제되면 그 그룹 안에 있던 메뉴들도 같이 삭제된다.
    // ! 이 부분 논리 삭제로 바꾸고
    // ! 같이 삭제되는 거는 고민을 해봐야 할 듯
    @OneToMany(mappedBy = "menuGroup", cascade = CascadeType.REMOVE)
    private List<Menu> menus = new ArrayList<>();

    @Column(nullable = false)
    // ! length 500 제한 걸기
    private String title;

    @Column(length = 500)
    private String description;

    private MenuGroup(Restaurant restaurant, String title, String description) {
        this.restaurant = restaurant;
        this.title = title;
        this.description = description;
    }

    public static MenuGroup createMenuGroup(Restaurant restaurant, String title,
            String description) {
        MenuGroup menuGroup = new MenuGroup(restaurant, title, getStoreDescription(description));
        // 두 엔티티 menuGroup 동기화
        restaurant.getMenuGroups().add(menuGroup);
        return menuGroup;
    }

    public void update(String title, String description) {
        this.title = title;
        this.description = getStoreDescription(description);
    }

    private static String getStoreDescription(String description) {
        return StringUtils.hasText(description) ? description : null;
    }

    // ! 양방향 처리
    public void addMenu(Menu menu) {
        menus.add(menu);
    }
}
