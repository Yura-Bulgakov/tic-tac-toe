package com.sbertesttask.tictactoe.controller;

import com.sbertesttask.tictactoe.dtos.CreateGameDTO;
import com.sbertesttask.tictactoe.dtos.GameBoardDTO;
import com.sbertesttask.tictactoe.game_utils.Pos;
import com.sbertesttask.tictactoe.security.JwtTokenUtils;
import com.sbertesttask.tictactoe.service.GameServiceInterface;
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


    private final GameServiceInterface gameService;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping
    public ResponseEntity<?> createGame(@RequestBody CreateGameDTO createGameRequest, HttpServletRequest request){
        String username = jwtTokenUtils.getUsernameFromRequest(request);

        Long gameId = gameService.createGame(createGameRequest.isActor(), username);
        return ResponseEntity
                .created(URI.create("/api/game/" + gameId)).build();

    }

    @GetMapping("/{game_id}")
    public ResponseEntity<GameBoardDTO> getGameBoard(@PathVariable("game_id") Long gameId){
       return ResponseEntity.ok(gameService.getGameBoard(gameId));
    }

    @PostMapping("/{game_id}")
    public ResponseEntity<?> makeMoveGame(@PathVariable("game_id") Long gameId,
                                                     @RequestBody Pos pos){
        boolean validMove = gameService.makeMove(gameId, pos);
        if (validMove) {
            return ResponseEntity.ok(gameService.getGameBoard(gameId));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{game_id}/last_turn")
    public ResponseEntity<?> undoMoveGame(@PathVariable("game_id") Long gameId){
        boolean validUnMove = gameService.undoLastMove(gameId);

        if (validUnMove) {
            return ResponseEntity.ok(gameService.getGameBoard(gameId));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
