package com.sbertesttask.tictactoe.service;

import com.sbertesttask.tictactoe.dtos.GameBoardDTO;
import com.sbertesttask.tictactoe.entity.Game;
import com.sbertesttask.tictactoe.entity.StatusCode;
import com.sbertesttask.tictactoe.entity.User;
import com.sbertesttask.tictactoe.repository.GameRepository;
import com.sbertesttask.tictactoe.game_utils.*;
import com.sbertesttask.tictactoe.security.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final UserService userService;
    private final MoveService moveService;
    private final GameHelper gameHelper;
    private final JwtTokenUtils jwtTokenUtils;
    private Board board;

    public Optional<Game> findById(Long id){
        return gameRepository.findById(id);
    }
    public void saveGame(Game game){ gameRepository.save(game);}

    public Long createGame(boolean actor, String username){
        User user = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("Пользователя нет в базе!"));
        Game newGame = new Game();
        newGame.setStatus(StatusCode.IN_PROGRESS.getCode());
        newGame.setUser(user);
        newGame.setGoesFirst(actor);
        newGame.setModifiedDate(new Date());
        saveGame(newGame);
        if (!newGame.isGoesFirst()){
            Seed playerSeed = loadPlayerSeedByGame(newGame);
            Seed machineSeed = loadMachineSeedByGame(newGame);
            Board board = new Board();
            makeMachineMoveByAI(newGame, board, playerSeed, machineSeed);
            saveGame(newGame);
        }
        return newGame.getId();
    }

    public GameBoardDTO getGameBoard(Long gameID){
        Game game = gameRepository.findById(gameID).orElseThrow(() -> new RuntimeException("Игра не найдена!"));
        return returnGameBoard(game,
                moveService.movesToPosList(moveService.findMovesByGameIdAndActorOrderByTurn(gameID, true)),
                moveService.movesToPosList(moveService.findMovesByGameIdAndActorOrderByTurn(gameID, false)));
    }

    public GameBoardDTO returnGameBoard(Game game, List<Pos> playerPos, List<Pos> machinePos){
        Seed playerSeed = loadPlayerSeedByGame(game);
        Seed machineSeed = loadMachineSeedByGame(game);
        GameBoardDTO returnBoard = new GameBoardDTO();
        char[][] charCells = new char[Board.BOARD_SIZE][Board.BOARD_SIZE];
        for (int i = 0; i < Board.BOARD_SIZE; i++){
            for (int j = 0; j < Board.BOARD_SIZE; j++){
                charCells[i][j] = Seed.Empty.getValue();
            }
        }
        if (!playerPos.isEmpty()) {
            for(Pos pos: playerPos){
                charCells[pos.getRow()][pos.getCol()] = playerSeed.getValue();
            }
        }
        if (!machinePos.isEmpty()) {
            for(Pos pos: machinePos){
                charCells[pos.getRow()][pos.getCol()] = machineSeed.getValue();
            }
        }
        returnBoard.setBoard(charCells);
        returnBoard.setStatus(game.getStatusAsEnum().getDescription());
        return returnBoard;
    }


    public boolean makeMove(Long gameId, Pos pos){
        Game game = findById(gameId).orElseThrow(() -> new RuntimeException("Игра, по которой идет попытка действия, не найдена"));
        if (pos.getRow() >= Board.BOARD_SIZE ||
                pos.getCol() >= Board.BOARD_SIZE ||
                moveService.existsMoveInGame(gameId, pos.getRow(), pos.getCol())){
            return false;
        }
        Seed playerSeed = loadPlayerSeedByGame(game);
        Seed machineSeed = loadMachineSeedByGame(game);
        Board board = loadBoardByGame(game, playerSeed, machineSeed);
        if (board.getBoardStatus().isOver()){
            return false;
        }
        makePlayerMove(game, pos, board, playerSeed, machineSeed);
        makeMachineMoveByAI(game, board, playerSeed, machineSeed);
        setGameStatus(game, board, playerSeed, machineSeed);
        saveGame(game);
        return true;
    }

    public void makeMachineMoveByAI(Game game, Board board, Seed playerSeed, Seed machineSeed){
        Set<Pos> availPos = board.getFreePositions();
        if (availPos.isEmpty()){
            return;
        }
        Pos finePos = gameHelper.findOptimalMovement(board, machineSeed);
        if (finePos.getRow() > Board.BOARD_SIZE ||
                finePos.getCol() > Board.BOARD_SIZE ||
                moveService.existsMoveInGame(game.getId(), finePos.getRow(), finePos.getCol())){
            return;
        }
        board.setSeedAtPosition(finePos, machineSeed);
        moveService.makeMove(game,false, finePos);
        game.setModifiedDate(new Date());
    }

    public void makePlayerMove(Game game, Pos pos, Board board, Seed playerSeed, Seed machineSeed){
        board.setSeedAtPosition(pos, playerSeed);
        game.setModifiedDate(new Date());
        moveService.makeMove(game,true, pos);
    }

    private Seed loadPlayerSeedByGame(Game game){
        return game.isGoesFirst() ? Seed.X : Seed.O;
    }

    private Seed loadMachineSeedByGame(Game game){
        return game.isGoesFirst() ? Seed.O : Seed.X;
    }

    private void setGameStatus(Game game, Board board, Seed playerSeed, Seed machineSeed){
        BoardStatus boardStatus = board.getBoardStatus();
        if (boardStatus.isOver() && game.getStatusAsEnum() == StatusCode.IN_PROGRESS){
            if (boardStatus.getWinnerSeed().equals(playerSeed)){
                game.setStatus(StatusCode.PLAYER_WIN.getCode());
            }else if (boardStatus.getWinnerSeed().equals(machineSeed)){
                game.setStatus(StatusCode.MACHINE_WIN.getCode());
            }else game.setStatus(StatusCode.NOBODY_WIN.getCode());
        }
    }

    private Board loadBoardByGame(Game game, Seed playerSeed, Seed machineSeed){
        Board board = new Board();
        List<Pos> playerPosList = moveService.movesToPosList(moveService.findMovesByGameIdAndActorOrderByTurn(game.getId(), true));
        List<Pos> machinePosList = moveService.movesToPosList(moveService.findMovesByGameIdAndActorOrderByTurn(game.getId(), false));
        if (!playerPosList.isEmpty()) {
            for(Pos pos1: playerPosList){
                board.setSeedAtPosition(new Pos(pos1.getRow(), pos1.getCol()), playerSeed);
            }
        }
        if (!machinePosList.isEmpty()) {
            for(Pos pos2: machinePosList){
                board.setSeedAtPosition(new Pos(pos2.getRow(), pos2.getCol()), machineSeed);
            }
        }
        return board;
    }

}
