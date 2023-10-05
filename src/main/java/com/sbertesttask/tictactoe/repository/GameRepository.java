package com.sbertesttask.tictactoe.repository;

import com.sbertesttask.tictactoe.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByModifiedDateBefore(Date date);
}
