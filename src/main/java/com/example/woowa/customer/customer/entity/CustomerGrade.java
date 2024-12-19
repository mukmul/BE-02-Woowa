package com.example.woowa.customer.customer.entity;

import static lombok.AccessLevel.PROTECTED;

import com.example.woowa.common.base.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_grade")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class CustomerGrade extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer orderCount;

    @Column(nullable = false, unique = true, length = 45)
    private String title;

    @Column(nullable = false)
    private Integer discountPrice;

    @Column(nullable = false)
    private Integer voucherCount;

    public CustomerGrade(Integer orderCount, String title, Integer discountPrice,
        Integer voucherCount) {
        this.orderCount = orderCount;
        this.title = title;
        this.discountPrice = discountPrice;
        this.voucherCount = voucherCount;
    }

    public void setOrderCount(int orderCount) {
        assert orderCount > 0;
        this.orderCount = orderCount;
    }

    public void setGrade(String title) {
        assert !title.isBlank() && (title.length() <= 10);
        this.title = title;
    }

    public void setDiscountPrice(int discountPrice) {
        assert discountPrice > 0;
        this.discountPrice = discountPrice;
    }

    public void setVoucherCount(int voucherCount) {
        assert voucherCount > 0;
        this.voucherCount = voucherCount;
    }
}
