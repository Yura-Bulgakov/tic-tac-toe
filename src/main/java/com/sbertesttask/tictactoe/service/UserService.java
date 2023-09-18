package com.sbertesttask.tictactoe.service;

import com.sbertesttask.tictactoe.dtos.RegistrationUserDTO;
import com.sbertesttask.tictactoe.entity.User;
import com.sbertesttask.tictactoe.entity.repository.UserRepository;
import com.sbertesttask.tictactoe.security.JwtTokenUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;


    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public boolean registerUser(RegistrationUserDTO createUserRequest){
        if(!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
            return false;
        }
        User newUser = new User();
        newUser.setUsername(createUserRequest.getLogin());
        String encodedPassword = passwordEncoder.encode(createUserRequest.getPassword());
        newUser.setPassword(encodedPassword);
        createNewUser(newUser);
        return true;
    }

    public User getUserFromJwt(HttpServletRequest request){
        String username = jwtTokenUtils.getUsernameFromRequest(request);
        Optional<User> opUser = findByUsername(username);
        return opUser.orElseThrow(() -> new RuntimeException("Пользователь отсутствует"));
    }

    public void createNewUser(User user){
        userRepository.save(user);
    }

    public boolean doesUserExist(String login){
        return userRepository.existsByUsername(login);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", username)
        ));
        List<GrantedAuthority> authorities = Collections.emptyList();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
