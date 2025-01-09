package com.example.woowa.customer.customer.service;

import com.example.woowa.common.exception.ErrorMessage;
import com.example.woowa.common.exception.NotFoundException;
import com.example.woowa.customer.customer.converter.CustomerMapper;
import com.example.woowa.customer.customer.dto.CustomerCreateRequest;
import com.example.woowa.customer.customer.dto.CustomerFindResponse;
import com.example.woowa.customer.customer.dto.CustomerUpdateRequest;
import com.example.woowa.customer.customer.entity.Customer;
import com.example.woowa.customer.customer.entity.CustomerAddress;
import com.example.woowa.customer.customer.repository.CustomerAddressRepository;
import com.example.woowa.customer.customer.repository.CustomerRepository;
import com.example.woowa.delivery.entity.AreaCode;
import com.example.woowa.delivery.service.AreaCodeService;
import com.example.woowa.security.user.service.UserService;
import com.example.woowa.security.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerAddressRepository customerAddressRepository;
    private final CustomerRepository customerRepository;
    private final CustomerGradeService customerGradeService;
    private final AreaCodeService areaCodeService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final CustomerMapper customerMapper;

    @Transactional
    public CustomerFindResponse createCustomer(CustomerCreateRequest customerCreateRequest) {

        boolean isExist = customerRepository.existsCustomerByLoginId(customerCreateRequest.getLoginId());
        if (isExist) {
            throw new IllegalArgumentException(ErrorMessage.DUPLICATE_LOGIN_ID.getMessage());
        }

        Customer customer = customerMapper.toCustomer(customerCreateRequest,
                customerGradeService.findDefaultCustomerGrade());

        customer.changePassword(passwordEncoder.encode(customer.getPassword()));

        customerRepository.save(customer);

        AreaCode areaCode = areaCodeService.findByAddress(customerCreateRequest.getAddress().getDefaultAddress());
        CustomerAddress customerAddress = customerMapper.toCustomerAddress(areaCode, customerCreateRequest.getAddress(), customer);
        customerAddressRepository.save(customerAddress);
        customer.addCustomerAddress(customerAddress);

        userService.createUser(customer, UserRole.ROLE_CUSTOMER);

        return customerMapper.toCustomerDto(customer);
    }

    public CustomerFindResponse findCustomer(String loginId) {
        Customer customer = customerRepository.findByLoginId(loginId).orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다. "+loginId));
        return customerMapper.toCustomerDto(customer);
    }

    @Transactional
    public CustomerFindResponse updateCustomer(String loginId, CustomerUpdateRequest customerUpdateRequest) {
        Customer customer = customerRepository.findByLoginId(loginId).orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_FOUND_CUSTOMER));

        if (customerUpdateRequest.getLoginPassword() != null) {
            customer.changePassword(customerUpdateRequest.getLoginPassword());
        }

        userService.syncUser(customer);

        return customerMapper.toCustomerDto(customer);
    }

    @Transactional
    public void deleteCustomer(String loginId) {
        Customer customer = customerRepository.findByLoginId(loginId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_FOUND_CUSTOMER.getMessage()));

        customerRepository.delete(customer);
        userService.deleteUser(loginId);
    }

    public Customer findCustomerEntity(String loginId) {
       return customerRepository.findByLoginId(loginId).orElseThrow(() -> new NotFoundException("Customer", loginId));
    }

    @Transactional
    public void updateCustomerGrade(String loginId) {
        Customer customer = findCustomerEntity(loginId);
        customer.setCustomerGrade(customerGradeService.findCustomerGradeByOrderPerMonthCount(customer.getOrderPerMonth()));
    }

    @Transactional
    public CustomerFindResponse updateCustomerStatusOnFirstDay(String loginId) {
        Customer customer = customerRepository.findByLoginId(loginId).orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_FOUND_CUSTOMER.getMessage()));
        customer.updateCustomerStatusOnFirstDay();
        return customerMapper.toCustomerDto(customer);
    }
}