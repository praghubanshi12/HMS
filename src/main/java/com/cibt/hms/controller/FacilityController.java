package com.cibt.hms.controller;

import java.util.List;

import javax.transaction.Transactional;

import com.cibt.hms.entity.Facility;
import com.cibt.hms.entity.RoomFacility;
import com.cibt.hms.repository.FacilityRepository;
import com.cibt.hms.repository.HotelRepository;
import com.cibt.hms.repository.RoomFacilityRepository;
import com.cibt.hms.repository.RoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/facilities")
public class FacilityController {

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private RoomFacilityRepository roomFacilityRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @GetMapping(value = "/room/{id}")
    @ResponseBody
    public List<Facility> getFacilitiesByRoomId(@PathVariable("id") Integer roomId) {
        return roomRepository.findById(roomId).get().getFacilities();
    }

    @GetMapping(value = "/hotel/{id}")
    @ResponseBody
    public List<Facility> getHotelFacilities(@PathVariable("id") Integer hotelId) {
        return hotelRepository.findById(hotelId).get().getFacilities();
    }
    
    @PostMapping(value = "/save-json")
    @Transactional
    @ResponseBody
    public Facility saveFacility(Facility facility) {
        Facility facilityNew = facilityRepository.save(facility);
        return facilityNew;
    }

    @PostMapping(value = "/room/save")
    @Transactional
    @ResponseBody
    public RoomFacility saveRoomFacility(RoomFacility roomFacility) {
        RoomFacility roomFacilityNew =  roomFacilityRepository.save(roomFacility);
        return roomFacilityNew;
    }

}