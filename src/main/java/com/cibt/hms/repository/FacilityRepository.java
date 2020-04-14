package com.cibt.hms.repository;

import java.util.List;

import com.cibt.hms.entity.Facility;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Integer> {
    List<Facility> findByHotelId(Integer hotelId);
}
