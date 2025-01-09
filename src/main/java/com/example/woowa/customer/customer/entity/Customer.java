package com.example.woowa.customer.customer.entity;

import static lombok.AccessLevel.PROTECTED;

import com.example.woowa.common.base.BaseLoginEntity;
import com.example.woowa.customer.voucher.entity.Voucher;
import com.example.woowa.order.order.entity.Order;
import com.example.woowa.order.review.entity.Review;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.example.woowa.order.review.enums.ReviewStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Customer extends BaseLoginEntity {
    private static final int DEFAULT_ORDER_COUNT = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "INT DEFAULT " + DEFAULT_ORDER_COUNT)
    private Integer orderPerMonth;

    @Column(columnDefinition = "INT DEFAULT " + DEFAULT_ORDER_COUNT)
    private Integer orderPerLastMonth;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isIssued;

    @Column(nullable = false)
    private LocalDate birthdate;

    @Column(columnDefinition = "INT DEFAULT " + DEFAULT_ORDER_COUNT)
    private Integer point;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "customer_grade_id", nullable = true)
    private CustomerGrade customerGrade;

    @OneToMany(mappedBy = "customer", orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "customer", orphanRemoval = true)
    private List<CustomerAddress> customerAddresses = new ArrayList<>();

    @OneToMany(orphanRemoval = true) // 단방향 사용 줄어보기
    private List<Voucher> vouchers = new ArrayList<>();

    @OneToMany(mappedBy = "customer", orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    public Customer(final String loginId, String loginPassword, LocalDate birthdate,
        CustomerGrade customerGrade) { // 리스트에 불변성을 보장해줘야함
        super(loginId, loginPassword, "", "");
        this.orderPerMonth = 0;
        this.orderPerLastMonth = 0;
        this.isIssued = false;
        this.birthdate = birthdate;
        this.point = 0;
        this.customerGrade = customerGrade;
    }

    public void updateCustomerStatusOnFirstDay() {
        this.orderPerLastMonth = this.orderPerMonth;
        this.orderPerMonth = 0;
        this.isIssued = false;
    }

    public void updateCustomerStatusWhenOrder(int usedPoint, int plusPoint) {
        addOrderPerMonth();
        usePoint(usedPoint - plusPoint);
    }

    public void updateCustomerStatusWhenOrderCancel(int usedPoint, int minusPoint) {
        minusOrderPerMonth();
        addPoint(usedPoint - minusPoint);
    }

    public void addOrderPerMonth() {
        ++this.orderPerMonth;
    }

    public void minusOrderPerMonth() {
        --this.orderPerMonth;
    }

    public void useMonthlyCoupon() {
        this.isIssued = true;
    }

    public void usePoint(int point) {
        assert point <= this.point;
        this.point -= point;
    }

    public void addPoint(int point) {
        this.point += point;
    }

    public void setCustomerGrade(CustomerGrade customerGrade) {
        this.customerGrade = customerGrade;
    }

    public void setIsIssued(boolean value) {
        this.isIssued = value;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public void removeReview(Review review) {
        review.setReviewStatus(ReviewStatus.DELETED);
    }

    public List<CustomerAddress> getCustomerAddresses() {
        return this.customerAddresses.stream().sorted(Comparator.comparing(CustomerAddress::getRecentOrderAt,Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
    } // 가독성 향상을 위해 분리, 최근 주문한 주소를 상위로 분리하는, 리펙토링

    public void addCustomerAddress(CustomerAddress customerAddress) {
        this.customerAddresses.add(customerAddress);
    }

    public void removeCustomerAddress(CustomerAddress customerAddress) {
        this.customerAddresses.remove(customerAddress);
    }

    public void addVoucher(Voucher voucher) {
        this.vouchers.add(voucher);
    }

    public void removeVoucher(Voucher voucher) {
        this.vouchers.remove(voucher);
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }
}
