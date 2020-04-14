package com.cibt.hms.controller;

import javax.servlet.http.HttpSession;

import com.cibt.hms.core.SecuredController;
import com.cibt.hms.entity.Staff;
import com.cibt.hms.repository.HotelRepository;
import com.cibt.hms.repository.StaffRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/")
public class HomeController extends SecuredController {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private HttpSession session;

    @GetMapping()
    public String index(Model model) {
        if (hasRole("manager")) {
            Staff staff = staffRepository.findByUserId(getLoggedInUser().getId());
            model.addAttribute("hotels", staff.getManagerHotels());
            return "hotel/index";
        }

        if (hasRole("staff")) {
            Integer userId = getLoggedInUser().getId();
            Integer hotelId = staffRepository.findByUserId(userId).getHotel().getId();
            model.addAttribute("hId", hotelId);
            session.setAttribute("currentHotelId", hotelId);
            return "tasks/index";
        }

        if (hasRole("customer")) {
            model.addAttribute("hotels", hotelRepository.findAll());
            return "hotel/index";
        }

        if (hasRole("receptionist")) {
            Integer userId = getLoggedInUser().getId();
            Integer hotelId = staffRepository.findByUserId(userId).getHotel().getId();
            model.addAttribute("hId", hotelId);
            model.addAttribute("pageURI", "/bookings");
            session.setAttribute("currentHotelId", hotelId);
            return "booking/index";
        }
        return "index";
    }

}