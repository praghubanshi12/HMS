package com.cibt.hms.auth.controller;

import com.cibt.hms.auth.entity.Role;
import com.cibt.hms.core.CRUDController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/roles")
public class RoleController extends CRUDController<Role> {
    public RoleController(){
        uri = "/roles";
        activeMenu = "auth";
        pageTitle = "Role";
        viewPath ="auth/roles";
    }
}