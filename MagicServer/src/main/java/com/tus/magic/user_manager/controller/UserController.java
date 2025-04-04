package com.tus.magic.user_manager.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tus.magic.models.Card;
import com.tus.magic.services.JwtService;
import com.tus.magic.user_manager.dto.CreateUserRequest;
import com.tus.magic.user_manager.dto.LoginRequest;
import com.tus.magic.user_manager.dto.LoginResponse;
import com.tus.magic.user_manager.models.User;
import com.tus.magic.user_manager.repo.UserRepository;
import com.tus.magic.user_manager.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;
	private final JwtService jwtService;
	
	@Autowired
	private UserRepository userRepository;

	public UserController(UserService userService, JwtService jwtService) {
		this.userService = userService;
		this.jwtService = jwtService;
	}

	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
		// Convert the DTO to a User entity
		User user = new User();
		if (request.getUsername().length() >= 3) {
			user.setUsername(request.getUsername());
		} else {
			return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
		}

		if (isValidPassword(request.getPassword())) {
			user.setPassword(request.getPassword());
		} else {
			return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
		}
		user.setRole(request.getRole());

		User createdUser = userService.createUser(user);
		return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
	}

	private boolean isValidPassword(String password) {
		boolean containsUpperCase = false, containsLowerCase = false, containsNumber = false;
		for (char c : password.toCharArray()) {
			if (Character.isUpperCase(c)) {
				containsUpperCase = true;
			} else if (Character.isLowerCase(c)) {
				containsLowerCase = true;
			} else if (Character.isDigit(c)) {
				containsNumber = true;
			}
		}
		return ((password.length() >= 8) && containsUpperCase && containsLowerCase && containsNumber);
	}

	@GetMapping
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> users = userService.getAllUsers();
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		boolean deleted = userService.deleteUser(id);
		if (deleted) {
			return ResponseEntity.noContent().build(); // 204 No Content on success
		} else {
			return ResponseEntity.notFound().build(); // 404 if user not found
		}
	}

	@PostMapping("/login")  //authentication package because uses dto
    public ResponseEntity<?> loginValidation(@RequestBody LoginRequest request) {
    	try {
        	User user = userService.authenticate(request.getUsername(), request.getPassword());
        	String jwt = jwtService.generateToken(user.getUsername(), user.getRole());
        	LoginResponse loginResponse = new LoginResponse();
        	loginResponse.setJwt(jwt);
            return ResponseEntity.ok(loginResponse); // 200 OK with true indicating success
        } catch (InvalidCredentialsException e) {
        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false); // 401 Unauthorized with false indicating failure
        }
    }
	
	@GetMapping("/{userId}/favorites")
    public ResponseEntity<List<Card>> getUserFavorites(@PathVariable String userId) {
        Optional<User> userOptional = userRepository.findByUsername(userId);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Card> favorites = user.getFavoriteCards(); // Get favorite cards
            
            return ResponseEntity.ok(favorites.stream().toList());
        }
        return ResponseEntity.notFound().build();
    }
}