package com.cloudpulse.backend.config;

import com.cloudpulse.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig {

    private final UserRepository userRepository;

//    @Bean
//    public UserDetailsService userDetailsService() {
//
//        return username -> {
//
//            com.cloudpulse.backend.entity.User user =
//                    userRepository.findByEmail(username)
//                            .orElseThrow(() ->
//                                    new UsernameNotFoundException("User not found")
//                            );
//
//            return org.springframework.security.core.userdetails.User
//                    .withUsername(user.getEmail())
//                    .password(user.getPassword())
//                    .authorities("USER")
//                    .build();
//        };
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }

//    @Bean
//    public org.springframework.security.authentication.AuthenticationProvider authenticationProvider(
//            UserDetailsService userDetailsService,
//            PasswordEncoder passwordEncoder) {
//        org.springframework.security.authentication.dao.DaoAuthenticationProvider authProvider = new org.springframework.security.authentication.dao.DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder);
//        return authProvider;
//    }
}