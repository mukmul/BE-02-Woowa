package com.example.woowa.restaurant.owner.service;

import com.example.woowa.common.exception.NotFoundException;
import com.example.woowa.restaurant.owner.dto.request.OwnerCreateRequest;
import com.example.woowa.restaurant.owner.dto.request.OwnerUpdateRequest;
import com.example.woowa.restaurant.owner.dto.response.OwnerCreateResponse;
import com.example.woowa.restaurant.owner.dto.response.OwnerFindResponse;
import com.example.woowa.restaurant.owner.entity.Owner;
import com.example.woowa.restaurant.owner.mapper.OwnerMapper;
import com.example.woowa.restaurant.owner.repository.OwnerRepository;
import com.example.woowa.security.repository.RoleRepository;
import com.example.woowa.security.repository.UserRepository;
import com.example.woowa.security.user.User;
import com.example.woowa.security.user.UserRole;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OwnerService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;

    private final PasswordEncoder passwordEncoder;
    private final OwnerMapper ownerMapper;

    @Transactional
    public OwnerCreateResponse createOwner(OwnerCreateRequest ownerCreateRequest) {
        // 사장님 엔티티에 저장
        Owner owner = ownerRepository.save(ownerMapper.toEntity(ownerCreateRequest));
        owner.changePassword(passwordEncoder.encode(owner.getPassword()));
        // 사용자 엔티티에 분류를 사장님으로 넣어서 저장
        userRepository.save(new User(owner.getLoginId(), owner.getPassword(), owner.getName(),
                owner.getPhoneNumber(),
                List.of(roleRepository.findByName(UserRole.ROLE_OWNER.toString()))));
        return ownerMapper.toCreateResponse(owner);
    }

    public List<OwnerFindResponse> findOwners() {
        return ownerRepository.findAll().stream()
            .map(ownerMapper::toFindResponse)
            .collect(Collectors.toList());
    }

    public OwnerFindResponse findOwnerById(Long ownerId) {
        return ownerMapper.toFindResponse(findOwnerEntityById(ownerId));
    }

    @Transactional
    public void deleteOwnerById(Long ownerId) {
        Owner owner = findOwnerEntityById(ownerId);
        // ! 논리 삭제로 바꾸기
        userRepository.deleteByLoginId(owner.getLoginId());
        ownerRepository.deleteById(ownerId);
    }

    @Transactional
    public void updateOwnerById(Long ownerId, OwnerUpdateRequest ownerUpdateRequest) {
        Owner owner = findOwnerEntityById(ownerId);
        ownerMapper.updateEntity(ownerUpdateRequest, owner);
        owner.changePassword(passwordEncoder.encode(owner.getPassword()));
    }

    public Owner findOwnerEntityById(Long ownerId) {
        return ownerRepository.findById(ownerId).
            orElseThrow(() -> new NotFoundException("존재하지 않는 사장님 아이디입니다."));
    }

}
