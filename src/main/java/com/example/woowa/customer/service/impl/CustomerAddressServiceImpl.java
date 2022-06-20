package com.example.woowa.customer.service.impl;

import com.example.woowa.customer.repository.CustomerAddressRepository;
import com.example.woowa.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerAddressServiceImpl implements CustomerService {
  private CustomerAddressRepository customerAddressRepository;
}
