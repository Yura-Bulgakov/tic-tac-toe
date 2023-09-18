package com.sbertesttask.tictactoe.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.*;


@Component
@Slf4j
public class JwtTokenUtils {

   @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Duration jwtLifetime;

    public String generateToken(UserDetails userDetails){
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + jwtLifetime.toMillis());
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String getUsernameFromRequest(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authHeader!=null && authHeader.startsWith("Bearer ")){
            jwt = authHeader.substring(7);
            try {
                username = getUsername(jwt);
            } catch (ExpiredJwtException e){
                log.debug("Время жизни токена вышло");
            } catch (SignatureException e){
                log.debug("Подпись неправильная");
            }
        }
        return username;
    }

    public String getUsername(String token){
        return getAllClaimsFromToken(token).getSubject();
    }

    private Claims getAllClaimsFromToken(String token){
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

}
