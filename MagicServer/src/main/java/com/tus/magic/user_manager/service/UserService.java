package com.tus.magic.user_manager.service;

import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tus.magic.user_manager.models.User;
import com.tus.magic.user_manager.repo.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public User createUser(User user) {
        // Encrypt the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    
    public boolean deleteUser(Long id) {
    	
    	if (id == 1) {
    		throw new IllegalStateException("Cannot delete the user with ID 1 (sys admin)");
        }
    	
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public User authenticate(String userName, String rawPassword) throws InvalidCredentialsException {
        Optional<User> userOptional = userRepository.findByUsername(userName);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return user; 
            }
        }
        
        throw new InvalidCredentialsException("Invalid username or password");
    }
}
