/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cibt.hms.auth.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.cibt.hms.auth.service.AuthServiceDetail;
import com.cibt.hms.entity.Customer;
import com.cibt.hms.entity.Staff;
import com.cibt.hms.repository.CustomerRepository;
import com.cibt.hms.repository.StaffRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 *
 * @author user
 */
@Component
public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private HttpSession session;

    public boolean checkUserId(Authentication authentication, int id) {
        String name = authentication.getName();

        if (staffRepository.findByEmail(name) != null) {
            Staff result = staffRepository.findByEmail(name);
            return result != null && result.getId() == id;
        }
        if (customerRepository.findByEmail(name) != null) {
            Customer result = customerRepository.findByEmail(name);
            return result != null && result.getId() == id;
        }
        return false;
    }

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication)
            throws IOException, ServletException {
        if (authentication.isAuthenticated()) {
            AuthServiceDetail detail = (AuthServiceDetail) authentication.getPrincipal();
            session.setAttribute("loggedUser", detail.getUser());
        }
        super.onAuthenticationSuccess(req, res, authentication);
    }

}
