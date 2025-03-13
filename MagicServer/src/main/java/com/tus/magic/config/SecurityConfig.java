package com.tus.magic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for APIs
            .authorizeHttpRequests(auth -> auth
                // ✅ Public Access
                .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cards", "/api/cards/search").permitAll()
                .requestMatchers("/", "/uploads/**", "/index.html", "/content/**", "/styles.css", "/script.js", "/images/**").permitAll()

                // ✅ USER Access
                .requestMatchers(HttpMethod.GET, "/favorites/stats").hasAnyAuthority("ROLE_USER", "ROLE_SYSTEM_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/users/{username}/favorites").hasAnyAuthority("ROLE_USER","ROLE_SYSTEM_ADMIN")
                .requestMatchers(HttpMethod.POST, "/favorites/add").hasAnyAuthority("ROLE_USER","ROLE_SYSTEM_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/favorites/remove").hasAnyAuthority("ROLE_USER","ROLE_SYSTEM_ADMIN")

                // ✅ SYSTEM_ADMIN Access
                .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("ROLE_SYSTEM_ADMIN")
                .requestMatchers("/**").hasAuthority("ROLE_SYSTEM_ADMIN")
                .anyRequest().authenticated() // Require authentication for everything else
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT Filter

        return http.build();
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
