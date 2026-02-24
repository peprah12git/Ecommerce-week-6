package com.smartcommerce.filter;

import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.smartcommerce.security.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements Filter {

    private final JwtUtil jwtUtil;

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/admin/login",
            "/api/products",
            "/api/categories",
            "/graphql",
            "/swagger-ui",
            "/v3/api-docs",
            "/graphiql"
    );

    private static final Set<String> PUBLIC_QUERIES = Set.of("categories", "category");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI();

        // Set CORS headers - dynamically handle origin
        String origin = req.getHeader("Origin");
        Set<String> allowedOrigins = Set.of(
            "http://localhost:3000",
            "http://localhost:5173",
            "http://127.0.0.1:3000",
            "http://127.0.0.1:5173",
            "http://localhost:3001"
        );
        
        if (origin != null && allowedOrigins.contains(origin)) {
            res.setHeader("Access-Control-Allow-Origin", origin);
        } else if (origin == null) {
            res.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        }
        
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Max-Age", "3600");

        // Allow OPTIONS requests (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Allow public paths
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        // Check GraphQL public queries
        if (path.equals("/graphql")) {
            String query = req.getParameter("query");
            if (query != null && PUBLIC_QUERIES.stream().anyMatch(query::contains)) {
                log.debug("Public GraphQL query allowed");
                chain.doFilter(request, response);
                return;
            }
        }

        // Validate JWT
        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing Authorization header: {}", path);
            unauthorized(res, "Missing or malformed Authorization header");
            return;
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = jwtUtil.getClaims(token);
            req.setAttribute("userId", Integer.parseInt(claims.getSubject()));
            req.setAttribute("userRole", claims.get("role", String.class));
            log.debug("Authenticated user: {} role: {}", claims.getSubject(), claims.get("role"));
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            unauthorized(res, "Invalid or expired token");
        }
    }

    private void unauthorized(HttpServletResponse res, String msg) throws IOException {
        res.setStatus(401);
        res.setContentType("application/json");
        res.getWriter().write("{\"error\":\"" + msg + "\"}");
    }
}
