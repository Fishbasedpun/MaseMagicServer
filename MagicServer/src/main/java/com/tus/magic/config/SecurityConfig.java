package com.tus.magic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tus.magic.filter.JWTAuthenticationFilter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	
	private JWTAuthenticationFilter jwtFilter;


	public SecurityConfig(JWTAuthenticationFilter jwtFilter) {
	    this.jwtFilter = jwtFilter;
	}

    // Define a PasswordEncoder bean for your application.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configure the security filter chain.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	return http
    	        .csrf(csrf -> csrf.disable())
    	        .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) // Allow H2 Console frames
    	        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    	        // Authorise requests configuration
    	        .authorizeHttpRequests(authorize -> authorize
    	            // Allow all requests to the H2 console
    	            .requestMatchers("/h2-console/**").permitAll()
    	            .requestMatchers("api/users/login").permitAll()
    	            .requestMatchers("/sonarqube/**").permitAll()  // Allow SonarQube
    	            .requestMatchers("/actuator/**").permitAll()
    	            .requestMatchers("/api/cards").permitAll()
    	            .requestMatchers("/uploads/**").permitAll()
                    .requestMatchers("/api/users/{username}/favorites", "/favorites/**").authenticated()
    	            .requestMatchers("/api/users/**").permitAll()
    	            // Allow public access to specified endpoints and static resources
    	            .requestMatchers(
    	                "/",
    	                "/index.html", 
    	                "/script.js", 
    	                "/styles.css", 
    	                "/content/**", 
    	                "/assets/**"  // This covers /assets and all its subfolders/files
    	            ).permitAll()
    	            .anyRequest().permitAll()
    	        ).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).build();
    }
    
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");  // Ensure Spring Security recognizes roles
        grantedAuthoritiesConverter.setAuthoritiesClaimName("role"); // Extract roles from the "role" claim in JWT

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}
