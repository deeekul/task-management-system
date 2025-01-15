package ru.vsu.cs.taskmanagementsystem.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import ru.vsu.cs.taskmanagementsystem.user.adapter.jpa.entity.User;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static ru.vsu.cs.taskmanagementsystem.security.entity.Role.ADMIN;
import static ru.vsu.cs.taskmanagementsystem.security.entity.Role.USER;


@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    private final AuthenticationProvider authProvider;

    private final LogoutHandler logoutHandler;

    private static final String[] UNRESTRICTED_URLS = {
            "/api/register",
            "/api/authenticate",
            "/api/refresh-token",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(UNRESTRICTED_URLS)
                                .permitAll()
                                .requestMatchers(HttpMethod.GET,"/api/users", "/api/users/**")
                                .hasAnyRole(ADMIN.name(), USER.name())
                                .requestMatchers(HttpMethod.PATCH,"/api/users/change-password")
                                .hasAnyRole(ADMIN.name(), USER.name())
                                .requestMatchers(HttpMethod.DELETE, "/api/users/{id}")
                                .hasRole(ADMIN.name())

                                .requestMatchers(HttpMethod.GET, "/api/tasks", "api/tasks/{id}")
                                .hasAnyRole(ADMIN.name(), USER.name())// TODO не работают роли, исправить и протестить
//                                .requestMatchers(HttpMethod.GET, "/api/tasks/**")
//                                .hasRole(ADMIN.name())
//                                .requestMatchers(HttpMethod.POST, "/api/tasks/{id}/comments")
//                                .hasAnyRole(ADMIN.name(), USER.name())
//                                .requestMatchers(HttpMethod.POST, "/api/tasks")
//                                .hasRole(ADMIN.name())
//                                .requestMatchers(HttpMethod.POST, "/api/tasks/{id}")
//                                .hasAnyRole(ADMIN.name(), USER.name())
//                                .requestMatchers(HttpMethod.DELETE, "/api/tasks/{id}")
//                                .hasRole(ADMIN.name())
                                .anyRequest()
                                .authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler(
                                        (request, response, authentication) -> SecurityContextHolder.clearContext()
                                )
                );
        return http.build();
    }
}