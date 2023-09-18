package com.sbertesttask.tictactoe.dtos;

import lombok.Data;

@Data
public class RegistrationUserDTO {
    private String login;
    private String password;
    private String confirmPassword;

}
