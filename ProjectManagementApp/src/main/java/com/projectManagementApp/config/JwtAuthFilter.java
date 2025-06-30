package com.projectManagementApp.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.projectManagementApp.security.service.CustomUserDetailService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Skip JWT authentication for public endpoints
        String requestPath = request.getRequestURI();
        if (shouldSkipAuthentication(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = null;
        String jwtToken = null;
        String header = request.getHeader("Authorization");
        
        try {
            // FIXED: Proper condition check and Bearer format
            if (header != null && header.startsWith("Bearer ")) {  // Fixed: "Bearer " not "Bearer: "
                jwtToken = header.substring(7);  // This line was outside the if block before
                username = jwtHelper.getUsernameFromToken(jwtToken);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = customUserDetailService.loadUserByUsername(username);
                    
                    // FIXED: Pass token to validateToken method if it requires it
                    if (jwtHelper.validateToken(jwtToken)) {  // Added userDetails parameter
                        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(token);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Check if the request path should skip JWT authentication
     */
    private boolean shouldSkipAuthentication(String requestPath) {
        // List of endpoints that don't require authentication
        String[] publicEndpoints = {
            "/auth/signup",
            "/auth/login", 
            "/signup",
            "/login",
            "/api/auth/signup",
            "/api/auth/login",
            "/public/**",
            "/actuator/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/error"
        };

        for (String endpoint : publicEndpoints) {
            if (endpoint.endsWith("**")) {
                // Handle wildcard patterns
                String basePattern = endpoint.substring(0, endpoint.length() - 2);
                if (requestPath.startsWith(basePattern)) {
                    return true;
                }
            } else if (requestPath.equals(endpoint)) {
                return true;
            }
        }
        
        return false;
    }
}