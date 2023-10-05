package com.sbertesttask.tictactoe.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthInterface extends UserDetailsService {
    public boolean registerUser(String username, String password, String confirmPassword);
    public String authUser(String username);

    public boolean doesUserExist(String login);

}
