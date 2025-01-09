package ru.pchelintsev.pkmn.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;
import ru.pchelintsev.pkmn.dtos.User;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${token.jwt_key:defaultJwtKey04361sasdsadaswerdxpr}")
    private String JWT_SECRET;

    private static final long EXPIRATION_TIME_MILLIS = 1800000; // 30 minutes
    private final JdbcUserDetailsManager jdbcUserDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public UserDetails verifyTokenAndGetUser(String token) {
        try {
            Claims claims;
            claims = Jwts.parserBuilder()
                    .setSigningKey(JWT_SECRET.getBytes())
                    .build().parseClaimsJws(token).getBody();
            return jdbcUserDetailsManager.loadUserByUsername(claims.getSubject());
        } catch(Exception e) {
            throw new RuntimeException("Неверный JWT", e);
        }
    }

    public String createToken(String username, List<GrantedAuthority> authorities) {
        String authoritiesString = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        SecretKey key;
        key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", authoritiesString)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MILLIS))
                .signWith(key)
                .compact();
    }

    public void registerUser(String username, String password) {
        String hashedPassword = passwordEncoder.encode(password);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        jdbcUserDetailsManager.createUser(new User(username,
                hashedPassword, true, authorities));
    }
}