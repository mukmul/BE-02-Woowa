package com.example.woowa.customer.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
@AllArgsConstructor
public class CustomerGradeCreateRequest {
    @Positive
    private final Integer orderCount;
    @NotBlank
    @Length(max = 10)
    private final String title;
    @Positive
    private final Integer discountPrice;
    @Positive
    private final Integer voucherCount;
}
