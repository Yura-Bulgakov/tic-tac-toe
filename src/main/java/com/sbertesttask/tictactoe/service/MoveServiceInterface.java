package com.sbertesttask.tictactoe.service;

import com.sbertesttask.tictactoe.entity.Game;
import com.sbertesttask.tictactoe.entity.Move;
import com.sbertesttask.tictactoe.game_utils.Pos;

import java.util.List;
import java.util.Optional;

public interface MoveServiceInterface {
    public void createNewMove(Move move);
    public void deleteMoveById(Long id);
    public void deleteMoveByGameId(Long id);

    public boolean existsMoveInGame(Long gameId, int row, int col);
    public boolean existByIdAndTurn(Long gameId, int turn);
    public List<Move> findMovesByGameIdOrderByTurn(Long gameId);

    public List<Move> findMovesByGameIdAndActorOrderByTurn(Long gameId, boolean actor);

    public Optional<Move> findTopMoveByGameIdOrderByTurnDesc(Long gameId);
    public Optional<Move> findByIdAndTurn(Long gameId, int turn);

    public List<Pos> movesToPosList(List<Move> moves);

    public boolean undoLastTurn(Long gameId);

    public void makeMove(Game game, boolean actor, Pos pos);
}
