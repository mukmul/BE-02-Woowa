package com.example.woowa.restaurant.owner.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;

import com.example.woowa.common.exception.NotFoundException;
import com.example.woowa.restaurant.owner.dto.request.OwnerCreateRequest;
import com.example.woowa.restaurant.owner.dto.request.OwnerUpdateRequest;
import com.example.woowa.restaurant.owner.dto.response.OwnerCreateResponse;
import com.example.woowa.restaurant.owner.dto.response.OwnerFindResponse;
import com.example.woowa.restaurant.owner.entity.Owner;
import com.example.woowa.restaurant.owner.mapper.OwnerMapper;
import com.example.woowa.restaurant.owner.repository.OwnerRepository;
import com.example.woowa.security.user.service.UserService;
import com.example.woowa.security.user.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OwnerMapper ownerMapper;

    private OwnerService ownerService;

    @BeforeEach
    void setUp() {
        ownerService = new OwnerService(ownerRepository, userService, passwordEncoder, ownerMapper);
    }

    @Test
    @DisplayName("사장님을 생성한다.")
    void createOwnerTest() {
        // Given
        OwnerCreateRequest request = new OwnerCreateRequest("loginId", "password", "홍길동", "010-1234-5678");
        Owner owner = new Owner("Aabcd123456", "tT@!123456789", "홍길동", "010-1234-5678");
        OwnerCreateResponse response = new OwnerCreateResponse(1L, "Aabcd123456", "tT@!123456789", "홍길동", "010-1111-1111", LocalDateTime.now());

        given(ownerRepository.existsOwnerByLoginId(request.getLoginId())).willReturn(false);
        given(ownerMapper.toEntity(request)).willReturn(owner);
        given(passwordEncoder.encode(owner.getPassword())).willReturn("encodedPassword");
        given(ownerRepository.save(owner)).willReturn(owner);
        given(ownerMapper.toCreateResponse(owner)).willReturn(response);

        // When
        OwnerCreateResponse result = ownerService.createOwner(request);

        // Then
        assertThat(result).isEqualTo(response);
        then(ownerRepository).should().save(owner);
        then(userService).should().createUser(owner, UserRole.ROLE_OWNER);
    }

    @Test
    @DisplayName("모든 사장님을 조회한다.")
    void findOwnersTest() {
        // Given
        Owner owner = new Owner("Aabcd123456", "tT@!123456789", "홍길동", "010-1234-5678");
        OwnerFindResponse response = new OwnerFindResponse(1L, "Aabcd123456", "tT@!123456789", "홍길동", "010-1111-1111", LocalDateTime.now(), LocalDateTime.now());

        given(ownerRepository.findAll()).willReturn(Collections.singletonList(owner));
        given(ownerMapper.toFindResponse(owner)).willReturn(response);

        // When
        List<OwnerFindResponse> result = ownerService.findOwners();

        // Then
        assertThat(result).containsExactly(response);
    }

    @Test
    @DisplayName("사장님 단건을 조회한다.")
    void findOwnerByIdTest() {
        // Given
        long ownerId = 1L;
        Owner owner = new Owner("Aabcd123456", "tT@!123456789", "홍길동", "010-1234-5678");
        OwnerFindResponse response = new OwnerFindResponse(ownerId, "Aabcd123456", "tT@!123456789", "홍길동", "010-1234-5678", LocalDateTime.now(), LocalDateTime.now());

        given(ownerRepository.findById(ownerId)).willReturn(Optional.of(owner));
        given(ownerMapper.toFindResponse(owner)).willReturn(response);

        // When
        OwnerFindResponse result = ownerService.findOwnerById(ownerId);

        // Then
        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("존재하지 않는 사장님을 조회하면 NotFoundException이 발생한다.")
    void findOwnerByIdNotFoundTest() {
        // Given
        long wrongOwnerId = -1L;
        given(ownerRepository.findById(wrongOwnerId)).willReturn(Optional.empty());

        // When // Then
        assertThatThrownBy(() -> ownerService.findOwnerById(wrongOwnerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 사장님 아이디입니다.");
    }

    @Test
    @DisplayName("사장님 정보를 업데이트한다.")
    void updateOwnerByIdTest() {
        // Given
        long ownerId = 1L;
        OwnerUpdateRequest request = new OwnerUpdateRequest("newPassword", "새이름", "010-9876-5432");
        Owner owner = new Owner("loginId", "password", "홍길동", "010-1234-5678");

        given(ownerRepository.findById(ownerId)).willReturn(Optional.of(owner));
        given(passwordEncoder.encode(request.getPassword())).willReturn("encodedPassword");

        // When
        ownerService.updateOwnerById(ownerId, request);

        // Then
        then(ownerMapper).should().updateEntity(request, owner);
        then(passwordEncoder).should().encode(request.getPassword());
        then(userService).should().syncUser(owner);

        // Verify that the Owner object's password is updated correctly
        assertThat(owner.getPassword()).isEqualTo("encodedPassword");
    }


    @Test
    @DisplayName("사장님을 삭제한다.")
    void deleteOwnerByIdTest() {
        // Given
        long ownerId = 1L;
        Owner owner = new Owner("loginId", "password", "홍길동", "010-1234-5678");

        given(ownerRepository.findById(ownerId)).willReturn(Optional.of(owner));

        // When
        ownerService.deleteOwnerById(ownerId);

        // Then
        then(userService).should().deleteUser(owner.getLoginId());
        then(ownerRepository).should().deleteById(ownerId);
    }

    @Test
    @DisplayName("존재하지 않는 사장님을 삭제하려 하면 NotFoundException이 발생한다.")
    void deleteOwnerByIdNotFoundTest() {
        // Given
        long wrongOwnerId = -1L;
        given(ownerRepository.findById(wrongOwnerId)).willReturn(Optional.empty());

        // When // Then
        assertThatThrownBy(() -> ownerService.deleteOwnerById(wrongOwnerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 사장님 아이디입니다.");
    }

    @Test
    @DisplayName("사장님 비밀번호를 변경한다.")
    void changeOwnerPasswordTest() {
        // Given
        long ownerId = 1L;
        String newPassword = "NewPassword123!";
        OwnerUpdateRequest request = new OwnerUpdateRequest(
                newPassword,
                "홍길동",
                "010-1234-5678"
        );

        Owner owner = new Owner("loginId", "OldPassword", "홍길동", "010-1234-5678");

        given(ownerRepository.findById(ownerId)).willReturn(Optional.of(owner));
        given(passwordEncoder.encode(newPassword)).willReturn("EncodedPassword");

        // When
        ownerService.updateOwnerById(ownerId, request);

        // Then
        then(ownerRepository).should().findById(ownerId);
        then(passwordEncoder).should().encode(newPassword);
        assertThat(owner.getPassword()).isEqualTo("EncodedPassword");
    }

}
