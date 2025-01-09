package com.example.woowa.delivery.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.example.woowa.TestInitUtil;
import com.example.woowa.config.JpaAuditingConfiguration;
import com.example.woowa.delivery.dto.RiderCreateRequest;
import com.example.woowa.delivery.entity.AreaCode;
import com.example.woowa.delivery.entity.Rider;
import com.example.woowa.delivery.entity.RiderAreaCode;
import com.example.woowa.delivery.mapper.RiderMapper;
import com.example.woowa.delivery.repository.AreaCodeRepository;
import com.example.woowa.delivery.repository.RiderRepository;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import(JpaAuditingConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@WithMockUser
class RiderServiceTest {


    @Test
    @DisplayName("중복된 로그인 id는 저장할 수 없다.")
    public void failSave() {
        RiderCreateRequest riderCreateRequest = new RiderCreateRequest("id", "password", "name",
                "폰");

        RiderRepository riderRepository = mock(RiderRepository.class);
        RiderService riderService = new RiderService(riderRepository,null,null,
        null,null,null,null);

        given(riderRepository.existsByLoginId("id")).willReturn(true);

        assertThrows(RuntimeException.class, () -> riderService.save(riderCreateRequest));
    }

    @Test
    @DisplayName("없는 id는 조회할 수 없다.")
    public void failFind() {
        RiderRepository riderRepository = mock(RiderRepository.class);
        RiderService riderService = new RiderService(riderRepository,null,null,
                null,null,null,null);

        given(riderRepository.findById(any())).willReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> riderService.findResponseById(1L));
    }

    @Test
    @DisplayName("라이더는 배달 지역을 추가 할 수 있다.")
    public void addRiderAreaCode() {
        RiderRepository riderRepository = mock(RiderRepository.class);

        AreaCodeRepository areaCodeRepository = mock(AreaCodeRepository.class);
        AreaCodeService areaCodeService = new AreaCodeService(areaCodeRepository);
        RiderService riderService = new RiderService(riderRepository,null,null,
                areaCodeService,null,areaCodeRepository,null);
        Rider rider = TestInitUtil.initRider();
        AreaCode areaCode = new AreaCode("0001", "서울특별시 강남구 신사동", true);

        // Mock 설정
        given(riderRepository.findById(any())).willReturn(Optional.of(rider));
        given(areaCodeRepository.findById(any())).willReturn(Optional.of(areaCode));

        // 메서드 호출
        riderService.addRiderAreaCode(1L, 1L);

        assertThat(rider.getRiderAreaCodeList().size()).isEqualTo(1);
        assertThat(areaCode.getRiderAreaCodeList().size()).isEqualTo(1);
        assertThat(rider.getRiderAreaCodeList().get(0)).isEqualTo(
                areaCode.getRiderAreaCodeList().get(0));
    }
}