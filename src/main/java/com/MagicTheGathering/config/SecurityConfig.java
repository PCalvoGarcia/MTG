package com.MagicTheGathering.config;

import com.MagicTheGathering.auth.AuthServiceHelper;
import com.MagicTheGathering.auth.filter.JwtAuthenticationFilter;
import com.MagicTheGathering.auth.filter.JwtValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {
    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    private AuthServiceHelper authServiceHelper;

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//                      CONNECT WITH FRONT
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("https://3596eafc18b0.ngrok-free.app")
//                        .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
//                        .allowedHeaders("*");
//            }
//        };
//    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
//                      CONNECT WITH NGROK TUNNEL
//                .cors(cors -> cors.configurationSource(request -> {
//                    CorsConfiguration config = new CorsConfiguration();
//                    config.setAllowCredentials(true);
//                    config.setAllowedOrigins(List.of("https://53a0871d3114.ngrok-free.app"));
//                    config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
//                    config.setAllowedHeaders(List.of("*"));
//                    return config;
//                }))
                .authorizeHttpRequests((authHttp) -> authHttp
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/cards/**", "/api/decks/**", "/api/deck-cards")
                        .hasRole("USER")
                        .requestMatchers(HttpMethod.POST,"/api/cards/**", "/api/decks/**", "/api/deck-cards")
                        .hasRole("USER")
                        .requestMatchers(HttpMethod.PUT,"/api/cards/**", "/api/decks/**", "/api/deck-cards")
                        .hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE,"/api/cards/**", "/api/decks/**", "/api/deck-cards")
                        .hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/users", "/api/users/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/register/admin")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**")
                        .hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), authServiceHelper))
                .addFilter(new JwtValidationFilter(authenticationManager(), authServiceHelper))
                .csrf(config -> config.disable())
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}

