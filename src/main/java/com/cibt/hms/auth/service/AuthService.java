package com.cibt.hms.auth.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.cibt.hms.auth.entity.User;
import com.cibt.hms.auth.entity.UserRole;
import com.cibt.hms.auth.repository.UserRepository;
import com.cibt.hms.auth.repository.UserRoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Override
    public UserDetails loadUserByUsername(String param) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(param);
        if (user == null) {
            throw new UsernameNotFoundException("");
        } else {
            AuthServiceDetail serviceDetail = new AuthServiceDetail(user.getEmail(), user.getPassword(),
                    getAuthorities(param));
            serviceDetail.setUser(user);
            serviceDetail.setLoginDate(new Date());
            
            return serviceDetail;
        }
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<UserRole> userRoles = userRoleRepository.findByUserEmail(user);

        for (UserRole userRole : userRoles) {
            authorities.add(new SimpleGrantedAuthority(userRole.getRole().getName()));
        }

        return authorities;
    }

}
