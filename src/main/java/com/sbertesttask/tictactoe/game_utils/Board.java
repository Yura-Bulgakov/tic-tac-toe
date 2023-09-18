package com.sbertesttask.tictactoe.game_utils;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Board {

    private interface Action {
        void doAction(int row, int col);
    }

    public static final int BOARD_SIZE = 3;
    private final Seed[][] cells = new Seed[BOARD_SIZE][BOARD_SIZE];
    private final Set<Pos> freePositions = new HashSet<>(BOARD_SIZE * BOARD_SIZE);

    public Board() {
        forEachElement((row, col) -> {
            cells[row][col] = Seed.Empty;
            freePositions.add(new Pos(row, col));
        });
    }

    private static void forEachElement(Action action) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                action.doAction(row, col);
            }
        }
    }

    public Set<Pos> getFreePositions() {
        return Collections.unmodifiableSet(freePositions);
    }

    public Seed getSeedAtPosition(Pos pos) {
        return cells[pos.getRow()][pos.getCol()];
    }

    public void setSeedAtPosition(Pos pos, Seed seed) {
        Seed currentSeed = cells[pos.getRow()][pos.getCol()];
        if (currentSeed != Seed.Empty && seed != Seed.Empty) {
            throw new GameException("Позиция " + pos + " уже занята!");
        }
        if (currentSeed == seed) return;
        cells[pos.getRow()][pos.getCol()] = seed;
        if (seed == Seed.Empty) {
            freePositions.add(pos);
        } else {
            freePositions.remove(pos);
        }
    }

    public GameStatus getGameStatus() {
        final int[] rowScores = new int[BOARD_SIZE];
        final int[] colScores = new int[BOARD_SIZE];
        final int[] diag1Score = new int[1];
        final int[] diag2Score = new int[1];
        forEachElement((row, col) -> {
            Seed seed = this.cells[row][col];
            int delta = getDelta(seed);
            rowScores[row] += delta;
            colScores[col] += delta;
            if (row == col) {
                diag1Score[0] += delta;
            }
            if (row == BOARD_SIZE - col - 1) {
                diag2Score[0] += delta;
            }
        });

        for (Seed seed : new Seed[]{Seed.O, Seed.X}) {
            final int winPoints = BOARD_SIZE * getDelta(seed);
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (rowScores[i] == winPoints || colScores[i] == winPoints)
                    return new GameStatus(true, seed);
            }
            if (diag1Score[0] == winPoints || diag2Score[0] == winPoints)
                return new GameStatus(true, seed);
        }
        return new GameStatus(getFreePositions().isEmpty(), Seed.Empty);
    }

    private int getDelta(Seed seed) {
        if (seed == Seed.X) return 1;
        else if (seed == Seed.O) return -1;
        return 0;
    }

    public Board createFullCopy() {
        Board board = new Board();
        forEachElement((row, col) -> {
            Pos pos = new Pos(row, col);
            Seed seed = getSeedAtPosition(pos);
            board.setSeedAtPosition(pos, seed);
        });
        return board;
    }



}
