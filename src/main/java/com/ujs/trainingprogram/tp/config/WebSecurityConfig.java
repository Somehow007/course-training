package com.ujs.trainingprogram.tp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ujs.trainingprogram.tp.common.filter.MyAuthenticationFilter;
import com.ujs.trainingprogram.tp.common.filter.ValidateCodeFilter;
import com.ujs.trainingprogram.tp.common.handler.MyAuthenticationFailuerHandler;
import com.ujs.trainingprogram.tp.common.handler.MyAuthenticationSuccessHandler;
import com.ujs.trainingprogram.tp.common.result.Result;
import com.ujs.trainingprogram.tp.common.web.Results;
import com.ujs.trainingprogram.tp.security.CustomUserDetailsService;
//import com.ujs.trainingprogram.tp.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.PrintWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(customUserDetailsService)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .disable())
                .logout(logout -> logout
                        .permitAll())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

}
//    @Autowired
//    @Lazy
//    private UserServiceImpl userService;
//    @Autowired
//    private MyAuthenticationSuccessHandler successHandler;
//    @Autowired
//    private MyAuthenticationFailuerHandler failureHandler;
//    @Autowired
//    private AuthenticationConfiguration authenticationConfiguration;
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setPasswordEncoder(new BCryptPasswordEncoder());
//        provider.setUserDetailsService(userService);
//        auth.authenticationProvider(provider);
//    }

//    // todo: 鉴权部分仍需处理，有很大bug，目前开发取消鉴权
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        // 1. 初始化过滤器
//        ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
//        validateCodeFilter.setMyAuthenticationFailuerHandler(failureHandler);
//
//        MyAuthenticationFilter authFilter = new MyAuthenticationFilter(authenticationManager());
//        authFilter.setFilterProcessesUrl("/loginByAccount");
//        authFilter.setAuthenticationSuccessHandler(successHandler);
//        authFilter.setAuthenticationFailureHandler(failureHandler);
//
//        // 2. 配置安全规则
//        return http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/", "/test", "/code/image").permitAll()
//                        .requestMatchers("/excel/users").permitAll()
//                        .requestMatchers("/user/**").hasAuthority("user")
//                        .requestMatchers("/doc.html", "/webjars/**", "/swagger-resources/**", "/v3/api-docs/**").permitAll()
//                        .requestMatchers("/college/**").permitAll()
//                        .requestMatchers("/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
//                        .sessionFixation().migrateSession())
//                .securityContext(securityContext -> securityContext
//                        .requireExplicitSave(false))
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .csrf(csrf -> csrf.disable())
//                .authenticationProvider(daoAuthenticationProvider())
//                .addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
//                .addFilterAt(authFilter, UsernamePasswordAuthenticationFilter.class)
//                .build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager() throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//    // 跨域配置
//    private UrlBasedCorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.addAllowedOrigin("http://localhost:8090");
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//        config.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }
//
//    @Bean
//    public DaoAuthenticationProvider daoAuthenticationProvider() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userService);
//        provider.setPasswordEncoder(passwordEncoder());
//        return provider;
//    }
//}
