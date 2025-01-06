package com.example.woowa.order.review.entity;

import static lombok.AccessLevel.PROTECTED;

import com.example.woowa.common.base.BaseTimeEntity;
import com.example.woowa.customer.customer.entity.Customer;
import com.example.woowa.order.order.entity.Order;
import com.example.woowa.order.review.enums.ScoreType;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScoreType scoreType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private Order order;

    public Review(String content, ScoreType scoreType, Customer customer, Order order) {
        this.content = content;
        this.scoreType = scoreType;
        this.customer = customer;
        this.order = order;
    }

    public void setContent(String content) {
        assert !content.isBlank();
        this.content = content;
    }

    public void setScoreType(int scoreType) {
        this.scoreType = ScoreType.find(scoreType);
    }
}
