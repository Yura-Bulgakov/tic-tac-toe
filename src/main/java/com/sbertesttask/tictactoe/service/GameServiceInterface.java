package com.sbertesttask.tictactoe.service;

import com.sbertesttask.tictactoe.dtos.GameBoardDTO;
import com.sbertesttask.tictactoe.entity.Game;
import com.sbertesttask.tictactoe.game_utils.Pos;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface GameServiceInterface{

    public Long createGame(boolean actor, String username);

    public List<Game> findByModifiedDateBefore(Date date);

    public void deleteGame(Long id);

    public Optional<Game> findById(Long id);

    public GameBoardDTO getGameBoard(Long gameID);

    public boolean makeMove(Long gameId, Pos pos);

    public boolean undoLastMove(Long gameId);



}
