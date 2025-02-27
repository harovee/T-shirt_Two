package com.shop.server.infrastructure.security.service;

import com.shop.server.entities.main.KhachHang;
import com.shop.server.entities.main.NhanVien;
import com.shop.server.infrastructure.constants.module.Role;
import com.shop.server.infrastructure.security.oauth2.session.InfoUserTShirt;
import com.shop.server.infrastructure.security.oauth2.user.UserPrincipal;
import com.shop.server.infrastructure.security.repository.SecurityClientRepository;
import com.shop.server.infrastructure.security.repository.SecurityStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final InfoUserTShirt infoUserTShirt;

    private final SecurityStaffRepository staffRepository;

    private final SecurityClientRepository clientRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        if (infoUserTShirt.getRoleName().equalsIgnoreCase(Role.ADMIN.name()) || infoUserTShirt.getRoleName().equalsIgnoreCase(Role.USER.name())) {
            Optional<NhanVien> staffOptional = staffRepository.findByEmail(email);
            if (staffOptional.isPresent()) {
                NhanVien staff = staffOptional.get();
                String role = staff.getRole().name();
                return UserPrincipal.create(staff, role);
            }
        } else if (infoUserTShirt.getRoleName().equalsIgnoreCase(Role.CLIENT.name())) {
            Optional<KhachHang> clientOptional = clientRepository.findByEmail(email);
            if (clientOptional.isPresent()) {
                KhachHang client = clientOptional.get();
                String role = Role.CLIENT.name();
                return UserPrincipal.create(client, role);
            }
        }

        throw new UsernameNotFoundException("user staff not found with email : " + email);
    }

}