package com.cibt.hms.auth.service;

import java.util.Collection;
import java.util.Date;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class AuthServiceDetail extends User {

    private static final long serialVersionUID = 1L;
    com.cibt.hms.auth.entity.User user;
    private Date loginDate;

    public AuthServiceDetail(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public void setUser(com.cibt.hms.auth.entity.User user) {
        this.user = user;
    }

    public com.cibt.hms.auth.entity.User getUser() {
        return user;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public Date getLoginDate() {
        return loginDate;
    }

}
