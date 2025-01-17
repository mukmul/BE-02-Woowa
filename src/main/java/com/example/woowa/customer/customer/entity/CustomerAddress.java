package com.example.woowa.customer.customer.entity;

import static lombok.AccessLevel.PROTECTED;

import com.example.woowa.common.base.BaseTimeEntity;
import com.example.woowa.delivery.entity.AreaCode;
import java.time.LocalDateTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "customer_address")
@Getter
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CustomerAddress extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 45)
    private String detailAddress;

    @Column(nullable = false, length = 45)
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_code", unique = false)
    private AreaCode areaCode;

    @Column
    private LocalDateTime recentOrderAt;

    public CustomerAddress(AreaCode areaCode, String detailAddress, String nickname, Customer customer) {
        setAddress(areaCode, detailAddress);
        setNickname(nickname);
        this.customer = customer;
    }

    public void setNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("Nickname cannot be blank");
        }
        this.nickname = nickname;
    }

    public void setAddress(AreaCode areaCode, String detailAddress) {
        if (areaCode == null || detailAddress == null || detailAddress.isBlank()) {
            throw new IllegalArgumentException("Invalid address or area code");
        }
        this.areaCode = areaCode;
        this.detailAddress = detailAddress;
    }

    public String getAddress() {
        return areaCode.getDefaultAddress() + " " + detailAddress;
    }

    //고객의 최근 주문 주소를 정렬하기 위해 최근 업데이트 시간 속성을 참고하기로 했습니다.
    //해당 메소드는 주문을 실행할 때만 호출하는 용도로 사용된다고 예상됩니다.
    public void updateRecentOrderTime() {
        this.recentOrderAt = LocalDateTime.now();
    }
}