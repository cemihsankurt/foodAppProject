package com.cemihsankurt.foodAppProject.security;

import com.cemihsankurt.foodAppProject.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtTokenProvider jwtTokenProvider;


    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                   CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header;
        String token;
        String email;

        header = request.getHeader("Authorization");

        if(header == null){
            filterChain.doFilter(request,response);
            return;
        }

        token = header.substring(7);
        try {
            email = jwtTokenProvider.getEmailFromToken(token);
            if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                if(userDetails != null && jwtTokenProvider.isTokenValid(token)) {
                    UsernamePasswordAuthenticationToken jwtAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
                }
            }
        } catch (Exception e) {
            // Log the exception (you can use a logging framework here)
            System.err.println("JWT processing failed: " + e.getMessage());
        } finally {
            filterChain.doFilter(request, response);
        }
    }
}
