package com.example.cloudstorageservice.security.jwt;

import com.example.cloudstorageservice.model.db.UserJwtEntity;
import com.example.cloudstorageservice.model.exception.InvalidJwtException;
import com.example.cloudstorageservice.repository.JwtSecurityRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class JwtAuthServiceImpl implements JwtAuthService {
    private final UserDetailsService userDetailsService;
    private final JwtSecurityRepository jwtSecurityRepository;

    @Override
    public String generateToken(String secret) {
        String id = UUID.randomUUID().toString().replace("-", "");
        Date now = new Date();
        Date exp = Date.from(LocalDateTime.now().plusMinutes(30)
                .atZone(ZoneId.systemDefault()).toInstant());

        String token = "";
        try {
            token = Jwts.builder()
                    .setId(id)
                    .setIssuedAt(now)
                    .setNotBefore(now)
                    .setExpiration(exp)
                    .signWith(SignatureAlgorithm.HS256, secret)
                    .compact();
        } catch (JwtException e) {
            e.printStackTrace();
        }
        return token;
    }

    @Override
    public String saveTokenToDataBase(String token, String username) {
        jwtSecurityRepository.save(new UserJwtEntity(token, username));
        log.info("The token for the user {} was generated and successfully saved to the database", username);
        return token;
    }

    @Override
    public boolean validateToken(String token, String username) {

        UserDetails user = userDetailsService.loadUserByUsername(username);
        try {
            Jwts.parser().setSigningKey(user.getPassword() + user.getUsername()).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            jwtSecurityRepository.deleteById(token);
            throw new InvalidJwtException("Token expired");
        } catch (Exception e) {
            jwtSecurityRepository.deleteById(token);
            throw new InvalidJwtException("Invalid token");
        }
    }

    @Override
    public HttpStatus deleteToken(String token) {
        jwtSecurityRepository.deleteById(token);
        log.info("The token was successfully removed from the database");
        return HttpStatus.OK;
    }

    @Override
    public UserJwtEntity getUserByToken(String token) {
        return jwtSecurityRepository.findDistinctByJwtToken(token)
                .orElseThrow(() -> new InvalidJwtException("User haven't jwt-token"));
    }
}
