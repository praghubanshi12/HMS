package com.cibt.hms.auth.repository;

import java.util.List;

import com.cibt.hms.auth.entity.UserRole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    List<UserRole> findByUserEmail(String email);
    List<UserRole> findByRoleId(Integer id);
}