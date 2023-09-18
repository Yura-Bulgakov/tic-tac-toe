package com.sbertesttask.tictactoe.game_utils;

public enum Seed {

    Empty('-'), O('O'), X('X');

    private final char value;

    Seed(char value) {
        this.value = value;
    }
    public char getValue(){
        return value;
    }

    public String toString(){
        return String.valueOf(value);
    }
}
