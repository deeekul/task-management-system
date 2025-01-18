package ru.vsu.cs.taskmanagementsystem.user.adapter.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.cs.taskmanagementsystem.user.UserService;
import ru.vsu.cs.taskmanagementsystem.user.adapter.rest.api.UserApi;
import ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.request.ChangePasswordRequest;
import ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.response.UserResponse;
import ru.vsu.cs.taskmanagementsystem.util.ErrorMessage;

import java.security.Principal;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static ru.vsu.cs.taskmanagementsystem.util.ValidationErrorsUtil.returnErrorsToClient;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController implements UserApi {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") Long id) {
        return ok(userService.getUserById(id));
    }

    @GetMapping("/login")
    public ResponseEntity<UserResponse> getUserByLogin(@RequestParam(value = "login") String login) {
        return ok(userService.getUserByLogin(login));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ok().build();
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            BindingResult bindingResult,
            Principal connectedUser) {
        if (bindingResult.hasErrors()) {
            String errorsMessage = returnErrorsToClient(bindingResult);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage(errorsMessage, 400));
        }
        userService.changePassword(request, connectedUser);
        return ok().build();
    }
}