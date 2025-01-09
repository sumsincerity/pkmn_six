package ru.pchelintsev.pkmn.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.pchelintsev.pkmn.dtos.User;
import ru.pchelintsev.pkmn.services.JwtService;

import java.util.Base64;
import java.util.stream.Collectors;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class LoginController {

    private final JwtService jwtService;

    @PostMapping("/success")
    public String successPage(HttpServletResponse response,
                              Authentication authentication) {
        String token = jwtService.createToken(authentication.getName(), authentication.getAuthorities()
                .stream().collect(Collectors.toUnmodifiableList()));
        response.addCookie(new Cookie("JWT", Base64.getEncoder().encodeToString(token.getBytes())));
        return "login proshel";
    }

    @GetMapping("/")
    public String home(Authentication authentication) {
        if(authentication.isAuthenticated()) {
            return authentication.getName();
        } else {
            return "Not authenticated";
        }
    }

    @PostMapping("/registration")
    public String registration(@RequestBody User user) {
        if(user.getPassword() != null && user.getUsername() != null) {
            jwtService.registerUser(user.getUsername(), user.getPassword());
            return "201. Registration successful.";
        } else {
            return "400";
        }
    }
}