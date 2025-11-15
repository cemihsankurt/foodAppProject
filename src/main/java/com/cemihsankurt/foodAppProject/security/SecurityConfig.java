package com.cemihsankurt.foodAppProject.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private final AuthenticationProvider authenticationProvider;


    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public static final String AUTHENTICATE = "/api/auth/authenticate";
    public static final String REGISTER_RESTAURANT = "/api/auth/register-restaurant";
    public static final String REGISTER_CUSTOMER = "/api/auth/register-customer";
    public static final String VERIFY_EMAIL = "/api/auth/verify";

    public SecurityConfig(AuthenticationProvider authenticationProvider,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(request ->
                    request.requestMatchers(AUTHENTICATE,REGISTER_CUSTOMER,REGISTER_RESTAURANT,VERIFY_EMAIL,"/error","/api/restaurants/**","/swagger-ui/**","/swagger-ui.html",
                                    "/v3/api-docs/**","/ws/**").
                            permitAll().
                            requestMatchers("/api/admin/**").hasRole("ADMIN").
                            requestMatchers("/api/restaurant-panel/**").hasRole("RESTAURANT").
                            requestMatchers("/api/customer/**","/api/orders/my-orders","/api/cart/**","/api/orders/create-from-cart","/api/orders/{orderId}/cancel").hasRole("CUSTOMER").
                            requestMatchers("/api/orders/**").hasAnyRole("CUSTOMER","RESTAURANT").
                            anyRequest().
                            authenticated())
                            .headers(headers ->
                                headers.frameOptions(frameOptions ->
                                frameOptions.sameOrigin() // Sadece 'localhost'tan gelen 'iframe'lere izin ver
                                                    )
                                    )
                            .sessionManagement(sessionManagement ->sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                            .authenticationProvider(authenticationProvider)
                            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);



        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Geliştirme için frontend'in adresine (Vite) izin ver:
        configuration.addAllowedOriginPattern("*"); // ★ BUNU EKLE (WS CORS'u düzeltir)
        configuration.addAllowedMethod("*");
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        // (Token'lı istekler için bu 'true' olabilir ama şimdilik '*' değilse kalsın)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Tüm yollar (/api/...) için bu ayarları uygula
        return source;
    }




}
