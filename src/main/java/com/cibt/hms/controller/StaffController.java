package com.cibt.hms.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.cibt.hms.auth.entity.Role;
import com.cibt.hms.auth.entity.User;
import com.cibt.hms.auth.repository.RoleRepository;
import com.cibt.hms.auth.repository.UserRepository;
import com.cibt.hms.core.CRUDController;
import com.cibt.hms.entity.RoomStaff;
import com.cibt.hms.entity.Staff;
import com.cibt.hms.repository.HotelRepository;
import com.cibt.hms.repository.RoomRepository;
import com.cibt.hms.repository.RoomStaffRepository;
import com.cibt.hms.repository.StaffRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/staffs")
public class StaffController extends CRUDController<Staff> {
    public StaffController() {
        viewPath = "staffs";
        pageTitle = "Staffs";
        uri = "/staffs";
        activeMenu = "staff";
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomStaffRepository roomStaffRepository;

    @Autowired
    private HttpSession session;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping(params = "hId")
    public String hotelStaffIndex(Model model, @RequestParam("hId") Integer hotelId) {
        model.addAttribute("hId", hotelId);// for sidebar
        session.setAttribute("currentHotelId", hotelId);
        return super.index(model);
    }

    @Override
    public String index(Model model) {
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("hotels", hotelRepository.findAll());
        return super.index(model);
    }

    @Override
    public String table(Model model) {
        if (hasRole("manager")) {
            Integer hotelId = (Integer) session.getAttribute("currentHotelId");
            model.addAttribute("records", staffRepository.findByHotelId(hotelId));
            session.removeAttribute("currentHotelId");
            return viewPath + "/table";
        }
        return super.table(model);
    }

    @GetMapping(value = "/room/{id}")
    @ResponseBody
    public List<Staff> getStaffsByRoomId(@PathVariable("id") Integer roomId) {
        return roomRepository.findById(roomId).get().getStaffs();
    }

    @GetMapping(value = "/hotel/{id}")
    @ResponseBody
    public List<Staff> getHotelStaffs(@PathVariable("id") Integer hotelId) {
        return hotelRepository.findById(hotelId).get().getStaffs();
    }

    @PostMapping(value = "/assign-room")
    @Transactional
    @ResponseBody
    public Staff assignRoom(RoomStaff roomStaff) {
        roomStaffRepository.save(roomStaff);
        return roomStaff.getStaff();
    }

    @PostMapping(value = "/grant-access")
    @ResponseBody
    @Transactional
    public Boolean grantAccess(@RequestParam("id") int id) {
        Staff staff = staffRepository.findById(id).get();
        User user = new User();
        user.setEmail(staff.getEmail());
        user.setPassword(encoder.encode(staff.getRole().getName() + "123"));
        List<Role> roles = new ArrayList<Role>();
        roles.add(staff.getRole());
        user.setRoles(roles);
        user.setStatus(true);
        userRepository.save(user);
        staff.setUserId(user.getId());
        staffRepository.save(staff);
        return staff.getUserId() > 0;
    }
}