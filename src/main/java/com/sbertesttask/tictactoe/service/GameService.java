package com.sbertesttask.tictactoe.service;

import com.sbertesttask.tictactoe.dtos.CreateGameDTO;
import com.sbertesttask.tictactoe.dtos.GameBoardDTO;
import com.sbertesttask.tictactoe.entity.Game;
import com.sbertesttask.tictactoe.entity.StatusCode;
import com.sbertesttask.tictactoe.entity.User;
import com.sbertesttask.tictactoe.entity.repository.GameRepository;
import com.sbertesttask.tictactoe.game_utils.*;
import com.sbertesttask.tictactoe.security.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final UserService userService;
    private final MoveService moveService;
    private final GameHelper gameHelper;
    private final JwtTokenUtils jwtTokenUtils;
    private Board board;

    public Optional<Game> finById(Long id){
        return gameRepository.findById(id);
    }
    public void createNewGame(Game game){ gameRepository.save(game);}

    public Long createGame(CreateGameDTO createRequest, HttpServletRequest request){
        User user = userService.getUserFromJwt(request);
        Game newGame = new Game();
        newGame.setStatus(StatusCode.IN_PROGRESS.getCode());
        newGame.setUser(user);
        newGame.setGoesFirst(createRequest.isActor());
        newGame.setModifiedDate(new Date());
        createNewGame(newGame);
        if (!newGame.isGoesFirst()){
            Seed machineSeed = Seed.X;
            Board board = new Board();
            makeMachineMoveByAI(machineSeed, board, newGame,null, null);
        }
        return newGame.getId();
    }

    public GameBoardDTO getGameBoard(Long gameID, HttpServletRequest request){
        Game game = gameRepository.findById(gameID).orElseThrow(() -> new RuntimeException("Данная игра отсутствует в БД!"));
        Seed playerSeed;
        if (game.isGoesFirst()){
            playerSeed = Seed.X;
        }else {
            playerSeed = Seed.O;
        }
        return returnGameBoard(playerSeed, game, moveService.movesToPosList(moveService.findMovesByGameIdAndActorOrderByTurn(gameID, true)),
                moveService.movesToPosList(moveService.findMovesByGameIdAndActorOrderByTurn(gameID, false)));

    }

    public GameBoardDTO returnGameBoard(Seed playerSeed, Game game, List<Pos> playerPos, List<Pos> machinePos){
        Seed machineSeed = playerSeed == Seed.O ? Seed.X : Seed.O;
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

    public void makeMachineMoveByAI(Seed machineSeed,Board board, Game game, List<Pos> playerPos, List<Pos> machinePos){
        Seed playerSeed = machineSeed == Seed.O ? Seed.X : Seed.O;
        if (playerPos != null && !playerPos.isEmpty()) {
            for(Pos pos: playerPos){
                board.setSeedAtPosition(new Pos(pos.getRow(), pos.getCol()), playerSeed);
            }
        }
        if (machinePos != null && !machinePos.isEmpty()) {
            for(Pos pos: machinePos){
                board.setSeedAtPosition(new Pos(pos.getRow(), pos.getCol()), playerSeed);
            }
        }
        Pos finePos = gameHelper.findOptimalMovement(board, machineSeed);
        if (finePos.getRow() > Board.BOARD_SIZE || finePos.getCol() > Board.BOARD_SIZE || moveService.existsMoveInGame(game.getId(), finePos.getRow(), finePos.getCol())){
            return;
        }
        board.setSeedAtPosition(finePos, machineSeed);
        if (game.getStatusAsEnum() == StatusCode.IN_PROGRESS){
            GameStatus gameStatus = board.getGameStatus();
            if (gameStatus.getWinnerSeed().equals(playerSeed)){
                game.setStatus(StatusCode.PLAYER_WIN.getCode());
            }else if (gameStatus.getWinnerSeed().equals(machineSeed)){
                game.setStatus(StatusCode.MACHINE_WIN.getCode());
            }else game.setStatus(StatusCode.NOBODY_WIN.getCode());
        }
        moveService.makeMove(game.getId(),false, finePos);
        game.setModifiedDate(new Date());
        createNewGame(game);
    }

    public boolean makePlayerMove(Long gameId, Pos pos){
        Game game = finById(gameId).orElseThrow(() -> new RuntimeException("Игра, по которой идет попытка действия, удалена"));
        if (pos.getRow() > Board.BOARD_SIZE || pos.getCol() > Board.BOARD_SIZE || moveService.existsMoveInGame(gameId, pos.getRow(), pos.getCol())){
            return false;
        }
        Seed playerSeed;
        if (game.isGoesFirst()){
            playerSeed = Seed.X;
        }else {
            playerSeed = Seed.O;
        }
        Seed machineSeed = playerSeed == Seed.O ? Seed.X : Seed.O;
        Board board = new Board();
        List<Pos> playerPosList = moveService.movesToPosList(moveService.findMovesByGameIdAndActorOrderByTurn(gameId, true));
        List<Pos> machinePosList = moveService.movesToPosList(moveService.findMovesByGameIdAndActorOrderByTurn(gameId, false));
        if (!playerPosList.isEmpty()) {
            for(Pos pos1: playerPosList){
                board.setSeedAtPosition(new Pos(pos1.getRow(), pos1.getCol()), playerSeed);
            }
        }
        if (!machinePosList.isEmpty()) {
            for(Pos pos2: machinePosList){
                board.setSeedAtPosition(new Pos(pos2.getRow(), pos2.getCol()), playerSeed);
            }
        }
        board.setSeedAtPosition(pos, playerSeed);
        if (game.getStatusAsEnum() == StatusCode.IN_PROGRESS){
            GameStatus gameStatus = board.getGameStatus();
            if (gameStatus.getWinnerSeed().equals(playerSeed)){
                game.setStatus(StatusCode.PLAYER_WIN.getCode());
            }else if (gameStatus.getWinnerSeed().equals(machineSeed)){
                game.setStatus(StatusCode.MACHINE_WIN.getCode());
            }else game.setStatus(StatusCode.NOBODY_WIN.getCode());
        }
        moveService.makeMove(game.getId(),true, pos);
        playerPosList.add(pos);
        makeMachineMoveByAI(machineSeed,board, game, playerPosList, machinePosList);
        return true;

    }

}
