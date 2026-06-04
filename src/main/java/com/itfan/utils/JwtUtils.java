package com.itfan.utils;

import com.itfan.pojo.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
@Component
public class JwtUtils {
    public final Key key; //密钥

    public JwtUtils(JwtConfig jwtConfig){
        this.key=Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtConfig.getSecret()));
    }

//    private static final Key key= Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public static final long EXPIRATION_TIME = 12 * 60 * 60 * 1000;
    /**
     * 生成jwt令牌
     */
    public String genertoToken(Map<String,Object> claims){
        return Jwts.builder()
                .setClaims(claims)
                .signWith(key)
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .compact();
    }

    public Claims parseToken(String token){
        Claims jwts=Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return jwts;
    }
}

