package com.cibt.hms.repository;

import com.cibt.hms.entity.Status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer>{
    Status findByName(String name);
}