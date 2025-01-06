package com.shop.server.infrastructure.security.service;

import com.shop.server.entities.main.Staff;
import com.shop.server.infrastructure.security.oauth2.user.UserPrincipal;
import com.shop.server.infrastructure.security.repository.SecurityNhanVienRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final SecurityNhanVienRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        Optional<Staff> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            Staff staff = userOptional.get();
            String role = staff.getRole().name();
            return UserPrincipal.create(staff, role);
        }

        throw new UsernameNotFoundException("user not found with email : " + email);
    }

}