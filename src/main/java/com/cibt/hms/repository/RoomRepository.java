package com.cibt.hms.repository;

import java.util.List;

import com.cibt.hms.entity.Room;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer>{
    List<Room> findByHotelId(Integer id, Sort sort);
    int countByRoomNoAndHotelId(Integer roomNo, Integer hotelId);
}