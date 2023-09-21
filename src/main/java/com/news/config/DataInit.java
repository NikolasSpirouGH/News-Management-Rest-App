package com.news.config;

import com.news.entity.Role;
import com.news.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        initializeRoles();
    }

    private void initializeRoles() {
        Role roleJournalist = roleRepository.save(new Role(1L,"JOURNALIST"));
        Role roleEditor = roleRepository.save(new Role(2L,"EDITOR"));
        Role roleAdmin = roleRepository.save(new Role(3L,"ADMIN"));
    }
}

