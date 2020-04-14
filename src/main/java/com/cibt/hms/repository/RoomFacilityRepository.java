package com.cibt.hms.repository;

import java.util.List;

import com.cibt.hms.entity.RoomFacility;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomFacilityRepository extends JpaRepository<RoomFacility, Integer> {
    @Query(nativeQuery = true, value = "SELECT r.* FROM room_facilities rf JOIN rooms r ON r.id = rf.room_id WHERE rf.facility_id in " + 
            "(:facilityIds) AND r.type=:roomType AND r.bed_type=:bedType AND r.is_smoking=:isSmoking AND r.is_balcony=:isBalcony GROUP BY r.id HAVING count(rf.facility_id) = :facilitiesCount")
    List<Integer> findRoomIdsByFacilities(@Param("facilityIds") List<Integer> facilityIds,
            @Param("facilitiesCount") Integer facilitiesCount, @Param("roomType") String roomType, @Param("bedType") String bedType, 
            @Param("isSmoking") boolean isSmoking, @Param("isBalcony") boolean isBalcony);

}
