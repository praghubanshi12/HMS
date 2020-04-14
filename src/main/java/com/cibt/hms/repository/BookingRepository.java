package com.cibt.hms.repository;

import java.util.List;

import com.cibt.hms.entity.Booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByHotelIdAndStatusIdIn(Integer hotelId, List<Integer> statusIds, Sort sort);
}
