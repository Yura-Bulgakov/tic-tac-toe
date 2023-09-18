package com.sbertesttask.tictactoe.entity.repository;

import com.sbertesttask.tictactoe.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    boolean existsByUsername(String login);

    Optional<User> findByUsername(String username);
}
