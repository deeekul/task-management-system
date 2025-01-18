package ru.vsu.cs.taskmanagementsystem.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vsu.cs.taskmanagementsystem.user.adapter.UserMapper;
import ru.vsu.cs.taskmanagementsystem.user.adapter.jpa.UserRepository;
import ru.vsu.cs.taskmanagementsystem.user.adapter.jpa.entity.User;
import ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.request.ChangePasswordRequest;
import ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.response.UserResponse;
import ru.vsu.cs.taskmanagementsystem.user.exception.InvalidPasswordException;
import ru.vsu.cs.taskmanagementsystem.user.exception.UserNotFoundException;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers() {
        var sensors = userRepository.findAll();

        return sensors.stream()
                .map(userMapper::map)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        var user = findUserByIdOrThrowException(id);
        return userMapper.map(user);
    }

    public UserResponse getUserByLogin(String login) {
        var user = findUserByLoginOrThrowException(login);
        return userMapper.map(user);
    }

    public void deleteUserById(Long id) {
        var user = findUserByIdOrThrowException(id);
        userRepository.delete(user);
    }

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var login = (String) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        var user = userRepository.findByLogin(login).orElseThrow();

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Указан неверный текущий пароль");
        }
        if (!request.newPassword().equals(request.confirmationPassword())) {
            throw new InvalidPasswordException("Пароли не совпадают");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private User findUserByIdOrThrowException(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new UserNotFoundException("Пользователь с id = " + userId + " не найден!",
                                HttpStatus.NOT_FOUND)
                );
    }

    private User findUserByLoginOrThrowException(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(
                        () -> new UserNotFoundException("Пользователь с login = " + login + " не найден!",
                                HttpStatus.NOT_FOUND)
                );
    }
}