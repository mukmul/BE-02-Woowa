package com.example.woowa.delivery.service;

import com.example.woowa.common.exception.ErrorMessage;
import com.example.woowa.delivery.dto.DeliveryCreateRequest;
import com.example.woowa.delivery.dto.DeliveryResponse;
import com.example.woowa.delivery.entity.Delivery;
import com.example.woowa.delivery.entity.Rider;
import com.example.woowa.delivery.enums.DeliveryStatus;
import com.example.woowa.delivery.mapper.DeliveryMapper;
import com.example.woowa.delivery.repository.DeliveryRepository;
import com.example.woowa.order.order.entity.Order;
import com.example.woowa.order.order.service.OrderService;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    private final DeliveryMapper deliveryMapper;

    private final RiderService riderService;

    private final OrderService orderService;

    public Page<DeliveryResponse> findWaitingDelivery(PageRequest pageRequest) {
        try {
            Page<Delivery> deliveryResponsePage = deliveryRepository.findByDeliveryStatus(pageRequest, DeliveryStatus.DELIVERY_WAITING);
            if (deliveryResponsePage.isEmpty()) {
                throw new Exception(ErrorMessage.NOT_FOUND_DATA.getMessage());
            }
            return deliveryResponsePage.map(deliveryMapper::toResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());

        }

    }

    public Delivery findEntityById(Long id) {
        return deliveryRepository.findById(id)
            .orElseThrow(
                () -> new RuntimeException(ErrorMessage.NOT_FOUND_DELIVERY.getMessage()));
    }

    public DeliveryResponse findResponseById(Long id) {

        try{
            Delivery delivery = findEntityById(id);
            return deliveryMapper.toResponse(delivery);
        }catch (Exception e){
            throw new RuntimeException(ErrorMessage.FAIL_TO_RETRIEVE.getMessage());
        }


    }

    /**
     * 추가할 사항 : 동시성 반영.
     */
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public void acceptDelivery(Long deliveryId, Long riderId, int deliveryMinute, int cookMinute) {
        Delivery delivery = findEntityById(deliveryId);
        if (!delivery.getDeliveryStatus().equals(DeliveryStatus.DELIVERY_WAITING)) {
            throw new RuntimeException(ErrorMessage.ALREADY_RECEIVE_DELIVERY.getMessage());
        }
        Rider rider = riderService.findEntityById(riderId);
        rider.addDelivery(delivery);
        delivery.accept(rider, deliveryMinute, cookMinute);
    }

    @Transactional
    public void delay(Long id, int delayMinute) {
        try {
            Delivery delivery = findEntityById(id);
            delivery.delay(delayMinute);
        }  catch (Exception e) {
            throw new RuntimeException("배달 지연 처리중 에러", e);
        }
    }

    @Transactional
    public void pickUp(Long id) {
        try {
            Delivery delivery = findEntityById(id);
            delivery.pickUp(30);
        } catch (Exception e) {
            throw new RuntimeException("배달 픽업 처리중 에러", e);
        }

    }

    @Transactional
    public void finish(Long deliveryId,Long riderId) {
        try {
            Delivery delivery = findEntityById(deliveryId);

            Rider rider = riderService.findEntityById(riderId);

            delivery.finish();
            rider.removeDelivery(delivery);
            rider.changeIsDelivery(false);
        }
        catch (Exception e) {
            throw new RuntimeException("배달 완료 처리중 에러", e);
        }
    }

    @Transactional
    public DeliveryResponse createDelivery(DeliveryCreateRequest deliveryCreateRequest) {
        try {
            Order order = orderService.findOrderById(deliveryCreateRequest.orderId());
            Delivery delivery = Delivery.createDelivery(order,
                    deliveryCreateRequest.restaurantAddress(),
                    deliveryCreateRequest.customerAddress(),
                    deliveryCreateRequest.deliveryFee());
            order.setDelivery(delivery);

            return deliveryMapper.toResponse(deliveryRepository.save(delivery));
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.FAIL_TO_SAVE.getMessage());
        }
    }
}
