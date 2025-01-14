package com.example.woowa.delivery.repository;

import com.example.woowa.delivery.entity.Rider;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderRepository extends JpaRepository<Rider, Long> {

    List<Rider> findAll();

    Page<Rider> findAllBy(PageRequest pageRequest);

    Optional<Rider> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
}
