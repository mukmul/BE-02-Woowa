package com.example.woowa.delivery.dto;

import com.example.woowa.common.base.dto.BaseUserResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class RiderResponse extends BaseUserResponse {

    private final Long id;

    private final boolean isDelivery;

    private final List<String> riderAreaList;

    public RiderResponse(LocalDateTime createdAt, LocalDateTime updatedAt, String loginId,
        String password, String name, String phoneNumber, Long id, boolean isDelivery,
        List<String> riderAreaList) {
        super(createdAt, updatedAt, loginId, password, name, phoneNumber);
        this.id = id;
        this.isDelivery = isDelivery;
        this.riderAreaList = new ArrayList<>(riderAreaList);
    }
}
