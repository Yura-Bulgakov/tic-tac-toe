package com.sbertesttask.tictactoe.controller;

import com.sbertesttask.tictactoe.dtos.CreateGameDTO;
import com.sbertesttask.tictactoe.dtos.GameBoardDTO;
import com.sbertesttask.tictactoe.game_utils.Pos;
import com.sbertesttask.tictactoe.service.GameService;
import com.sbertesttask.tictactoe.service.MoveService;
import com.sbertesttask.tictactoe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {


    private final GameService gameService;
    private final MoveService moveService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateGameDTO createGameRequest, HttpServletRequest request){
        Long gameId = gameService.createGame(createGameRequest, request);
        return ResponseEntity
                .created(URI.create("/api/game/" + gameId)).build();

    }

    @GetMapping("/{game_id}")
    public ResponseEntity<GameBoardDTO> getGameBoard(@PathVariable("game_id") Long gameId, HttpServletRequest request){
       return ResponseEntity.ok(gameService.getGameBoard(gameId, request));
    }

    @PostMapping("/{game_id}")
    public ResponseEntity<?> makeMoveGame(@PathVariable("game_id") Long gameId, HttpServletRequest request,
                                                     @RequestBody Pos pos){
        boolean validMove = gameService.makePlayerMove(gameId, pos);
        if (validMove) {
            return ResponseEntity.ok(gameService.getGameBoard(gameId, request));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{game_id}/last_turn")
    public ResponseEntity<?> makeMoveGame(@PathVariable("game_id") Long gameId, HttpServletRequest request){
        boolean validUnMove = moveService.undoLastTurn(gameId);

        if (validUnMove) {
            return ResponseEntity.ok(gameService.getGameBoard(gameId, request));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
