package ru.vsu.cs.taskmanagementsystem.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.cs.taskmanagementsystem.security.api.AuthenticationApi;
import ru.vsu.cs.taskmanagementsystem.security.dto.request.AuthenticationRequest;
import ru.vsu.cs.taskmanagementsystem.security.dto.request.RegisterRequest;
import ru.vsu.cs.taskmanagementsystem.security.service.AuthenticationService;
import ru.vsu.cs.taskmanagementsystem.util.ErrorMessage;

import java.io.IOException;

import static ru.vsu.cs.taskmanagementsystem.util.ValidationErrorsUtil.returnErrorsToClient;


@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AuthenticationController implements AuthenticationApi {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorsMessage = returnErrorsToClient(bindingResult);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage(errorsMessage, 400));
        }
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AuthenticationRequest request,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorsMessage = returnErrorsToClient(bindingResult);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage(errorsMessage, 400));
        }
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        authService.refreshToken(request, response);
    }
}