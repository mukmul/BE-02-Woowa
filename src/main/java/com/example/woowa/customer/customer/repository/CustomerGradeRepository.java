package com.example.woowa.customer.customer.repository;

import com.example.woowa.customer.customer.entity.CustomerGrade;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerGradeRepository extends JpaRepository<CustomerGrade, Integer> {
    Optional<CustomerGrade> findFirstByOrderByOrderCount(); // 쿼리문 네이티브 쿼리

    Optional<CustomerGrade> findFirstByOrderCountLessThanEqualOrderByOrderCountDesc(int orderCount);  // 쿼리문 네이티브 쿼리

    Optional<CustomerGrade> findById(Long id);  // 쿼리문 네이티브 쿼리
}
