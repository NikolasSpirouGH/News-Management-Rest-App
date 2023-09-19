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
        // Initialize roles
        initializeRoles();
    }

    private void initializeRoles() {
        Role roleVisitor = roleRepository.save(new Role(1L,"VISITOR"));
        Role roleJournalist = roleRepository.save(new Role(2L,"JOURNALIST"));
        Role roleEditor = roleRepository.save(new Role(3L,"EDITOR"));
        Role roleAdmin = roleRepository.save(new Role(4L,"ADMIN"));
    }
}

