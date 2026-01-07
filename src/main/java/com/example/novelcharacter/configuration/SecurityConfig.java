package com.example.novelcharacter.configuration;

import com.example.novelcharacter.JWT.CustomLogoutFilter;
import com.example.novelcharacter.JWT.JWTFilter;
import com.example.novelcharacter.JWT.JWTUtil;
import com.example.novelcharacter.JWT.LoginFilter;
import com.example.novelcharacter.OAuth2.CustomSuccessHandler;
import com.example.novelcharacter.component.RateLimitFilter;
import com.example.novelcharacter.service.CustomOAuth2UserService;
import com.example.novelcharacter.service.RefreshService;
import com.example.novelcharacter.service.TokenProvider;
import com.example.novelcharacter.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final RefreshService refreshService;
    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final TokenProvider tokenProvider;


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        "/static/**",
                        "/favicon.ico",
                        "/book-open.svg",
                        "/manifest.json",
                        "/asset-manifest.json",
                        "/index.html",
                        "/*.js",
                        "/*.css",
                        "/*.png",
                        "/*.json"
                );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/actuator/**" ,"/page/**", "/oauth/callback", "/login",
                                "/api/**", "/reissue", "/post/**", "/userIdFind",
                                "/resetPassword", "/resetPassword/confirm"
                        ).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshService), LogoutFilter.class)
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshService, userService, tokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new JWTFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
