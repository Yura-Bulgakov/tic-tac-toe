package com.sbertesttask.tictactoe.service;

import com.sbertesttask.tictactoe.entity.Game;
import com.sbertesttask.tictactoe.entity.Move;
import com.sbertesttask.tictactoe.repository.MoveRepository;
import com.sbertesttask.tictactoe.game_utils.Pos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MoveService implements MoveServiceInterface{

    private final MoveRepository moveRepository;


    public void createNewMove(Move move){
        moveRepository.save(move);
    }

    public void deleteMoveById(Long id) {
        moveRepository.deleteById(id);
    }

    public void deleteMoveByGameId(Long id){
        moveRepository.deleteByGameId(id);
    }

    public boolean existsMoveInGame(Long gameId, int row, int col){
        return moveRepository.existsByGameIdAndRowAndCol(gameId, row, col);
    }

    public List<Move> findMovesByGameIdOrderByTurn(Long gameId) {
        return moveRepository.findByGameIdOrderByTurn(gameId);
    }

    public List<Move> findMovesByGameIdAndActorOrderByTurn(Long gameId, boolean actor) {
        return moveRepository.findByGameIdAndActorOrderByTurn(gameId, actor);
    }

    public Optional<Move> findTopMoveByGameIdOrderByTurnDesc(Long gameId){
        return moveRepository.findTopByGameIdOrderByTurnDesc(gameId);
    }


    public List<Pos> movesToPosList(List<Move> moves) {
        List<Pos> posList = new ArrayList<>();
        for (Move move : moves) {
            posList.add(move.toPos());
        }
        return posList;
    }
    public boolean existByIdAndTurn(Long gameId, int turn){
        return moveRepository.existsByGameIdAndTurn(gameId,turn);
    }
    public Optional<Move> findByIdAndTurn(Long gameId, int turn){
        return moveRepository.findByGameIdAndTurn(gameId,turn);
    }

    public boolean undoLastTurn(Long gameId){
        List<Move> playerMoves = findMovesByGameIdAndActorOrderByTurn(gameId, true);
        List<Move> machineMoves = findMovesByGameIdAndActorOrderByTurn(gameId, false);
        if(playerMoves.isEmpty()){
            return false;
        }
        int sizePlayerMoves = playerMoves.size();
        int sizeMachineMoves = machineMoves.size();
        Move lastMove = playerMoves.get(sizePlayerMoves -1);
        int lastPlayerTurn = lastMove.getTurn();
        deleteMoveById(lastMove.getId());
        if (sizePlayerMoves > sizeMachineMoves){
            return true;
        } else if (findByIdAndTurn(gameId,lastPlayerTurn+1).isPresent()){
            deleteMoveById(findByIdAndTurn(gameId, lastPlayerTurn+1).get().getId());
        } else if (findByIdAndTurn(gameId,lastPlayerTurn-1).isPresent() &&
                findByIdAndTurn(gameId,lastPlayerTurn-1).get().getTurn() > 1){
            deleteMoveById(findByIdAndTurn(gameId, lastPlayerTurn+1).get().getId());
        }
        return true;
    }

    public void makeMove(Game game,boolean actor, Pos pos){
        Optional<Move> optionalMove = findTopMoveByGameIdOrderByTurnDesc(game.getId());
        int maxTurn;
        if (optionalMove.isPresent()){
            Move maxTurnMove = optionalMove.get();
            maxTurn = maxTurnMove.getTurn();
        } else maxTurn = 0;
        Move move = new Move();
        move.setGame(game);
        move.setActor(actor);
        move.setCol(pos.getCol());
        move.setRow(pos.getRow());
        move.setTurn(++maxTurn);
        createNewMove(move);
    }

}
