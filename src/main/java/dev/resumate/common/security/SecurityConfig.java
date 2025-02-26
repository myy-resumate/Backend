package dev.resumate.common.security;

import dev.resumate.common.security.JwtExceptionFilter;
import dev.resumate.common.security.JwtFilter;
import dev.resumate.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtExceptionFilter jwtExceptionFilter;

    public static final String[] PERMIT_ALL_URL = {  //인증 안해도 되는 요청들
            "/swagger-ui/**", "/v3/api-docs/**", //"/api/test/hello",
            "/api/members", "/api/members/login", "/api/members/reissue"
    };

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(auth -> auth.disable())
                .formLogin(auth -> auth.disable())
                .httpBasic(auth -> auth.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PERMIT_ALL_URL).permitAll()
                        .requestMatchers("/api/**").hasAnyAuthority(Role.MEMBER.name())
                        .anyRequest().authenticated())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtFilter.class)
                /*.exceptionHandling(config -> config
                        .authenticationEntryPoint(authenticationEntryPoint))*/ //이거로 변경 예정
                .build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
