package com.cibt.hms.auth.controller;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.cibt.hms.auth.entity.Role;
import com.cibt.hms.auth.entity.User;
import com.cibt.hms.auth.repository.RoleRepository;
import com.cibt.hms.auth.repository.UserRepository;
import com.cibt.hms.entity.Customer;
import com.cibt.hms.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/register")
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder encoder;


    @GetMapping()
    public String index(Model model) {
        return "register/index";
    }

    @PostMapping()
    @Transactional
    public String registerCustomer(Customer customer, @RequestParam("password") String password){
        User user = new User();
        user.setEmail(customer.getEmail());
        user.setPassword(encoder.encode(password));
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findById(5).get());
        user.setRoles(roles);
        userRepository.save(user);
        customer.setUser(user);
        customerRepository.save(customer);

        return "login/index";
    }

}