package com.roomiesmart.identity.infrastructure.config;

import com.roomiesmart.identity.application.service.AuthService;
import com.roomiesmart.identity.domain.port.in.AuthUseCase;
import com.roomiesmart.identity.domain.port.out.PasswordEncoderPort;
import com.roomiesmart.identity.domain.port.out.SessionStorePort;
import com.roomiesmart.identity.domain.port.out.TokenProviderPort;
import com.roomiesmart.identity.domain.port.out.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthUseCase authUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            TokenProviderPort tokenProviderPort,
            SessionStorePort sessionStorePort) {
        return new AuthService(userRepositoryPort, passwordEncoderPort, tokenProviderPort, sessionStorePort);
    }
}
