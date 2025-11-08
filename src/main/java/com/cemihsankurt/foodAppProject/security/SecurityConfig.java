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
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(request ->
                    request.requestMatchers(AUTHENTICATE,REGISTER_CUSTOMER,REGISTER_RESTAURANT,VERIFY_EMAIL,"/error","/api/restaurants/**").
                            permitAll().
                            requestMatchers("/api/admin/**").hasRole("ADMIN").
                            requestMatchers("/api/restaurant-panel/**").hasRole("RESTAURANT").
                            requestMatchers("/api/customer/**","/api/orders/my-orders","/api/cart/**","/api/orders/create").hasRole("CUSTOMER").
                            requestMatchers("/api/orders/**").hasAnyRole("CUSTOMER","RESTAURANT").
                            anyRequest().
                            authenticated())
                    .sessionManagement(sessionManagement ->sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authenticationProvider(authenticationProvider)
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }




}
