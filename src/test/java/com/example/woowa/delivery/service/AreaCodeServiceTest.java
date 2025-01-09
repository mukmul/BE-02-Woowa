package com.example.woowa.delivery.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.woowa.config.JpaAuditingConfiguration;
import com.example.woowa.delivery.entity.AreaCode;
import com.example.woowa.delivery.repository.AreaCodeRepository;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DataJpaTest
@Import(JpaAuditingConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@WithMockUser
class AreaCodeServiceTest {

    @MockitoBean
    AreaCodeService areaCodeService;

    @Autowired
    private AreaCodeRepository areaCodeRepository;

    @Test
    @DisplayName("ArreaCode를 생성한다")
    public void testCreateAreaCode() {
        AreaCodeRepository areaCodeRepository= mock(AreaCodeRepository.class);
        AreaCodeService areaCodeService = new AreaCodeService(areaCodeRepository);
        when(areaCodeRepository.save(any(AreaCode.class))).thenReturn(new AreaCode("1", "서울특별시 종로구", false));

        AreaCode areaCode=areaCodeRepository.save(new AreaCode("1", "서울특별시 종로구", false));
        System.out.println(areaCode);
    }

    @Test
    @DisplayName("defaultAddress를 통해 AreaCode를 찾을 수 있다.")
    public void findByDefaultAddress() {
        AreaCodeRepository areaCodeRepository= mock(AreaCodeRepository.class);
        AreaCodeService areaCodeService = new AreaCodeService(areaCodeRepository);
        AreaCode areaCode =new AreaCode("1", "서울특별시 종로구", false);
        AreaCode retrieveAreaCode = areaCodeService.findByAddress(areaCode.getDefaultAddress());
        System.out.println(retrieveAreaCode);
        when(areaCodeRepository.findByDefaultAddress("서울특별시 종로구")).thenReturn(Optional.of(areaCode));



        assertThat(retrieveAreaCode.getCode()).isEqualTo(areaCode.getCode());
        assertThat(retrieveAreaCode.getDefaultAddress()).isEqualTo(areaCode.getDefaultAddress());
    }

    @Test
    @DisplayName("법정동 코드를 통해 AreaCode를 찾을 수 있다.")
    public void findByCode() {
        String code = "1";
        AreaCode areaCode = new AreaCode(code, "서울특별시 종로구", false);
        given(areaCodeRepository.findByCode(any())).willReturn(Optional.of(areaCode));

        AreaCode retrieveAreaCode = areaCodeService.findByCode(code);
        assertThat(retrieveAreaCode.getCode()).isEqualTo(code);
        assertThat(retrieveAreaCode.getDefaultAddress()).isEqualTo("서울특별시 종로구");
    }


}