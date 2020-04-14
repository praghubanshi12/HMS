package com.cibt.hms.repository;

import com.cibt.hms.entity.BookingMatchingRooms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingMatchingRoomsRepository extends JpaRepository<BookingMatchingRooms, Integer> {
    Long deleteByBookingId(Integer bookingId);
}
