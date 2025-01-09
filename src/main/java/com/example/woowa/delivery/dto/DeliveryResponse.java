package com.example.woowa.delivery.dto;

import com.example.woowa.delivery.enums.DeliveryStatus;

import java.time.LocalDateTime;

public record DeliveryResponse(
        Long id,
        String restaurantAddress,
        String customerAddress,
        int deliveryFee,
        DeliveryStatus deliveryStatus,
        LocalDateTime arrivalTime
) {}
