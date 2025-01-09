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
        validateInputs(orderCount, title, discountPrice, voucherCount);
        this.orderCount = orderCount;
        this.title = title;
        this.discountPrice = discountPrice;
        this.voucherCount = voucherCount;
    }

    public void updateGrade(String title, Integer orderCount, Integer discountPrice, Integer voucherCount) {
        validateInputs(orderCount, title, discountPrice, voucherCount);
        this.title = title;
        this.orderCount = orderCount;
        this.discountPrice = discountPrice;
        this.voucherCount = voucherCount;
    }

    public void validateInputs(Integer orderCount, String title, Integer discountPrice, Integer voucherCount) {
        if (orderCount == null || orderCount <= 0) {
            throw new IllegalArgumentException("Order count must be greater than 0");
        }
        if (title == null || title.isBlank() || title.length() > 10) {
            throw new IllegalArgumentException("Title must be non-blank and at most 10 characters");
        }
        if (discountPrice == null || discountPrice <= 0) {
            throw new IllegalArgumentException("Discount price must be greater than 0");
        }
        if (voucherCount == null || voucherCount <= 0) {
            throw new IllegalArgumentException("Voucher count must be greater than 0");
        }
    }
}
