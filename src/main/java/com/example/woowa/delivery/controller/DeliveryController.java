package com.example.woowa.delivery.controller;

import com.example.woowa.delivery.dto.DeliveryCreateRequest;
import com.example.woowa.delivery.dto.DeliveryResponse;
import com.example.woowa.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<DeliveryResponse> createDelivery(@RequestBody @Valid DeliveryCreateRequest deliveryCreateRequest)
    {
        DeliveryResponse deliveryResponse =  deliveryService.createDelivery(deliveryCreateRequest);
        return ResponseEntity.ok(deliveryResponse);
    }
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponse> findByDelivery(@PathVariable Long id) {
        DeliveryResponse deliveryResponse = deliveryService.findResponseById(id);
        return ResponseEntity.ok(deliveryResponse);
    }

    @GetMapping("/rider")
    public ResponseEntity<Page<DeliveryResponse>> findByWaitingDelivery(@RequestParam int page) {
        PageRequest pageRequest = PageRequest.of(page, 20);
        return ResponseEntity.ok(deliveryService.findWaitingDelivery(pageRequest));
    }

    // 추후 security 도입시 riderId받는 부분은 제거.
    @PutMapping("/accept/{deliveryId}/{riderId}")
    public ResponseEntity<Void> acceptDelivery(@PathVariable Long deliveryId,
        @PathVariable Long riderId, @RequestParam @NotNull @PositiveOrZero Integer deliveryMinute,
        Integer cookMinute) {
        deliveryService.acceptDelivery(deliveryId, riderId, deliveryMinute, cookMinute);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/delay/{deliveryId}")
    public ResponseEntity<Void> delayDelivery(@PathVariable Long deliveryId,
        @RequestParam @NotNull @PositiveOrZero Integer delayMinute) {
        deliveryService.delay(deliveryId, delayMinute);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/pickup/{deliveryId}")
    public ResponseEntity<Void> pickUpDelivery(@PathVariable Long deliveryId) {
        deliveryService.pickUp(deliveryId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/finish/{deliveryId}/{riderId}")
    public ResponseEntity<Void> finishDelivery(@PathVariable Long deliveryId,
        @PathVariable Long riderId) {
        deliveryService.finish(deliveryId,riderId);
        return ResponseEntity.noContent().build();
    }
}
