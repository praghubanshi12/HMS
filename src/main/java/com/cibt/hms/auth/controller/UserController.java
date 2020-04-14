package com.cibt.hms.auth.controller;

import com.cibt.hms.auth.entity.User;
import com.cibt.hms.auth.repository.RoleRepository;
import com.cibt.hms.core.CRUDController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/users")
public class UserController extends CRUDController<User>{
    public UserController(){
        uri = "/users";
        activeMenu = "auth";
        pageTitle = "User";
        viewPath ="auth/users";
    }
    
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public String index(Model model) {
        model.addAttribute("roles", roleRepository.findAll());
        return super.index(model);
    }

}