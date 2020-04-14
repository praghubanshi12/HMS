package com.cibt.hms.auth.handler;

import javax.annotation.PostConstruct;

import com.cibt.hms.auth.repository.UserRepository;
import com.cibt.hms.core.SecuredController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StaticContextInitializer {

    @Autowired
    UserRepository userRepository;

    @PostConstruct
    public void init() {
        SecuredController.setUserRepository(userRepository);
    }
}