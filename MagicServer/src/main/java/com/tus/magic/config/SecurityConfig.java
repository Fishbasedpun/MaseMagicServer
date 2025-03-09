package com.tus.magic.config;

/*import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	http
        .csrf(csrf -> csrf.disable())  // ✅ Disable CSRF (fixes 403 errors)
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()  // ✅ Allow all requests without authentication
        )
        .formLogin(login -> login.disable()) // ✅ Disable login page
        .httpBasic(basic -> basic.disable()); // ✅ Disable HTTP Basic authentication


        return http.build();
    }
}*/
