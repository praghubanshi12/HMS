package com.cibt.hms.controller;

import com.cibt.hms.core.CRUDController;
import com.cibt.hms.entity.Status;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/status")
public class StatusController extends CRUDController<Status>{
    public StatusController(){
        viewPath = "status";
        pageTitle = "Status";
        uri = "/status";
        activeMenu = "status";
    }

}