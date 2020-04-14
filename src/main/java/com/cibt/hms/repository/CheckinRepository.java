package com.cibt.hms.repository;

import java.util.List;

import com.cibt.hms.entity.Checkin;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckinRepository extends JpaRepository<Checkin, Integer> {
    List<Checkin> findByRoomHotelIdAndCheckedTrue(Integer hotelId);
    List<Checkin> findByRoomHotelId(Integer hotelId, Sort sort);
}
