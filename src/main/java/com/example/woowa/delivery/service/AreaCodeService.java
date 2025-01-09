package com.example.woowa.delivery.service;

import com.example.woowa.common.exception.ErrorMessage;
import com.example.woowa.common.util.FileUtil;
import com.example.woowa.delivery.entity.AreaCode;
import com.example.woowa.delivery.repository.AreaCodeRepository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AreaCodeService {

    private final AreaCodeRepository areaCodeRepository;

    public AreaCode findEntityById(Long id) {
        return areaCodeRepository.findById(id).orElseThrow(
            () -> new RuntimeException(ErrorMessage.NOT_FOUND_AREA_CODE.getMessage()));
    }

    @Transactional
    public void deleteAll() {
        try {
            if (areaCodeRepository.count() == 0) {
                throw new RuntimeException(ErrorMessage.NOT_FOUND_DATA.getMessage());
            }
            areaCodeRepository.deleteAll();
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.FAIL_TO_DELETE.getMessage());
        }
    }

    public List<AreaCode> findAll() {
        try {
            List<AreaCode> areaCodes = areaCodeRepository.findAll();
            if (areaCodes.isEmpty()) {
                throw new RuntimeException(ErrorMessage.NOT_FOUND_DATA.getMessage());
            }
            return areaCodes;
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.FAIL_TO_RETRIEVE.getMessage());

        }
    }

    public AreaCode findByAddress(String defaultAddress) {
        return areaCodeRepository.findByDefaultAddress(defaultAddress)
            .orElseThrow(() -> new RuntimeException(ErrorMessage.NOT_FOUND_AREA_CODE_ADDRESS.getMessage()));
    }

    public void init() {
        try {
            List<AreaCode> areaCodeList = FileUtil.parseAreaCodeList();


            if (areaCodeList.isEmpty()) {
                throw new RuntimeException(ErrorMessage.NOT_FOUND_DATA.getMessage());
            }

            areaCodeRepository.saveAll(areaCodeList);
        } catch (Exception e) {

            throw new RuntimeException(ErrorMessage.FAIL_TO_SAVE.getMessage());
        }
    }

    public AreaCode findByCode(String code) {
        return areaCodeRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException(ErrorMessage.NOT_FOUND_AREA_CODE_ADDRESS.getMessage()));
    }
}
