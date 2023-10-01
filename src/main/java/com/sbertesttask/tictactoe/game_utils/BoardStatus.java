package com.sbertesttask.tictactoe.game_utils;

public class BoardStatus {
    private final boolean isOver;
    private final Seed winner;

    public BoardStatus(boolean isFinished, Seed winnerSeed) {
        this.isOver = isFinished;
        this.winner = winnerSeed;
    }

    public boolean isOver() {
        return isOver;
    }

    public Seed getWinnerSeed() {
        return winner;
    }
}
