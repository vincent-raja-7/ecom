package com.infinitetechies.ecom.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinitetechies.ecom.exception.JwtValidationException;
import com.infinitetechies.ecom.model.dto.response.ErrorResponse;
import com.infinitetechies.ecom.service.inf.IJwtService;
import com.infinitetechies.ecom.service.inf.IUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final IJwtService jwtService;
    private final ApplicationContext context;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/v1/auth/login") || path.equals("/api/v1/auth/register") || path.equals("/api/v2/auth/login") || path.equals("/api/v2/auth/refresh") ||
        path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs/**");
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;
        String path = request.getRequestURI();
        if (path.equals("/api/v1/auth/login") || path.equals("/api/v1/auth/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
            try{
                email = jwtService.extractUserEmail(token);
            } catch (JwtValidationException ex) {
                // Handle the exception directly in the filter
                handleJwtValidationError(response, ex);
                return; // Important: stop filter chain execution
            }
        }
        else{
            handleJwtValidationError(response, new JwtValidationException("JWT validation failed"));
            return;
        }

        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = context.getBean(IUserService.class).loadUserByUsername(email);
            log.info("Authenticated!");
            log.debug("Auth");
            if(jwtService.validateToken(token,userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            else{
                handleJwtValidationError(response, new JwtValidationException("JWT validation failed"));
                return;
            }
        }
        filterChain.doFilter(request,response);
    }


    private void handleJwtValidationError(HttpServletResponse response, JwtValidationException ex) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("JwtValidationException")
                .message(ex.getMessage())
                .build();

        // Convert to JSON and write to response
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}
