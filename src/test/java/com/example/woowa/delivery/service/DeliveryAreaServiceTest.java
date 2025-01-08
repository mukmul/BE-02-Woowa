package com.example.woowa.delivery.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.example.woowa.config.JpaAuditingConfiguration;
import com.example.woowa.delivery.entity.AreaCode;
import com.example.woowa.delivery.entity.DeliveryArea;
import com.example.woowa.delivery.repository.AreaCodeRepository;
import com.example.woowa.delivery.repository.DeliveryAreaRepository;
import com.example.woowa.restaurant.restaurant.entity.Restaurant;
import com.example.woowa.restaurant.restaurant.repository.RestaurantRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.example.woowa.restaurant.restaurant.service.RestaurantService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import(JpaAuditingConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@WithMockUser
class DeliveryAreaServiceTest {

    @Test
    @DisplayName("저장")
    public void save() {
        AreaCodeRepository areaCodeRepository = mock(AreaCodeRepository.class);
        AreaCodeService areaCodeService = new AreaCodeService(areaCodeRepository);
        RestaurantRepository restaurantRepository = mock(RestaurantRepository.class);
        RestaurantService restaurantService = new RestaurantService(restaurantRepository,null,null,
                null,areaCodeService,null,null);

        DeliveryAreaRepository deliveryAreaRepository = mock(DeliveryAreaRepository.class);
        DeliveryAreaService deliveryAreaService = new DeliveryAreaService(deliveryAreaRepository);


        AreaCode areaCode = new AreaCode("0000", "서울특별시 종로구", true);
        Restaurant restaurant = Restaurant.createRestaurant(
                "테스트 레스토랑", "220-81-62517",  LocalTime.of(10, 0),
                LocalTime.of(22, 0), true,
                "010-123-4567", "테스트용 임시 레스토랑 생성입니다.", "서울시 종로구"
        );


        given(areaCodeRepository.findById(any())).willReturn(Optional.of(areaCode));
        given(restaurantRepository.findById(any())).willReturn(Optional.of(restaurant));

        DeliveryArea deliveryArea = new DeliveryArea(areaCode, restaurant);
        restaurant.addDeliveryArea(deliveryArea);
        areaCode.addDeliveryArea(deliveryArea);
        // 저장 동작 모킹 및 호출
        deliveryAreaService.save(deliveryArea);

        // 검증

        verify(deliveryAreaRepository, times(1)).save(deliveryArea);

        // 추가 검증 - 관계 확인
        assertThat(restaurant.getDeliveryAreas()).contains(deliveryArea);
        assertThat(areaCode.getDeliveryAreas()).contains(deliveryArea);
    }

    @Test
    @DisplayName("AreaCode를 통해 DeliveryArea를 조회할 수 있다.")
    @Transactional
    public void findByAreaCode() {
        // Mock Repository 생성
        AreaCodeRepository areaCodeRepository = mock(AreaCodeRepository.class);
        AreaCodeService areaCodeService = new AreaCodeService(areaCodeRepository);

        // 테스트 데이터 준비
        String address = "서울특별시 종로구";
        AreaCode areaCode = new AreaCode("0000", address, true);
        DeliveryArea deliveryArea = new DeliveryArea(areaCode, null); // restaurant는 필요하지 않으므로 null로 설정
        areaCode.addDeliveryArea(deliveryArea);
        // Mock Repository 동작 정의
        given(areaCodeRepository.findByCode("0000")).willReturn(Optional.of(areaCode));

        // Service를 통해 AreaCode 조회
        AreaCode resultAreaCode = areaCodeService.findByCode("0000");

        // 조회된 DeliveryArea 가져오기
        DeliveryArea resultDeliveryArea = resultAreaCode.getDeliveryAreaList().get(0);

        // 검증
        assertThat(resultDeliveryArea.getAreaCode().getDefaultAddress()).isEqualTo(address);
        assertThat(resultDeliveryArea.getDeliveryFee()).isEqualTo(0);
    }

    @Test
    @DisplayName("Restaurant을 통해 DeliveryArea를 조회할 수 있다.")
    @Transactional
    public void findByRestaurant() {
        // Mock Repository 생성
        RestaurantRepository restaurantRepository = mock(RestaurantRepository.class);
        RestaurantService restaurantService = new RestaurantService(
                restaurantRepository, null, null, null, null, null, null
        );

        // 테스트 데이터 준비
        String name = "테스트 레스토랑";
        String address = "서울특별시 종로구";

        AreaCode areaCode = new AreaCode("0000", address, true);
        Restaurant restaurant = Restaurant.createRestaurant(
                name, "220-81-62517", LocalTime.of(10, 0), LocalTime.of(22, 0), true,
                "010-1234-5678", "테스트용 레스토랑입니다.", "서울시 종로구"
        );

        DeliveryArea deliveryArea = new DeliveryArea(areaCode, restaurant);
        restaurant.addDeliveryArea(deliveryArea);

        // Mock Repository 동작 정의
        given(restaurantRepository.findByName(name)).willReturn(List.of(restaurant));

        List<Restaurant> resultRestaurant=restaurantRepository.findByName(name);

        // Restaurant에서 DeliveryArea 조회
        DeliveryArea resultDeliveryArea = resultRestaurant.get(0).getDeliveryAreas().get(0);

        // 검증
        assertThat(resultDeliveryArea.getDeliveryFee()).isEqualTo(0);
        assertThat(resultDeliveryArea.getRestaurant().getName()).isEqualTo(name);
    }
}