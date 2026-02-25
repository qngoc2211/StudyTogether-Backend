package com.example.demo.config;

import com.example.demo.entity.Users;
import com.example.demo.repository.UsersRepository;   // SỬA CHỖ NÀY

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner runner(UsersRepository repo,  // SỬA CHỖ NÀY
                                    PasswordEncoder encoder) {

        return args -> {

            if (repo.findByUsername("admin").isEmpty()) {

                Users admin = new Users();
                admin.setUsername("admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");

                repo.save(admin);

                System.out.println("ADMIN created!");
            }

            if (repo.findByUsername("user").isEmpty()) {

                Users user = new Users();
                user.setUsername("user");
                user.setEmail("user@gmail.com");
                user.setPassword(encoder.encode("user123"));
                user.setRole("ROLE_USER");

                repo.save(user);

                System.out.println("USER created!");
            }
        };
    }
}
