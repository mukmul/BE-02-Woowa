package com.example.woowa.customer.customer.converter;

import com.example.woowa.customer.customer.dto.CustomerAddressCreateRequest;
import com.example.woowa.customer.customer.dto.CustomerAddressFindResponse;
import com.example.woowa.customer.customer.dto.CustomerCreateRequest;
import com.example.woowa.customer.customer.dto.CustomerFindResponse;
import com.example.woowa.customer.customer.dto.CustomerGradeCreateRequest;
import com.example.woowa.customer.customer.dto.CustomerGradeFindResponse;
import com.example.woowa.customer.customer.entity.Customer;
import com.example.woowa.customer.customer.entity.CustomerAddress;
import com.example.woowa.customer.customer.entity.CustomerGrade;
import com.example.woowa.delivery.entity.AreaCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

// 표현식을 별도의 메서드로 추출, 이름이 너무 복잡, 재사용 가능한 상수로 정의
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL, componentModel = "spring")
public interface CustomerMapper { // 메퍼 사용 이유
  @Mapping(target = "birthdate", expression = "java(LocalDate.parse(customerCreateRequest.getBirthdate(), DateTimeFormatter.ISO_DATE))")
  Customer toCustomer(CustomerCreateRequest customerCreateRequest, CustomerGrade customerGrade);
  CustomerFindResponse toCustomerDto(Customer customer);

  CustomerGradeFindResponse toCustomerGradeDto(CustomerGrade customerGrade);
  CustomerGrade toCustomerGrade(CustomerGradeCreateRequest customerGradeCreateRequest);

  @Mapping(target = "address", expression = "java(customerAddress.getAddress())")
  CustomerAddressFindResponse toCustomerAddressDto(CustomerAddress customerAddress);
  CustomerAddress toCustomerAddress(
      AreaCode areaCode, CustomerAddressCreateRequest customerAddressCreateRequest, Customer customer);
}