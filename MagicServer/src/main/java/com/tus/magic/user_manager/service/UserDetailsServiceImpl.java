package com.tus.magic.user_manager.service;

import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.tus.magic.user_manager.models.User;
import com.tus.magic.user_manager.repo.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserRepository userRepository;

	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		Optional<User> userOptional = userRepository.findByUsername(username);

		if (userOptional.isEmpty()) {
			throw new UsernameNotFoundException("Can't find user.");
		}

		return userOptional.get();
	}
}