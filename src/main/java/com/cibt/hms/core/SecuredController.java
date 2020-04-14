package com.cibt.hms.core;

import javax.servlet.http.HttpSession;

import com.cibt.hms.auth.entity.Role;
import com.cibt.hms.auth.entity.User;
import com.cibt.hms.auth.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class SecuredController {
    protected String viewPath;
    protected String uri;
    protected String pageTitle;
    protected String activeMenu;

    private static UserRepository staticUserRepository;

    public static void setUserRepository(UserRepository repo) {
        SecuredController.staticUserRepository = repo;
    }

    @Autowired
    private HttpSession session;

    @ModelAttribute(value="pageURI")
    public String getPageURI(){
        return uri;
    }

    @ModelAttribute(value="pageTitle")
    public String getPageTitle(){
        return pageTitle;
    }

    @ModelAttribute(value="activeMenu")
    public String getActiveMenu(){
        return activeMenu;
    }

    public static boolean hasRole(String... roles){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = staticUserRepository.findByEmail(email);
        for(String role:roles){
            for(Role currentUserRole: user.getRoles()){
                if(currentUserRole.getName().equals(role)){
                    return true;
                }
            }
        }
        return false;
    }

    public User getLoggedInUser(){
        return (User)session.getAttribute("loggedUser");
    }

}