package com.example.woowa.customer.customer.controller;

import com.example.woowa.customer.customer.dto.CustomerAddressUpdateRequest;
import com.example.woowa.customer.customer.dto.CustomerAddressCreateRequest;
import com.example.woowa.customer.customer.dto.CustomerAddressFindResponse;
import com.example.woowa.customer.customer.service.CustomerAddressService;

import java.util.List;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers/addresses")
public class CustomerAddressController {
    private final CustomerAddressService customerAddressService;

    @PostMapping("/{loginId}")
    public ResponseEntity<CustomerAddressFindResponse> createCustomerAddress(@PathVariable String loginId, @RequestBody @Valid CustomerAddressCreateRequest customerAddressCreateRequest) {
        CustomerAddressFindResponse customerAddressFindResponse = customerAddressService.createCustomerAddress(loginId, customerAddressCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerAddressFindResponse);
    }

    @GetMapping("/{loginId}")
    public ResponseEntity<List<CustomerAddressFindResponse>> readCustomerAddresses(@PathVariable String loginId) {
        return ResponseEntity.ok(customerAddressService.findCustomerAddresses(loginId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerAddressFindResponse> updateCustomerAddress(@PathVariable Long id, @RequestBody @Valid CustomerAddressUpdateRequest customerAddressUpdateRequest) {
        CustomerAddressFindResponse customerAddressFindResponse = customerAddressService.updateCustomerAddress(id, customerAddressUpdateRequest);
        return ResponseEntity.ok(customerAddressFindResponse);
    }

    @DeleteMapping("/{loginId}/{id}")
    public ResponseEntity<Void> deleteCustomerAddress(@PathVariable String loginId, @PathVariable Long id) {
        customerAddressService.deleteCustomerAddress(loginId, id);
        return ResponseEntity.noContent().build();
    }
}
