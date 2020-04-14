package com.cibt.hms.repository;

import java.util.List;

import com.cibt.hms.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    Staff findByEmail(String email);
    Staff findByUserId(Integer userId);
    List<Staff> findByHotelId(Integer hotelId);
}
