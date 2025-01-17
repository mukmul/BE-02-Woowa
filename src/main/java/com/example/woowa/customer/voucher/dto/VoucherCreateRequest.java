package com.example.woowa.customer.voucher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoucherCreateRequest {
    @NotBlank
    private final String voucherType;
    @NotBlank
    private final String eventType;
    @Positive
    private final Integer discountValue;
    @Pattern(regexp = "^(19|20)\\d{2}[-](0?[1-9]|[12][0-9]|3[01])[-](0?[1-9]|1[012]) (\\d{1,2}[:-]\\d{2})$", message = "YYYY-MM-dd HH:mm  형태로 작성해야 합니다.")
    private final String expirationDate;
}
