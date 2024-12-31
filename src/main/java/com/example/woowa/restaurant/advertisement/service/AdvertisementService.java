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
        advertisementRepository.deleteById(advertisementId);
    }
    // deleteAdvertisementById에서 ID 존재 여부를 먼저 확인하여 예외 발생 방지.
    // 물리 삭제 대신 논리 삭제로 변경하고, 삭제된 데이터를 처리하는 로직 추가 필요.

    @Transactional
    public void includeRestaurantInAdvertisement(Long advertisementId, Long restaurantId) {
        Advertisement advertisement = findAdvertisementEntityById(advertisementId);
        Restaurant restaurant = findRestaurantEntityById(restaurantId);

        RestaurantAdvertisement restaurantAdvertisement = new RestaurantAdvertisement(restaurant, advertisement);
        // includeRestaurantInAdvertisement 메서드에서 생성된 RestaurantAdvertisement를 저장소에 저장하도록 수정.
        // 추가 성공 여부를 클라이언트에 알리려면 결과 데이터를 반환하거나 상태 메시지를 전달하면 좋다.
    }

    @Transactional
    public void excludeRestaurantOutOfAdvertisement(Long advertisementId, Long restaurantId) {
        Advertisement advertisement = findAdvertisementEntityById(advertisementId);
        Restaurant restaurant = findRestaurantEntityById(restaurantId);

        RestaurantAdvertisement restaurantAdvertisement = restaurantAdvertisementRepository.findById(
                new RestaurantAdvertisementId(restaurantId, advertisementId))
            .orElseThrow(() -> new NotFoundException("가게(" + restaurantId + ")가 광고(" + restaurantId + ")에 포함되어 있지 않습니다."));
        // String.format 사용해서 가독성 up

        advertisement.removeRestaurantAdvertisement(restaurantAdvertisement);
        restaurant.getRestaurantAdvertisements().remove(restaurantAdvertisement);
        // 삭제 성공 여부 전달.(성공/실패를 명확히 알릴 수 있도록 수정.)
    }

    public Advertisement findAdvertisementEntityById(Long advertisementId) {
        return advertisementRepository.findById(advertisementId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 광고 아이디입니다."));
    }

    public Restaurant findRestaurantEntityById(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 restaurantId 입니다."));
    }

    /* 공통적인 예외 처리 로직을 유틸 메서드로 추출하는 방향 제안
    public <T> T findEntityById(Long id, JpaRepository<T, Long> repository, String entityName) {
    return repository.findById(id)
        .orElseThrow(() -> new NotFoundException("존재하지 않는 " + entityName + " 입니다."));
    }

    public Advertisement findAdvertisementEntityById(Long advertisementId) {
        return findEntityById(advertisementId, advertisementRepository, "광고");
    }

    public Restaurant findRestaurantEntityById(Long restaurantId) {
        return findEntityById(restaurantId, restaurantRepository, "레스토랑");
    }
     */
}
