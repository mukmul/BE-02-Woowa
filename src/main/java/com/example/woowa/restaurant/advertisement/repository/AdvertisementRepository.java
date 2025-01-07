package com.example.woowa.restaurant.advertisement.repository;

import com.example.woowa.restaurant.advertisement.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    @Query(value = "SELECT * FROM advertisement WHERE title = :title AND deleted_at IS NOT NULL", nativeQuery = true)
    Optional<Advertisement> findDeletedAdvertisementByTitle(@Param("title") String title);
}
