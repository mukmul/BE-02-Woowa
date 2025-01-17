package com.example.woowa.restaurant.advertisement.controller;

import com.example.woowa.restaurant.advertisement.dto.request.AdvertisementCreateRequest;
import com.example.woowa.restaurant.advertisement.dto.request.AdvertisementUpdateRequest;
import com.example.woowa.restaurant.advertisement.dto.response.AdvertisementCreateResponse;
import com.example.woowa.restaurant.advertisement.dto.response.AdvertisementFindResponse;
import com.example.woowa.restaurant.advertisement.service.AdvertisementService;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RequestMapping("/baemin/v1/advertisements")
@RestController
public class AdvertisementRestController {

    private final AdvertisementService advertisementService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdvertisementCreateResponse> createAdvertisement(final @Valid @RequestBody
        AdvertisementCreateRequest advertisementCreateRequest) {
        AdvertisementCreateResponse advertisement = advertisementService.createAdvertisement(
            advertisementCreateRequest);
        return new ResponseEntity<>(advertisement, HttpStatus.CREATED);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AdvertisementFindResponse>> findAllAdvertisements() {
        List<AdvertisementFindResponse> advertisements = advertisementService.findAdvertisements();
        if (advertisements.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(advertisements, HttpStatus.OK);
    }

    @GetMapping(value = "/{advertisementId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdvertisementFindResponse> findAdvertisementById(final @PathVariable Long advertisementId) {
        AdvertisementFindResponse advertisement = advertisementService.findAdvertisementById(advertisementId);
        return new ResponseEntity<>(advertisement, HttpStatus.OK);
    }

    @PutMapping(value = "/{advertisementId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdvertisementFindResponse> updateAdvertisementById(final @PathVariable Long advertisementId,
        final @Valid @RequestBody AdvertisementUpdateRequest advertisementUpdateRequest) {
        AdvertisementFindResponse updatedAdvertisement = advertisementService.updateAdvertisementById(advertisementId, advertisementUpdateRequest);
        return new ResponseEntity<>(updatedAdvertisement, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{advertisementId}")
    public ResponseEntity<Void> deleteAdvertisementById(final @PathVariable Long advertisementId) {
        advertisementService.deleteAdvertisementById(advertisementId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/{advertisementId}/restaurants/{restaurantId}")
    public ResponseEntity<AdvertisementFindResponse> includeRestaurantInAdvertisement(final @PathVariable Long advertisementId,
        final @PathVariable Long restaurantId) {
        AdvertisementFindResponse advertisement = advertisementService.includeRestaurantInAdvertisement(advertisementId, restaurantId);
        return new ResponseEntity<>(advertisement, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{advertisementId}/restaurants/{restaurantId}")
    public ResponseEntity<Void> excludeRestaurantOutOfAdvertisement(final @PathVariable Long advertisementId,
        final @PathVariable Long restaurantId) {
        advertisementService.excludeRestaurantOutOfAdvertisement(advertisementId, restaurantId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
