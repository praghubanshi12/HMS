package com.cibt.hms.repository;

import com.cibt.hms.entity.RoomStaff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomStaffRepository extends JpaRepository<RoomStaff, Integer>{
    
}