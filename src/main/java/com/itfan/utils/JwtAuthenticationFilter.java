package com.itfan.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization!=null && authorization.startsWith("Bearer")){
            String substring = authorization.substring(7);
            try {
                Claims claims = jwtUtils.parseToken(substring);

                String username = claims.getSubject();

                String role = claims.get("role", String.class);

                ArrayList<GrantedAuthority> authorities = new ArrayList<>();

                if (role!=null){
                    authorities.add(new SimpleGrantedAuthority("ROLE_"+role));
                }

                UsernamePasswordAuthenticationToken authenticationToken=
                        new UsernamePasswordAuthenticationToken(username,null,authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }catch (JwtException e){
                logger.warn("JWT 解析失败:"+e.getMessage());
            }
        }
        filterChain.doFilter(request,response);
    }
}
