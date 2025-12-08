package com.common.config;

import com.common.filter.JwtAuthenticationTokenFilter;
import com.common.handler.AuthenticationEntryPointImpl;
import com.common.handler.LogoutSuccessHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * JWT 认证过滤器
     */
    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    /**
     * 认证失败处理器
     */
    private final AuthenticationEntryPointImpl authenticationEntryPoint;

    /**
     * 登出成功处理器
     */
    private final LogoutSuccessHandlerImpl logoutSuccessHandler;

    public SecurityConfig(JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter,
                         AuthenticationEntryPointImpl authenticationEntryPoint,
                         LogoutSuccessHandlerImpl logoutSuccessHandler) {
        this.jwtAuthenticationTokenFilter = jwtAuthenticationTokenFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.logoutSuccessHandler = logoutSuccessHandler;
    }

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 安全过滤链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 关闭 CSRF
            .csrf(csrf -> csrf.disable())
            // 开启跨域
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 不创建会话
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置请求授权
            .authorizeHttpRequests(authz -> authz
                // 放行静态资源
                .requestMatchers("/static/**", "/favicon.ico").permitAll()
                // 放行登录接口
                .requestMatchers("/auth/login", "/auth/logout").permitAll()
                // 放行测试接口
                .requestMatchers("/test/**").permitAll()
                // 放行预检请求
                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            // 配置异常处理器
            .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
            // 配置登出
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessHandler(logoutSuccessHandler)
            )
            // 添加 JWT 过滤器
            .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 跨域配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
