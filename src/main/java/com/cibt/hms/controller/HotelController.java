package com.cibt.hms.controller;

import java.util.ArrayList;
import java.util.List;

import com.cibt.hms.auth.entity.UserRole;
import com.cibt.hms.auth.repository.UserRoleRepository;
import com.cibt.hms.core.CRUDController;
import com.cibt.hms.entity.Hotel;
import com.cibt.hms.entity.Staff;
import com.cibt.hms.repository.HotelRepository;
import com.cibt.hms.repository.StaffRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/hotels")
public class HotelController extends CRUDController<Hotel> {

    public HotelController() {
        viewPath = "hotel";
        pageTitle = "Hotels";
        uri = "/hotels";
        activeMenu = "hotel";
    }

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Override
    public String index(Model model) {
       if (hasRole("superadmin")) {
            List<UserRole> userRoles = userRoleRepository.findByRoleId(2);
            List<Staff> managers = new ArrayList<Staff>();
            for (UserRole userRole : userRoles) {
                managers.add(staffRepository.findByUserId(userRole.getUser().getId()));
            }
            model.addAttribute("managers", managers);
            return viewPath + "/superadmin/index";
        }

        if (hasRole("manager")) {
            Staff staff = staffRepository.findByUserId(getLoggedInUser().getId());
            model.addAttribute("hotels", staff.getManagerHotels());
            return viewPath + "/index";
        }

        if (hasRole("customer")) {
            model.addAttribute("hotels", hotelRepository.findAll());
            return viewPath + "/index";
        }
        return viewPath + "/table";
    }

}