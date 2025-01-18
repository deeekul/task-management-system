package ru.vsu.cs.taskmanagementsystem.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.vsu.cs.taskmanagementsystem.security.dto.request.AuthenticationRequest;
import ru.vsu.cs.taskmanagementsystem.security.dto.request.RegisterRequest;
import ru.vsu.cs.taskmanagementsystem.security.dto.response.AuthenticationResponse;
import ru.vsu.cs.taskmanagementsystem.security.entity.UserDetailsImpl;
import ru.vsu.cs.taskmanagementsystem.security.entity.token.Token;
import ru.vsu.cs.taskmanagementsystem.security.entity.token.TokenRepository;
import ru.vsu.cs.taskmanagementsystem.security.exception.InvalidAuthorizationHeaderException;
import ru.vsu.cs.taskmanagementsystem.user.adapter.UserMapper;
import ru.vsu.cs.taskmanagementsystem.user.adapter.jpa.UserRepository;
import ru.vsu.cs.taskmanagementsystem.user.adapter.jpa.entity.User;
import ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.response.UserResponse;
import ru.vsu.cs.taskmanagementsystem.user.exception.InvalidPasswordException;
import ru.vsu.cs.taskmanagementsystem.user.exception.UserNotFoundException;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static ru.vsu.cs.taskmanagementsystem.security.entity.Role.USER;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserMapper userMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthenticationService authService;

    @Test
    void register_shouldReturnUserResponse_whenValidRequest() {
        // given
        var request = new RegisterRequest(
                "Иван",
                "Иванов",
                "ivanov_i",
                "p@ssw0rd",
                USER
        );
        var encodedPassword = "$2a$10$7wrtwctbL63wGqxCZvRv6ulQVkHrAgdHNiP3USKFE9V7Tj9kHnqC2";

        var userToSave = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .login(request.login())
                .password(encodedPassword)
                .role(request.role())
                .build();
        var savedUser = User.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .login("ivanov_i")
                .password(encodedPassword)
                .role(USER)
                .build();
        var mappedUser = new UserResponse(
                1L,
                "Иван",
                "Иванов",
                "ivanov_i",
                USER
        );

        doReturn(encodedPassword)
                .when(passwordEncoder)
                .encode(request.password());

        doReturn(savedUser)
                .when(userRepository)
                .save(userToSave);

        doReturn(mappedUser)
                .when(userMapper)
                .map(savedUser);

        // when
        var result = authService.register(request);

        // then
        var expectedResult = new UserResponse(
                1L,
                "Иван",
                "Иванов",
                "ivanov_i",
                USER
        );

        assertThat(result).isEqualTo(expectedResult);

        verify(passwordEncoder, times(1)).encode("p@ssw0rd");
        verify(userRepository, times(1)).save(userToSave);
        verify(userMapper, times(1)).map(savedUser);
        verifyNoMoreInteractions(passwordEncoder);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    void authenticate_shouldReturnAuthenticationResponse_whenValidCredentials() {
        // given
        var authRequest = new AuthenticationRequest(
                "ivanov_i",
                "p@ssw0rd"
        );
        var encodedPassword = "$2a$10$7wrtwctbL63wGqxCZvRv6ulQVkHrAgdHNiP3USKFE9V7Tj9kHnqC2";

        var foundUser = User.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .login("ivanov_i")
                .password(encodedPassword)
                .role(USER)
                .build();
        var accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjaHV5a292X2QiLC" +
                "JpYXQiOjE3MzU5MzA3MzgsImV4cCI6MTczNjAxNzEzOH0.PdEJBIblyu7ODiekRyqInTpvFXMvqxV0ChtIoKr5wKI";
        var refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjaHV5a292X2QiL" +
                "CJpYXQiOjE3MzU5MzA3MzgsImV4cCI6MTczNjUzNTUzOH0.0iJz-mFwGPtkltwk_IAJjjtzGNKasm3tMggUT39Mz6g";

        var userDetails = new UserDetailsImpl(foundUser);

        doReturn(Optional.of(foundUser))
                .when(userRepository)
                .findByLogin(authRequest.login());

        doReturn(true)
                .when(passwordEncoder)
                .matches(authRequest.password(), foundUser.getPassword());

        doReturn(userDetails)
                .when(userMapper)
                .mapToUserDetails(foundUser);

        doReturn(accessToken)
                .when(jwtService)
                .generateToken(userDetails);

        doReturn(refreshToken)
                .when(jwtService)
                .generateRefreshToken(userDetails);

        // when
        var result = authService.authenticate(authRequest);

        // then
        var expectedResult = new AuthenticationResponse(
                accessToken,
                refreshToken
        );

        assertThat(result.accessToken()).isEqualTo(expectedResult.accessToken());
        assertThat(result.refreshToken()).isEqualTo(expectedResult.refreshToken());

        verify(userRepository, times(1)).findByLogin(authRequest.login());
        verify(passwordEncoder, times(1)).matches("p@ssw0rd", foundUser.getPassword());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(userDetails);
        verify(jwtService, times(1)).generateRefreshToken(userDetails);
        verify(tokenRepository, times(1)).findAllValidTokenByUser(foundUser.getId());
        verify(tokenRepository, times(1)).save(any(Token.class));

        verifyNoMoreInteractions(userRepository, passwordEncoder, authenticationManager, jwtService, tokenRepository);
    }

    @Test
    void authenticate_shouldThrowUserNotFoundException_whenInvalidLogin() {
        // given
        var authRequest = new AuthenticationRequest(
                "nonexistent_u",
                "p@ssw0rd"
        );
        var emptyEntity = Optional.empty();

        doReturn(emptyEntity)
                .when(userRepository)
                .findByLogin(authRequest.login());

        // then
        assertThatThrownBy(() -> authService.authenticate(authRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователя с таким логином не существует!");

        verify(userRepository, times(1)).findByLogin(authRequest.login());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void authenticate_shouldThrowInvalidPasswordException_whenInvalidPassword() {
        // given
        var authRequest = new AuthenticationRequest(
                "ivanov_i",
                "wrong_password"
        );
        var encodedPassword = "$2a$10$7wrtwctbL63wGqxCZvRv6ulQVkHrAgdHNiP3USKFE9V7Tj9kHnqC2";
        var foundUser = User.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .login("ivanov_i")
                .password(encodedPassword)
                .role(USER)
                .build();

        doReturn(Optional.of(foundUser))
                .when(userRepository)
                .findByLogin(authRequest.login());

        doReturn(false)
                .when(passwordEncoder)
                .matches(authRequest.password(), encodedPassword);

        // then
        assertThatThrownBy(() -> authService.authenticate(authRequest))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Неверный пароль, попробуйте заново.");

        verify(userRepository, times(1)).findByLogin(authRequest.login());
        verify(passwordEncoder, times(1)).matches("wrong_password", encodedPassword);
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    void refreshToken_shouldReturnNewAccessToken_whenValidRefreshToken() throws IOException {
        // given
        var refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjaHV5a292X2QiL" +
                "CJpYXQiOjE3MzU5MzA3MzgsImV4cCI6MTczNjUzNTUzOH0.0iJz-mFwGPtkltwk_IAJjjtzGNKasm3tMggUT39Mz6g";
        var newAccessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjaHV5a292X2QiLC" +
                "JpYXQiOjE3MzU5MzA3MzgsImV4cCI6MTczNjAxNzEzOH0.PdEJBIblyu7ODiekRyqInTpvFXMvqxV0ChtIoKr5wKI";

        var login = "ivanov_i";
        var encodedPassword = "$2a$10$7wrtwctbL63wGqxCZvRv6ulQVkHrAgdHNiP3USKFE9V7Tj9kHnqC2";
        var foundUser = User.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .login("ivanov_i")
                .password(encodedPassword)
                .role(USER)
                .build();
        var userDetails = new UserDetailsImpl(foundUser);

        doReturn(foundUser.getLogin())
                .when(jwtService)
                .extractUsername(refreshToken);

        doReturn(Optional.of(foundUser))
                .when(userRepository)
                .findByLogin(login);

        doReturn(userDetails)
                .when(userMapper)
                .mapToUserDetails(foundUser);

        doReturn(true)
                .when(jwtService)
                .isTokenValid(refreshToken, userDetails);

        doReturn(newAccessToken)
                .when(jwtService)
                .generateToken(userDetails);

        doReturn("Bearer " + refreshToken)
                .when(request)
                .getHeader("Authorization");

        doReturn(mock(ServletOutputStream.class))
                .when(response)
                .getOutputStream();

        // when
        authService.refreshToken(request, response);

        // then
        verify(jwtService, times(1)).extractUsername(refreshToken);
        verify(userRepository, times(1)).findByLogin(login);
        verify(userMapper, times(1)).mapToUserDetails(foundUser);
        verify(jwtService, times(1)).isTokenValid(refreshToken, userDetails);
        verify(jwtService, times(1)).generateToken(userDetails);
        verify(tokenRepository, times(1)).findAllValidTokenByUser(foundUser.getId());
        verify(tokenRepository, times(1)).save(any(Token.class));
        verify(request, times(1)).getHeader("Authorization");

        verifyNoMoreInteractions(jwtService, userRepository, userMapper, tokenRepository, request);
    }

    @Test
    void refreshToken_shouldThrowInvalidAuthorizationHeaderException_whenInvalidAuthorizationHeader() {
        // given
        doReturn("Invalid Authorization Header")
                .when(request)
                .getHeader("Authorization");

        // then
        assertThatThrownBy(() -> authService.refreshToken(request, response))
                .isInstanceOf(InvalidAuthorizationHeaderException.class)
                .hasMessageContaining("Значение заголовка 'Authorization' некорректно или отсутствует!");

        verify(request, times(1)).getHeader(AUTHORIZATION);
    }
}