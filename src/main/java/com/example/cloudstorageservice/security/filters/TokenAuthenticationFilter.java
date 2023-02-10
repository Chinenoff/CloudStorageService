package com.example.cloudstorageservice.security.filters;

import com.example.cloudstorageservice.model.db.UserJwtEntity;
import com.example.cloudstorageservice.security.CustomUserDetailsService;
import com.example.cloudstorageservice.security.jwt.JwtAuthServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@AllArgsConstructor
@Slf4j
public class TokenAuthenticationFilter extends GenericFilterBean {
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthServiceImpl jwtAuthService;
    public static final String JWT_REQUEST_HEADER = "auth-token";

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        final String token = httpRequest.getHeader(JWT_REQUEST_HEADER);
        if (token != null) {
            log.info("Checking the presence of a token in the database and its validity");
            UserJwtEntity userJwt = jwtAuthService.getUserByToken(token.substring(7));
            if (jwtAuthService.validateToken(userJwt.getJwtToken(), userJwt.getUsername())) {
                UserDetails currentUser = userDetailsService.loadUserByUsername(userJwt.getUsername());
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}
