package com.example.woowa.delivery.controller;

import com.example.woowa.delivery.dto.RiderCreateRequest;
import com.example.woowa.delivery.dto.RiderResponse;
import com.example.woowa.delivery.dto.RiderUpdateRequest;
import com.example.woowa.delivery.service.RiderService;
import java.net.URI;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rider")
@RequiredArgsConstructor
public class RiderController {

    private final RiderService riderService;

    @PostMapping
    public ResponseEntity<Void> sign(
        @RequestBody
        @Valid final RiderCreateRequest riderCreateRequest) {
        long id = riderService.save(riderCreateRequest);
        return ResponseEntity.created(URI.create("/api/v1/rider/" + id)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRider(@PathVariable Long id) {
        riderService.deleteRider(id);
        return ResponseEntity.ok("Rider successfully deleted.");
    }

    @GetMapping
    public ResponseEntity<Page<RiderResponse>> pagingRider(
        @RequestParam final int page) {
        PageRequest pageRequest = PageRequest.of(page, 20);
        Page<RiderResponse> riderPage = riderService.findAll(pageRequest);
        return ResponseEntity.ok(riderPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RiderResponse> getRider(
        @PathVariable final Long id) {
        RiderResponse riderResponse = riderService.findResponseById(id);
        return ResponseEntity.ok(riderResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> userUpdate(
        @RequestBody
        @Valid final RiderUpdateRequest riderUpdateRequest,
        @PathVariable final Long id) {
        riderService.update(id, riderUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> changeIsDelivery(
        @RequestParam
        @NotNull final Boolean isDelivery,
        @PathVariable final Long id) {
        riderService.changeIsDelivery(id, isDelivery);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("add/{riderId}/{areaId}")
    public ResponseEntity<Void> addArea(@PathVariable final Long areaId,
        @PathVariable final Long riderId) {
        riderService.addRiderAreaCode(riderId, areaId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("remove/{riderId}/{areaId}")
    public ResponseEntity<Void> removeArea(@PathVariable final Long areaId,
                                        @PathVariable final Long riderId) {
        riderService.removeRiderAreaCode(riderId, areaId);
        return ResponseEntity.noContent().build();
    }
}