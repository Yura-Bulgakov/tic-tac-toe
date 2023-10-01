package com.sbertesttask.tictactoe.game_utils;

import org.springframework.stereotype.Service;

@Service
public class GameHelper {

    private static class Line{
        private final Pos[] line = new Pos[Board.BOARD_SIZE];

        Line(Pos pos1, Pos pos2, Pos pos3){
            line[0] = pos1;
            line[1] = pos2;
            line[2] = pos3;
        }
        Pos getPos(int index) {
            return line[index];
        }
    }

    private static final Line[] lines = new Line[]
            {
                    new Line(new Pos(0, 0), new Pos(0, 1), new Pos(0, 2)),
                    new Line(new Pos(1, 0), new Pos(1, 1), new Pos(1, 2)),
                    new Line(new Pos(2, 0), new Pos(2, 1), new Pos(2, 2)),
                    new Line(new Pos(0, 0), new Pos(1, 0), new Pos(2, 0)),
                    new Line(new Pos(0, 1), new Pos(1, 1), new Pos(2, 1)),
                    new Line(new Pos(0, 2), new Pos(1, 2), new Pos(2, 2)),
                    new Line(new Pos(0, 0), new Pos(1, 1), new Pos(2, 2)),
                    new Line(new Pos(0, 2), new Pos(1, 1), new Pos(2, 0)),
            };


    private Seed ourSeed;
    private Seed oppSeed;

    public Pos findOptimalMovement(Board board, Seed seed) {
        if (seed == Seed.Empty)
            throw new IllegalArgumentException("seed не должен быть Empty");
        ourSeed = seed;
        oppSeed = seed == Seed.O ? Seed.X : Seed.O;
        Score score = miniMax(board, ourSeed, 4);
        return score.getPos();
    }

    private Score miniMax(Board board, Seed seed, int depth) {
        if (seed == Seed.Empty)
            throw new IllegalArgumentException("seed не должен быть Empty");
        int bestScore = (seed == ourSeed) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Pos bestPos = null;
        if (depth == 0 || board.getBoardStatus().isOver()) {
            bestScore = evaluate(board);
        } else {
            Board clonedBoard = board.createFullCopy();
            for (Pos freePos : board.getFreePositions()) {
                clonedBoard.setSeedAtPosition(freePos, seed);
                if (seed == ourSeed) {
                    int currentScore = miniMax(clonedBoard, oppSeed, depth - 1).getScorePoints();
                    if (currentScore > bestScore) {
                        bestScore = currentScore;
                        bestPos = freePos;
                    }
                } else {
                    int currentScore = miniMax(clonedBoard, ourSeed, depth - 1).getScorePoints();
                    if (currentScore < bestScore) {
                        bestScore = currentScore;
                        bestPos = freePos;
                    }
                }
                clonedBoard.setSeedAtPosition(freePos, Seed.Empty);
            }
        }
        return new Score(bestPos, bestScore);
    }

    private int evaluateSimple(Board board) {
        BoardStatus status = board.getBoardStatus();
        if (status.isOver()) {
            return switch (status.getWinnerSeed()) {
                case Empty -> 0;
                case O, X -> ourSeed == status.getWinnerSeed() ? 1 : -1;
            };
        }
        return 0;
    }

    private int evaluate(Board board) {
        int score = 0;
        for (Line line : lines) {
            score += evaluateLine(board, line);
        }
        return score;
    }

    private int evaluateLine(Board board, Line line) {
        int score = 0;

        Seed cell1 = board.getSeedAtPosition(line.getPos(0));
        Seed cell2 = board.getSeedAtPosition(line.getPos(1));
        Seed cell3 = board.getSeedAtPosition(line.getPos(2));

        if (cell1 == ourSeed) {
            score = 1;
        } else if (cell1 == oppSeed) {
            score = -1;
        }
        if (cell2 == ourSeed) {
            if (score == 1) {
                score = 10;
            } else if (score == -1) {
                return 0;
            } else {
                score = 1;
            }
        } else if (cell2 == oppSeed) {
            if (score == -1) {
                score = -10;
            } else if (score == 1) {
                return 0;
            } else {
                score = -1;
            }
        }

        if (cell3 == ourSeed) {
            if (score > 0) {
                score *= 10;
            } else if (score < 0) {
                return 0;
            } else {
                score = 1;
            }
        } else if (cell3 == oppSeed) {
            if (score < 0) {
                score *= 10;
            } else if (score > 0) {
                return 0;
            } else {
                score = -1;
            }
        }
        return score;
    }

}
