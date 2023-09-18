package com.sbertesttask.tictactoe.entity;

public enum StatusCode {
    IN_PROGRESS(1, "Партия не закончена."),
    PLAYER_WIN(2, "Игрок победил!"),
    MACHINE_WIN(3, "Компьютер победил!"),
    NOBODY_WIN(4, "Ничья!");


    private final Integer code;
    private final String description;

    StatusCode(Integer i, String d) {
        this.code = i;
        this.description = d;
    }

    public Integer getCode(){return code;}

    public String getDescription(){return description;}
}
