package com.example.woowa.restaurant.advertisement.controller;

import com.example.woowa.restaurant.advertisement.dto.request.AdvertisementCreateRequest;
import com.example.woowa.restaurant.advertisement.dto.request.AdvertisementUpdateRequest;
import com.example.woowa.restaurant.advertisement.dto.response.AdvertisementCreateResponse;
import com.example.woowa.restaurant.advertisement.dto.response.AdvertisementFindResponse;
import com.example.woowa.restaurant.advertisement.service.AdvertisementService;
import java.util.List;
import javax.validation.Valid;
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
        // 에러 빌생 시 400 에러 주도록 수정 필요.
        return new ResponseEntity<>(advertisement, HttpStatus.CREATED);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AdvertisementFindResponse>> findAllAdvertisements() {
        List<AdvertisementFindResponse> advertisements = advertisementService.findAdvertisements();
        // 빈 리스트일 경우에 204 No Content 하도록 수정 필요.
        return new ResponseEntity<>(advertisements, HttpStatus.OK);
    }

    @GetMapping(value = "/{advertisementId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdvertisementFindResponse> findAdvertisementById(final @PathVariable Long advertisementId) {
        AdvertisementFindResponse advertisement = advertisementService.findAdvertisementById(advertisementId);
        // 삭제하려는 ID가 존재하지 않을 때의 예외 처리 필요.
        // 상태 코드 추가 필요.
        return new ResponseEntity<>(advertisement, HttpStatus.OK);
    }

    @PutMapping(value = "/{advertisementId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateAdvertisementById(final @PathVariable Long advertisementId,
        final @Valid @RequestBody AdvertisementUpdateRequest advertisementUpdateRequest) {
        advertisementService.updateAdvertisementById(advertisementId, advertisementUpdateRequest);
        // 응답 데이터를 반환하도록 변경하는게 좋지 않을까 왜 NO_CONTENT 하는지 모르겠음.
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/{advertisementId}")
    public ResponseEntity<Void> deleteAdvertisementById(final @PathVariable Long advertisementId) {
        advertisementService.deleteAdvertisementById(advertisementId);
        // 논리 삭제로 수정하면 좋겠다.
        // 삭제하려는 ID가 존재하지 않을 때의 예외 처리 필요.
        // 상태 코드 수정 필요.
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/{advertisementId}/restaurants/{restaurantId}")
    public ResponseEntity<Void> includeRestaurantInAdvertisement(final @PathVariable Long advertisementId,
        final @PathVariable Long restaurantId) {
        advertisementService.includeRestaurantInAdvertisement(advertisementId, restaurantId);
        // 삭제하려는 ID가 존재하지 않을 때의 예외 처리 필요.
        // 상태 코드 수정 필요.
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/{advertisementId}/restaurants/{restaurantId}")
    public ResponseEntity<Void> excludeRestaurantOutOfAdvertisement(final @PathVariable Long advertisementId,
        final @PathVariable Long restaurantId) {
        advertisementService.excludeRestaurantOutOfAdvertisement(advertisementId, restaurantId);
        // 삭제하려는 ID가 존재하지 않을 때의 예외 처리 필요.
        // 상태 코드 수정 필요.
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
