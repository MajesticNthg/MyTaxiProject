package com.taxiproject.mytaxi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String requestPath = request.getServletPath();
        final String method = request.getMethod();

        System.out.println("=== JWT Filter START ===");
        System.out.println("Path: " + requestPath);
        System.out.println("Method: " + method);
        System.out.println("Auth Header: " + authHeader);


        if (!requestPath.startsWith("/api/")) {
            System.out.println("⏭️ Skipping non-API path");
            chain.doFilter(request, response);
            return;
        }

        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            System.out.println("JWT extracted: " + jwt.substring(0, Math.min(jwt.length(), 30)) + "...");

            try {
                username = jwtUtil.extractUsername(jwt);
                System.out.println("✅ Extracted username: " + username);
            } catch (Exception e) {
                System.out.println("❌ Error extracting username: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("❌ No Bearer token found");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("👤 Loading UserDetails for: " + username);

            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                System.out.println("✅ UserDetails loaded: " + userDetails.getUsername());

                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    System.out.println("✅ Token VALID");
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.out.println("❌ Token INVALID");
                }
            } catch (Exception e) {
                System.out.println("❌ Error loading UserDetails: " + e.getMessage());
            }
        }
        System.out.println("=== JWT FILTER END ===");
        System.out.println("========================================\n");
        chain.doFilter(request, response);
    }
}
