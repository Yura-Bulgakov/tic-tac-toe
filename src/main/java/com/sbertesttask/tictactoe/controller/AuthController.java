package com.sbertesttask.tictactoe.controller;

import com.sbertesttask.tictactoe.dtos.JwtResponseDTO;
import com.sbertesttask.tictactoe.dtos.RegistrationUserDTO;
import com.sbertesttask.tictactoe.dtos.JwtRequestDTO;
import com.sbertesttask.tictactoe.exceptions.AppError;
import com.sbertesttask.tictactoe.security.JwtTokenUtils;
import com.sbertesttask.tictactoe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequestDTO authRequest){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Неправильны логины или пароль"), HttpStatus.BAD_REQUEST);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponseDTO(token));
    }


    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody RegistrationUserDTO registrationUserDTO){
        if (userService.doesUserExist(registrationUserDTO.getLogin())){
            return ResponseEntity.badRequest().body("Логин уже занят");
        }
        userService.registerUser(registrationUserDTO.getLogin(),registrationUserDTO.getPassword(),registrationUserDTO.getConfirmPassword());
        return ResponseEntity.ok().body("Пользователь успешно создан");

    }


}
