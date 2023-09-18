package com.sbertesttask.tictactoe.dtos;

import lombok.Data;

@Data
public class GameBoardDTO {
    private char[][] board;
    private String status;
}
