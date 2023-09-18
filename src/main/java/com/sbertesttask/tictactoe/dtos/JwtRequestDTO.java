package com.sbertesttask.tictactoe.dtos;

import lombok.Data;

@Data
public class JwtRequestDTO {
    private String username;
    private String password;
}
