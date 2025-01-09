package ru.pchelintsev.pkmn.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.pchelintsev.pkmn.filters.JwtAuthenticationFilter;
import ru.pchelintsev.pkmn.services.JwtService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JdbcUserDetailsManager jdbcUserDetailsManager;
    private final JwtService jwtService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                customizer ->
                        customizer
                                .requestMatchers(HttpMethod.GET, "/api/v1/card").permitAll()
                                .requestMatchers("/api/v1/card/{name}").permitAll()
                                .requestMatchers("/api/v1/card/owner").permitAll()
                                .requestMatchers( HttpMethod.POST, "/api/v1/card").hasRole("ADMIN")
                                .requestMatchers( HttpMethod.POST, "/api/v1/student").hasRole("ADMIN")
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/registration").permitAll()
                                .anyRequest().authenticated()
        );
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.formLogin(form -> form.successForwardUrl("/success"));

        http.csrf(AbstractHttpConfigurer::disable);
        http.addFilterBefore(new JwtAuthenticationFilter(jwtService),
                UsernamePasswordAuthenticationFilter.class);
        http.userDetailsService(jdbcUserDetailsManager);
        return http.build();
    }
}