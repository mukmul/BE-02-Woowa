package com.example.woowa.delivery.service;

import com.example.woowa.common.exception.ErrorMessage;
import com.example.woowa.delivery.dto.RiderCreateRequest;
import com.example.woowa.delivery.dto.RiderResponse;
import com.example.woowa.delivery.dto.RiderUpdateRequest;
import com.example.woowa.delivery.entity.AreaCode;
import com.example.woowa.delivery.entity.Rider;
import com.example.woowa.delivery.entity.RiderAreaCode;
import com.example.woowa.delivery.mapper.RiderMapper;
import com.example.woowa.delivery.repository.AreaCodeRepository;
import com.example.woowa.delivery.repository.RiderAreaCodeRepository;
import com.example.woowa.delivery.repository.RiderRepository;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RiderService {

    private final RiderRepository riderRepository;

    private final RiderMapper riderMapper;

    private final AreaCodeService areaCodeService;

    private final RiderAreaCodeRepository riderAreaCodeRepository;

    private final AreaCodeRepository areaCodeRepository;

    @Transactional
    public void deleteAll() {
        try {
            if (riderRepository.count() == 0) {
                throw new RuntimeException(ErrorMessage.NOT_FOUND_DATA.getMessage());
            }
            riderRepository.deleteAll();
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.FAIL_TO_DELETE.getMessage());
        }
    }

    @Transactional
    public Long save(RiderCreateRequest riderCreateRequest) {
        try {
            Rider rider = riderMapper.toRider(riderCreateRequest);
            boolean isId = riderRepository.existsByLoginId(riderCreateRequest.loginId());
            if (isId) {
                throw new RuntimeException(ErrorMessage.DUPLICATE_LOGIN_ID.getMessage());
            }

            riderRepository.save(rider);
            return rider.getId();
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.FAIL_TO_SAVE.getMessage());
        }
    }

    @Transactional
    public void update(Long id, RiderUpdateRequest riderUpdateRequest) {
        try {
            Rider rider = findEntityById(id);
            rider.update(riderUpdateRequest.getName(), riderUpdateRequest.getPhoneNumber());
        } catch (RuntimeException e) {
            throw new RuntimeException(ErrorMessage.FAIL_TO_UPDATE.getMessage());
        }
    }

    @Transactional
    public void changeIsDelivery(Long id, Boolean isDelivery) {
        try {
            findEntityById(id).changeIsDelivery(isDelivery);
        } catch (RuntimeException e) {
            throw new RuntimeException("배달 상태"+ErrorMessage.FAIL_TO_UPDATE.getMessage());
        }
    }

    public RiderResponse findResponseById(Long id) {
        return riderMapper.toResponse(findEntityById(id));
    }

    public Rider findEntityById(Long id) {
        return riderRepository.findById(id).orElseThrow(() -> new RuntimeException("없는 배달기사 입니다."));
    }

    public Page<RiderResponse> findAll(PageRequest pageRequest) {
        try {
            return riderRepository.findAllBy(pageRequest).map(riderMapper::toResponse);
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.FAIL_TO_RETRIEVE.getMessage());
        }
    }

    @Transactional
    public void addRiderAreaCode(Long riderId, Long areaCodeId) {
        try {
            Rider rider = findEntityById(riderId);
            AreaCode areaCode = areaCodeService.findEntityById(areaCodeId);

            RiderAreaCode riderAreaCode = new RiderAreaCode(rider, areaCode);
            riderAreaCodeRepository.save(riderAreaCode);
            areaCode.addRiderAreaCode(riderAreaCode);
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.FAIL_TO_SAVE.getMessage());
        }
    }

    @Transactional
    public void removeRiderAreaCode(Long riderId, Long areaCodeId) {
        try {
            Rider rider = findEntityById(riderId);
            List<RiderAreaCode> riderAreaCodeList = rider.getRiderAreaCodeList();
            riderAreaCodeList.stream()
                    .filter(riderAreaCode -> riderAreaCode.getAreaCode().getId().equals(areaCodeId))
                    .findFirst()
                    .ifPresent(riderAreaCode -> {
                        rider.removeRiderAreaCode(riderAreaCode);

                        AreaCode areaCode = riderAreaCode.getAreaCode();
                        areaCode.removeRiderAreaCode(riderAreaCode);
                        areaCodeRepository.save(areaCode);

                        // 중간 엔티티 삭제
                        riderAreaCodeRepository.delete(riderAreaCode);
                    });
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.FAIL_TO_DELETE.getMessage());
        }
    }
}
