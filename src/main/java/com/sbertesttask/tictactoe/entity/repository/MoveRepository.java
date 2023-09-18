package com.sbertesttask.tictactoe.entity.repository;

import com.sbertesttask.tictactoe.entity.Move;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MoveRepository extends JpaRepository<Move, Long> {


    List<Move> findByGameIdOrderByTurn(Long gameId);
    List<Move> findByGameIdAndActorOrderByTurn(Long gameId, boolean actor);
    Optional<Move> findTopByGameIdOrderByTurnDesc(Long gameId);
    Optional<Move> findByGameIdAndTurn(Long gameId, int turn);
    void deleteById(Long id);

    boolean existsByGameIdAndRowAndCol(Long gameId, int row, int col);

    boolean existsByGameIdAndTurn(Long gameId, int turn);
}
