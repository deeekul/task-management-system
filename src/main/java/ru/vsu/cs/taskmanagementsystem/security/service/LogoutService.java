package ru.vsu.cs.taskmanagementsystem.security.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import ru.vsu.cs.taskmanagementsystem.security.entity.token.TokenRepository;
import ru.vsu.cs.taskmanagementsystem.security.exception.TokenNotFoundException;


@RequiredArgsConstructor
@Service
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        var accessToken = authHeader.substring(7);
        var storedToken = tokenRepository.findByToken(accessToken)
                .orElseThrow(
                        () -> new TokenNotFoundException("Токен доступа не найден!")
                );
        storedToken.setExpired(true);
        storedToken.setRevoked(true);
        tokenRepository.save(storedToken);
        SecurityContextHolder.clearContext();
    }
}