package com.example.woowa.delivery.service;

import com.example.woowa.common.exception.ErrorMessage;
import com.example.woowa.delivery.entity.AreaCode;
import com.example.woowa.delivery.entity.DeliveryArea;
import com.example.woowa.delivery.repository.DeliveryAreaRepository;
import com.example.woowa.restaurant.restaurant.entity.Restaurant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryAreaService {

    private final DeliveryAreaRepository deliveryAreaRepository;

    public void save(DeliveryArea deliveryArea)
    {
      try
      {
          deliveryAreaRepository.save(deliveryArea);
      } catch (Exception e)
      {
          throw new RuntimeException(ErrorMessage.FAIL_TO_SAVE.getMessage());
      }
    }

    public int getDeliveryFee(Restaurant restaurant, String defaultAddress) {
        return deliveryAreaRepository.findByRestaurantAndAddress(restaurant, defaultAddress)
                .orElseThrow(() -> new IllegalArgumentException(
                        ErrorMessage.NOT_ORDERABLE_AREA.getMessage()))
                .getDeliveryFee();
    }

    public List<DeliveryArea> findDeliveryAreaEntityWithRestaurant(AreaCode areaCode)
    {
        try
        {
            List<DeliveryArea> deliveryAreaList= deliveryAreaRepository.findByAreaCode(areaCode);
            if(deliveryAreaList.isEmpty())
            {
                throw new RuntimeException(ErrorMessage.NOT_FOUND_DATA.getMessage());
            }
            return deliveryAreaList;
        } catch (Exception e)
        {
            throw new RuntimeException(ErrorMessage.FAIL_TO_RETRIEVE.getMessage());
        }

    }
}
