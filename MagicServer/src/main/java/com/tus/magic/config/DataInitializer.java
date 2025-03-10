package com.tus.magic.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.tus.magic.user_manager.models.Role;
import com.tus.magic.user_manager.models.User;
import com.tus.magic.user_manager.repo.UserRepository;
import com.tus.magic.user_manager.service.UserService;

@Component
public class DataInitializer implements ApplicationRunner {

 private final UserRepository userRepository;
 private final UserService userService;
 private final String username = "admin";
 private final String password = "admin";

 public DataInitializer(UserRepository userRepository, UserService userService) {
     this.userRepository = userRepository;
     this.userService = userService;
 }

 @Override
 public void run(ApplicationArguments args) throws Exception {
     // Check if an admin user exists (using username "admin")
     if (userRepository.findByUsername(username).isEmpty()) {
         // Create a new admin user. The password will be encrypted by the UserService.
         User admin = new User(username, password, Role.SYSTEM_ADMIN);
         userService.createUser(admin);
     }
 }
}
