package com.example.woowa.restaurant.menu.entity;

import com.example.woowa.common.base.BaseTimeEntity;
import com.example.woowa.restaurant.menu.enums.MenuStatus;
import com.example.woowa.restaurant.menugroup.entity.MenuGroup;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Entity
@Table(name = "menu")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class Menu extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_group_id", nullable = false)
    private MenuGroup menuGroup;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer price;

    @Column(length = 500)
    private String description;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isMain;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MenuStatus menuStatus;

    public static Menu createMenu(MenuGroup menuGroup, String title, Integer price,
            String description,
            Boolean isMain,
            MenuStatus menuStatus) {
        Menu menu = new Menu(null, menuGroup, title, price,
                convertToNullIfEmptyDescription(description),
                isMain,
                menuStatus);
        menuGroup.addMenu(menu);
        return menu;
    }

    public void update(String title, Integer price, String description) {
        this.title = title;
        this.price = price;
        this.description = convertToNullIfEmptyDescription(description);
    }

    public void changeMenuStatus(MenuStatus menuStatus) {
        this.menuStatus = menuStatus;
    }

    public void setMainMenu() {
        isMain = true;
    }

    public void cancelMainMenu() {
        isMain = false;
    }

    private static String convertToNullIfEmptyDescription(String description) {
        return StringUtils.hasText(description) ? description : null;
    }
}
