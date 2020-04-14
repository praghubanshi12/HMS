package com.cibt.hms.repository;

import java.util.List;

import com.cibt.hms.entity.RoomTask;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTaskRepository extends JpaRepository<RoomTask, Integer>{
    List<RoomTask> findByRoomHotelId(Integer hotelId);
    List<RoomTask> findByRoomIdAndStaffId(Integer roomId, Integer staffId);
    
}