package com.example.woowa.restaurant.advertisement.service;

import com.example.woowa.common.exception.NotFoundException;
import com.example.woowa.restaurant.advertisement.dto.request.AdvertisementCreateRequest;
import com.example.woowa.restaurant.advertisement.dto.request.AdvertisementUpdateRequest;
import com.example.woowa.restaurant.advertisement.dto.response.AdvertisementCreateResponse;
import com.example.woowa.restaurant.advertisement.dto.response.AdvertisementFindResponse;
import com.example.woowa.restaurant.advertisement.entity.Advertisement;
import com.example.woowa.restaurant.advertisement.mapper.AdvertisementMapper;
import com.example.woowa.restaurant.advertisement.repository.AdvertisementRepository;
import com.example.woowa.restaurant.advertisement.validate.AdvertisementValidator;
import com.example.woowa.restaurant.restaurant.entity.Restaurant;
import com.example.woowa.restaurant.restaurant.repository.RestaurantRepository;
import com.example.woowa.restaurant.restaurant_advertisement.entity.RestaurantAdvertisement;
import com.example.woowa.restaurant.restaurant_advertisement.entity.RestaurantAdvertisementId;
import com.example.woowa.restaurant.restaurant_advertisement.repository.RestaurantAdvertisementRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AdvertisementService {

    private final RestaurantAdvertisementRepository restaurantAdvertisementRepository;
    private final RestaurantRepository restaurantRepository;
    private final AdvertisementRepository advertisementRepository;

    private final AdvertisementMapper advertisementMapper;

    @Transactional
    public AdvertisementCreateResponse createAdvertisement(AdvertisementCreateRequest advertisementCreateRequest) {
        AdvertisementValidator.validateRate(advertisementCreateRequest.getRate());
        AdvertisementValidator.validateLimitSize(advertisementCreateRequest.getLimitSize());

        Optional<Advertisement> existingAdvertisement = advertisementRepository.findDeletedAdvertisementByTitle(advertisementCreateRequest.getTitle());

        if (existingAdvertisement.isPresent()) {
            Advertisement advertisement = existingAdvertisement.get();
            advertisement.restore();
            return advertisementMapper.toCreateResponse(advertisement);
        }
        Advertisement advertisement = advertisementRepository.save(
            advertisementMapper.toEntity(advertisementCreateRequest));
        return advertisementMapper.toCreateResponse(advertisement);
    }

    public List<AdvertisementFindResponse> findAdvertisements() {
        return advertisementRepository.findAll().stream()
            .map(advertisementMapper::toFindResponse)
            .collect(Collectors.toList());
    }

    public AdvertisementFindResponse findAdvertisementById(Long advertisementId) {
        return advertisementMapper.toFindResponse(findAdvertisementEntityById(advertisementId));
    }

    @Transactional
    public AdvertisementFindResponse updateAdvertisementById(Long advertisementId, AdvertisementUpdateRequest advertisementUpdateRequest) {
        Advertisement advertisement = findAdvertisementEntityById(advertisementId);
        advertisementMapper.updateEntity(advertisementUpdateRequest, advertisement);
        return advertisementMapper.toFindResponse(advertisement);
    }

    @Transactional
    public void deleteAdvertisementById(Long advertisementId) {
        Advertisement advertisement = findAdvertisementEntityById(advertisementId);
        restaurantAdvertisementRepository.deleteByAdvertisement(advertisement);
        advertisementRepository.deleteById(advertisement.getId());
    }

    @Transactional
    public AdvertisementFindResponse includeRestaurantInAdvertisement(Long advertisementId, Long restaurantId) {
        Advertisement advertisement = findAdvertisementEntityById(advertisementId);
        Restaurant restaurant = findRestaurantEntityById(restaurantId);

        Optional<RestaurantAdvertisement> existingRelation =
                Optional.ofNullable(restaurantAdvertisementRepository.findByAdvertisementAndRestaurant(advertisement, restaurant));

        if (existingRelation.isPresent()) {
            RestaurantAdvertisement restaurantAdvertisement = existingRelation.get();
            if (restaurantAdvertisement.getDeletedAt() != null) {
                // 논리 삭제된 경우 복구 처리
                restaurantAdvertisement.restore();
                advertisement.incrementCurrentSize();  // 복구 시 currentSize 증가
            } else {
                throw new IllegalStateException(
                        String.format("광고 ID %d와 레스토랑 ID %d의 관계가 이미 존재합니다.", advertisementId, restaurantId));
            }
        } else {
            // 새로운 관계 추가
            AdvertisementValidator.isAvailable(advertisement.getCurrentSize(), advertisement.getLimitSize());
            RestaurantAdvertisement newRelation = new RestaurantAdvertisement(restaurant, advertisement);
            advertisement.getRestaurantAdvertisements().add(newRelation);
            advertisement.incrementCurrentSize();  // 새로운 가게 등록 시 currentSize 증가
        }

        return advertisementMapper.toFindResponse(advertisement);
    }

    @Transactional
    public void excludeRestaurantOutOfAdvertisement(Long advertisementId, Long restaurantId) {
        Advertisement advertisement = findAdvertisementEntityById(advertisementId);
        AdvertisementValidator.validateCurrentSizeNotBelowZero(advertisement.getCurrentSize());

        Restaurant restaurant = findRestaurantEntityById(restaurantId);
        RestaurantAdvertisement restaurantAdvertisement = restaurantAdvertisementRepository.findById(
                        new RestaurantAdvertisementId(restaurantId, advertisementId))
                .orElseThrow(() -> new NotFoundException(
                        String.format("레스토랑 ID %d는 광고 ID %d에 포함되어 있지 않습니다.", restaurantId, advertisementId)));

        advertisement.getRestaurantAdvertisements().remove(restaurantAdvertisement);
        restaurant.getRestaurantAdvertisements().remove(restaurantAdvertisement);

        advertisement.decrementCurrentSize();
        restaurantAdvertisementRepository.delete(restaurantAdvertisement);
    }


    public Advertisement findAdvertisementEntityById(Long advertisementId) {
        return advertisementRepository.findById(advertisementId)
            .orElseThrow(() -> new NotFoundException(String.format("존재하지 않는 광고 ID %d 입니다.", advertisementId)));
    }

    public Restaurant findRestaurantEntityById(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new NotFoundException(String.format("존재하지 않는 가게 ID %d 입니다.", restaurantId)));
    }

}
