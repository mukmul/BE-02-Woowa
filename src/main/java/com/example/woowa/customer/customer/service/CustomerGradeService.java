package com.example.woowa.customer.customer.service;

import com.example.woowa.customer.customer.converter.CustomerMapper;
import com.example.woowa.customer.customer.dto.CustomerGradeCreateRequest;
import com.example.woowa.customer.customer.dto.CustomerGradeFindResponse;
import com.example.woowa.customer.customer.dto.CustomerGradeUpdateRequest;
import com.example.woowa.customer.customer.entity.CustomerGrade;
import com.example.woowa.customer.customer.repository.CustomerGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerGradeService {
    private final CustomerGradeRepository customerGradeRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    public CustomerGradeFindResponse createCustomerGrade(
        CustomerGradeCreateRequest customerGradeCreateRequest) {
        CustomerGrade customerGrade = customerMapper.toCustomerGrade(
            customerGradeCreateRequest);
        CustomerGrade savedGrade = customerGradeRepository.save(customerGrade);
        return customerMapper.toCustomerGradeDto(savedGrade);
    }

    public CustomerGradeFindResponse findCustomerGrade(Long id) {
        CustomerGrade customerGrade = findGradeById(id);
        return customerMapper.toCustomerGradeDto(customerGrade);
    }

    @Transactional
    public CustomerGradeFindResponse updateCustomerGrade(Long id, CustomerGradeUpdateRequest updateCustomerGradeDto) {
        CustomerGrade customerGrade = findGradeById(id);
        customerGrade.updateGrade(
                updateCustomerGradeDto.getTitle(),
                updateCustomerGradeDto.getOrderCount(),
                updateCustomerGradeDto.getDiscountPrice(),
                updateCustomerGradeDto.getVoucherCount()
        );

        return customerMapper.toCustomerGradeDto(customerGrade);
    }

    @Transactional
    public void deleteCustomerGrade(Long id) {
        CustomerGrade customerGrade = findGradeById(id);
        customerGradeRepository.delete(customerGrade);
    }

    public CustomerGrade findDefaultCustomerGrade() {
        return customerGradeRepository.findFirstByOrderByOrderCount().orElse(null);
    }

    public CustomerGrade findCustomerGradeByOrderPerMonthCount(int orderCount) {
        return customerGradeRepository.findFirstByOrderCountLessThanEqualOrderByOrderCountDesc(orderCount).orElse(findDefaultCustomerGrade());
    }

    private CustomerGrade findGradeById(Long id) {
        return customerGradeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("customer grade not found for ID: "+id));
    }
}
