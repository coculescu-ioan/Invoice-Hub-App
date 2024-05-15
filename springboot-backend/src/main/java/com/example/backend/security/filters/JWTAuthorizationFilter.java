package com.example.backend.security.filters;

import com.example.backend.utilities.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private JWTUtils jwtUtils;

    private UserDetailsService userDetailsService;

    public JWTAuthorizationFilter(JWTUtils jwtUtils,
                                  AuthenticationManager authenticationManager,
                                  UserDetailsService userDetailsService) {
        super(authenticationManager);
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
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
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                return new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
            }
            return null;
        }
        return null;
    }
}
