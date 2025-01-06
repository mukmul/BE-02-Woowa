package com.example.woowa.order.order.entity;

import com.example.woowa.common.base.BaseTimeEntity;
import com.example.woowa.customer.customer.entity.Customer;
import com.example.woowa.delivery.entity.Delivery;
import com.example.woowa.order.order.enums.OrderStatus;
import com.example.woowa.order.order.enums.PaymentType;
import com.example.woowa.restaurant.restaurant.entity.Restaurant;
import com.example.woowa.customer.voucher.entity.Voucher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Cart> carts = new ArrayList<>();

    @Column(nullable = false)
    private Integer orderPrice;
    @Column(nullable = false)
    private Integer deliveryFee;

    @Column(nullable = false)
    private Integer afterDiscountTotalPrice;

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer voucherDiscountPrice;

    @OneToOne(mappedBy = "order")
    private Delivery delivery;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer usedPoint;

    private Integer cookingTime;

    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    private Order(Voucher voucher, Customer customer, Restaurant restaurant,
            Integer orderPrice, Integer afterDiscountTotalPrice,
            Integer voucherDiscountPrice,
            PaymentType paymentType, Integer usedPoint, OrderStatus orderStatus,
            String deliveryAddress, int deliveryFee) {
        this.voucher = voucher;
        this.customer = customer;
        this.restaurant = restaurant;
        this.orderPrice = orderPrice;
        this.afterDiscountTotalPrice = afterDiscountTotalPrice;
        this.voucherDiscountPrice = voucherDiscountPrice;
        this.paymentType = paymentType;
        this.usedPoint = usedPoint;
        this.orderStatus = orderStatus;
        this.deliveryAddress = deliveryAddress;
        this.deliveryFee = deliveryFee;
    }

    public static Order createOrder(Customer customer, Restaurant restaurant, Voucher voucher,
            String deliveryAddress,
            Integer usedPoint, PaymentType paymentType, List<Cart> carts, int deliveryFee) {

        int orderPrice = carts.stream()
                .mapToInt(cart -> cart.getMenu().getPrice() * cart.getQuantity())
                .sum();
        int voucherDiscountPrice = getVoucherDiscountPrice(voucher, orderPrice + deliveryFee);
        int afterDiscountTotalPrice = orderPrice + deliveryFee - voucherDiscountPrice;

        customer.updateCustomerStatusWhenOrder(usedPoint,
                calculatePlusPoint(afterDiscountTotalPrice));

        Order order = new Order(voucher, customer, restaurant, orderPrice,
                afterDiscountTotalPrice,
                voucherDiscountPrice, paymentType, usedPoint, OrderStatus.PAYMENT_COMPLETED,
                deliveryAddress, deliveryFee);

        carts.forEach(order::addCart);

        return order;
    }

    public void acceptOrder(int cookingTime, Delivery delivery) {
        orderStatus = OrderStatus.ACCEPTED;
        this.delivery = delivery;
        this.cookingTime = cookingTime;
    }

    public void cancelOrder() {
        if (Objects.nonNull(voucher)) {
            voucher.cancelUse();
            voucher = null;
        }

        customer.updateCustomerStatusWhenOrderCancel(usedPoint,
                calculatePlusPoint(afterDiscountTotalPrice));

        orderStatus = OrderStatus.CANCEL;
    }

    public void addCart(Cart cart) {
        carts.add(cart);
        cart.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    private static int getVoucherDiscountPrice(Voucher voucher, int beforeDiscountTotalPrice) {
        return Objects.isNull(voucher) ? 0 : voucher.getDiscountPrice(beforeDiscountTotalPrice);
    }

    private static int calculatePlusPoint(Integer afterDiscountTotalPrice) {
        return (int) Math.round(afterDiscountTotalPrice * 0.5D);
    }
}