package com.cibt.hms.repository;

import java.util.Date;

import com.cibt.hms.entity.BookedRooms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookedRoomsRepository extends JpaRepository<BookedRooms, Integer> {
        @Query(nativeQuery = true, value = "SELECT * from booked_rooms WHERE room_id=:roomId AND ((booking_start_date<=:endDate AND booking_end_date>=:endDate)"
                        + " OR (booking_start_date<=:startDate AND booking_end_date>=:startDate) OR (booking_start_date>=:startDate AND booking_end_date<=:endDate))")
        BookedRooms findBookedRoomByBookingDateRange(@Param("roomId") Integer roomId,
                        @Param("startDate") Date startDate, @Param("endDate") Date endDate);

        Long deleteByBookingId(Integer bookingId);

        Long deleteByBookingIdAndRoomId(Integer bookingId, Integer roomId);

        BookedRooms findByBookingIdAndRoomId(Integer bookingId, Integer roomId);

        BookedRooms findByStartDateAndRoomId(Date currentDate, Integer roomId);
}
