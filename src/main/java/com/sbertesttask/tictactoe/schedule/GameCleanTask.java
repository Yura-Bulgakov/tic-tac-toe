package com.sbertesttask.tictactoe.schedule;

import com.sbertesttask.tictactoe.entity.Game;
import com.sbertesttask.tictactoe.service.GameServiceInterface;
import com.sbertesttask.tictactoe.service.MoveServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GameCleanTask {

    private final GameServiceInterface gameService;
    private final MoveServiceInterface moveService;

    @Scheduled(cron = "0 0 0  * * ?")
    public void cleanInactiveGame(){
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date beforeDate = calendar.getTime();
        List<Game> inactiveGames = gameService.findByModifiedDateBefore(beforeDate);
        for (Game game: inactiveGames){
            moveService.deleteMoveByGameId(game.getId());
            gameService.deleteGame(game.getId());
        }
    }
}
