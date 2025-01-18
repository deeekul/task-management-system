package ru.vsu.cs.taskmanagementsystem.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vsu.cs.taskmanagementsystem.security.dto.request.AuthenticationRequest;
import ru.vsu.cs.taskmanagementsystem.security.dto.request.RegisterRequest;
import ru.vsu.cs.taskmanagementsystem.security.dto.response.AuthenticationResponse;
import ru.vsu.cs.taskmanagementsystem.security.entity.token.Token;
import ru.vsu.cs.taskmanagementsystem.security.entity.token.TokenRepository;
import ru.vsu.cs.taskmanagementsystem.security.entity.token.TokenType;
import ru.vsu.cs.taskmanagementsystem.security.exception.InvalidAuthorizationHeaderException;
import ru.vsu.cs.taskmanagementsystem.user.adapter.UserMapper;
import ru.vsu.cs.taskmanagementsystem.user.adapter.jpa.UserRepository;
import ru.vsu.cs.taskmanagementsystem.user.adapter.jpa.entity.User;
import ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.response.UserResponse;
import ru.vsu.cs.taskmanagementsystem.user.exception.InvalidPasswordException;
import ru.vsu.cs.taskmanagementsystem.user.exception.UserNotFoundException;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final UserMapper userMapper;

    public UserResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .login(request.login())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();
        var savedUser = userRepository.save(user);

        return userMapper.map(savedUser);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByLogin(request.login())
                .orElseThrow(
                        () -> new UserNotFoundException("Пользователя с таким логином не существует!"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidPasswordException("Неверный пароль, попробуйте заново.");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.login(),
                request.password()
                )
        );

        var userDetails = userMapper.mapToUserDetails(user);
        var accessToken = jwtService.generateToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .user(user)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidAuthorizationHeaderException(
                    "Значение заголовка 'Authorization' некорректно или отсутствует!"
            );
        }
        var refreshToken = authHeader.substring(7);
        var userLogin = jwtService.extractUsername(refreshToken);
        if (userLogin != null) {
            var user = userRepository.findByLogin(userLogin).
                    orElseThrow();
            var userDetails = userMapper.mapToUserDetails(user);
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                var accessToken = jwtService.generateToken(userDetails);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}