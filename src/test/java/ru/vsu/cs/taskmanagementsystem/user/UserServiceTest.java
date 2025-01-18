package ru.vsu.cs.taskmanagementsystem.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.vsu.cs.taskmanagementsystem.user.adapter.UserMapper;
import ru.vsu.cs.taskmanagementsystem.user.adapter.jpa.UserRepository;
import ru.vsu.cs.taskmanagementsystem.user.adapter.jpa.entity.User;
import ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.request.ChangePasswordRequest;
import ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.response.UserResponse;
import ru.vsu.cs.taskmanagementsystem.user.exception.UserNotFoundException;
import ru.vsu.cs.taskmanagementsystem.user.exception.InvalidPasswordException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static ru.vsu.cs.taskmanagementsystem.security.entity.Role.USER;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsers_shouldReturnListOfUserResponses_whenCalled() {
        // given
        var foundUsers = List.of(
                User.builder()
                        .id(1L)
                        .firstName("Иван")
                        .lastName("Иванов")
                        .login("ivanov_i")
                        .password("$2a$10$7wrtwctbL63wGqxCZvRv6ulQVkHrAgdHNiP3USKFE9V7Tj9kHnqC2")
                        .role(USER)
                        .build(),
                User.builder()
                        .id(2L)
                        .firstName("Александр")
                        .lastName("Малышев")
                        .login("malyshev_a")
                        .password("$2a$10$KoT4Ctc7aheAEmBZFDH0TOQVOSNtm08qNkzm41pUqJREpm4xSYicS")
                        .role(USER)
                        .build()
        );

        var mappedUsers = List.of(
                new UserResponse(
                        1L,
                        "Иван",
                        "Иванов",
                        "ivanov_i",
                        USER
                ),
                new UserResponse(
                        2L,
                        "Александр",
                        "Малышев",
                        "malyshev_a",
                        USER
                )
        );

        doReturn(foundUsers)
                .when(userRepository)
                .findAll();

        doReturn(mappedUsers.get(0))
                .when(userMapper)
                .map(foundUsers.get(0));

        doReturn(mappedUsers.get(1))
                .when(userMapper)
                .map(foundUsers.get(1));

        // when
        var result = userService.getAllUsers();

        // then
        var expectedResult = List.of(
                new UserResponse(
                        1L,
                        "Иван",
                        "Иванов",
                        "ivanov_i",
                        USER
                ),
                new UserResponse(
                        2L,
                        "Александр",
                        "Малышев",
                        "malyshev_a",
                        USER
                )
        );
        assertThat(result).isEqualTo(expectedResult);

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(2)).map(any(User.class));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    void getUserById_shouldReturnUserResponse_whenUserExists() {
        // given
        final Long id = 1L;
        var foundUser = User.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .login("ivanov_i")
                .password("$2a$10$7wrtwctbL63wGqxCZvRv6ulQVkHrAgdHNiP3USKFE9V7Tj9kHnqC2")
                .role(USER)
                .build();

        var mappedUser = new UserResponse(
                1L,
                "Иван",
                "Иванов",
                "ivanov_i",
                USER
        );
        doReturn(Optional.of(foundUser))
                .when(userRepository)
                .findById(id);
        doReturn(mappedUser)
                .when(userMapper)
                .map(foundUser);

        // when
        var result = userService.getUserById(id);

        // then
        var expectedResult = new UserResponse(
                1L,
                "Иван",
                "Иванов",
                "ivanov_i",
                USER
        );
        assertThat(result).isEqualTo(expectedResult);

        verify(userRepository, times(1)).findById(id);
        verify(userMapper, times(1)).map(foundUser);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    void getUserById_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        // given
        final Long id = 1L;
        var emptyEntity = Optional.empty();

        doReturn(emptyEntity)
                .when(userRepository)
                .findById(id);

        // then
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(id));

        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void getUserByLogin_shouldReturnUserResponse_whenUserExists() {
        // given
        final String login = "ivanov_i";
        var foundUser = User.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .login("ivanov_i")
                .password("$2a$10$7wrtwctbL63wGqxCZvRv6ulQVkHrAgdHNiP3USKFE9V7Tj9kHnqC2")
                .role(USER)
                .build();

        var mappedUser = new UserResponse(
                1L,
                "Иван",
                "Иванов",
                "ivanov_i",
                USER
        );

        doReturn(Optional.of(foundUser))
                .when(userRepository)
                .findByLogin(login);
        doReturn(mappedUser)
                .when(userMapper)
                .map(foundUser);

        // when
        var result = userService.getUserByLogin(login);

        // then
        var expectedResult = new UserResponse(
                1L,
                "Иван",
                "Иванов",
                "ivanov_i",
                USER
        );
        assertThat(result).isEqualTo(expectedResult);

        verify(userRepository, times(1)).findByLogin(login);
        verify(userMapper, times(1)).map(foundUser);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    void getUserByLogin_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        // given
        final String login = "ivanov_i";
        var emptyEntity = Optional.empty();

        doReturn(emptyEntity)
                .when(userRepository)
                .findByLogin(login);

        // then
        assertThatThrownBy(() -> userService.getUserByLogin(login))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь с login = ivanov_i не найден");

        verify(userRepository, times(1)).findByLogin(login);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUserById_shouldDeleteUser_whenUserExists() {
        //given
        final Long id = 1L;
        var foundUser = User.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .login("ivanov_i")
                .password("$2a$10$7wrtwctbL63wGqxCZvRv6ulQVkHrAgdHNiP3USKFE9V7Tj9kHnqC2")
                .role(USER)
                .build();

        doReturn(Optional.of(foundUser))
                .when(userRepository)
                .findById(id);

        // when
        userService.deleteUserById(id);

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).delete(foundUser);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUserById_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        // given
        final Long id = 1L;
        var optionalEmpty = Optional.empty();

        doReturn(optionalEmpty)
                .when(userRepository)
                .findById(id);

        // then
        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(id));

        verify(userRepository, times(1)).findById(id);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void changePassword_shouldUpdatePassword_whenValidRequest() {
        // given
        var request = new ChangePasswordRequest(
                "old_password",
                "new_password",
                "new_password"
        );
        var login = "ivanov_i";
        var encodedOldPassword = "$2a$10$7wrtwctbL63wGqxCZvRv6ulQVkHrAgdHNiP3USKFE9V7Tj9kHnqC2";
        var encodedNewPassword = "$2a$10$19XjGQXNHI7/lqWTnQzBcex5jDYX5DuXsMI1YE0u0HR9.qLmcz4zi";
        var user = User.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .login(login)
                .password(encodedOldPassword)
                .role(USER)
                .build();

        doReturn(Optional.of(user))
                .when(userRepository)
                .findByLogin(login);

        doReturn(true)
                .when(passwordEncoder)
                .matches("old_password", user.getPassword());

        doReturn(encodedNewPassword)
                .when(passwordEncoder)
                .encode("new_password");

        var principal = new UsernamePasswordAuthenticationToken(login, encodedOldPassword,
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));
        SecurityContextHolder.getContext().setAuthentication(principal);

        // when
        userService.changePassword(request, principal);

        // then
        assertThat(user.getPassword()).isEqualTo(encodedNewPassword);

        verify(userRepository, times(1)).findByLogin(login);
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void changePassword_shouldThrowPasswordChangeException_whenInvalidOldPassword() {
        // given
        var request = new ChangePasswordRequest(
                "wrong_password",
                "new_password",
                "new_password"
        );
        var login = "ivanov_i";
        var encodedOldPassword = "$2a$10$7wrtwctbL63wGqxCZvRv6ulQVkHrAgdHNiP3USKFE9V7Tj9kHnqC2";
        var user = User.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .login(login)
                .password(encodedOldPassword)
                .role(USER)
                .build();

        doReturn(Optional.of(user))
                .when(userRepository)
                .findByLogin(login);
        doReturn(false)
                .when(passwordEncoder)
                .matches("wrong_password", user.getPassword());

        var principal = new UsernamePasswordAuthenticationToken(login, encodedOldPassword,
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));
        SecurityContextHolder.getContext().setAuthentication(principal);

        // then
        assertThatThrownBy(() -> userService.changePassword(request, principal))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Указан неверный текущий пароль");

        verify(userRepository).findByLogin(login);
        verify(passwordEncoder, times(1)).matches("wrong_password", user.getPassword());
        verify(userRepository, never()).save(user);
        verifyNoMoreInteractions(passwordEncoder);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void changePassword_shouldThrowPasswordChangeException_whenPasswordsDoNotMatch() {
        // given
        var request = new ChangePasswordRequest(
                "old_password",
                "new_password",
                "other_password"
        );
        var login = "ivanov_i";
        var encodedOldPassword = "$2a$10$7wrtwctbL63wGqxCZvRv6ulQVkHrAgdHNiP3USKFE9V7Tj9kHnqC2";
        var user = User.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Иванов")
                .login(login)
                .password(encodedOldPassword)
                .role(USER)
                .build();

        doReturn(Optional.of(user))
                .when(userRepository)
                .findByLogin(login);
        doReturn(true)
                .when(passwordEncoder)
                .matches("old_password", user.getPassword());

        var principal = new UsernamePasswordAuthenticationToken(login, encodedOldPassword,
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));
        SecurityContextHolder.getContext().setAuthentication(principal);

        // then
        assertThatThrownBy(() -> userService.changePassword(request, principal))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Пароли не совпадают");

        verify(userRepository).findByLogin(login);
        verify(passwordEncoder, times(1)).matches("old_password", user.getPassword());
        verify(userRepository, never()).save(user);
        verifyNoMoreInteractions(passwordEncoder);
        verifyNoMoreInteractions(userRepository);
    }
}