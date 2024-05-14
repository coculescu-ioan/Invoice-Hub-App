package com.example.backend.security.filters;

import com.example.backend.utilities.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.Objects;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private JWTUtils jwtUtils;

    public JWTAuthorizationFilter(JWTUtils jwtUtils, AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if(Objects.isNull(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);

    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if(Objects.nonNull(header)) {
            String username = jwtUtils.extractUsername(header.replace("Bearer ", ""));
            if(Objects.nonNull(username)) {
                return new UsernamePasswordAuthenticationToken(username, null);
            }
            return null;
        }
        return null;
    }
}
