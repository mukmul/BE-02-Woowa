package com.example.woowa.delivery.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;



public record DeliveryCreateRequest (

    @Size(min = 1, max = 255)
    String restaurantAddress,

    @Size(min = 1, max = 255)
    String customerAddress,

    @Positive(message = "배달료는 양수 입니다.")
    int deliveryFee,

    @Positive(message = "orderId는 양수 입니다.")
    Long orderId
){}
