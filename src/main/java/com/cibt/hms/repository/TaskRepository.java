package com.cibt.hms.repository;

import java.util.List;

import com.cibt.hms.entity.Task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer>{
    List<Task> findByHotelId(Integer hotelId);

}