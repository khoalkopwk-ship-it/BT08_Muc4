package vn.iotstar.config;

import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import vn.iotstar.repository.UserRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
// Cấu hình bảo mật cho ứng dụng
// Cấu hình để xác thực người dùng và phân quyền truy cập dựa trên vai trò
// Cấu hình để mã hóa mật khẩu người dùng bằng BCrypt
// Cấu hình để xác thực người dùng dựa trên tên đăng nhập hoặc email
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository userRepository) {
        return login -> userRepository
                .findFirstByUsernameIgnoreCaseOrEmailIgnoreCase(login, login)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole())
                        .disabled(!user.isEnabled())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản"));
    }

        @Bean
        SecurityFilterChain securityFilterChain(
                HttpSecurity http) throws Exception {

        return http
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(
                                DispatcherType.FORWARD,
                                DispatcherType.ERROR
                        ).permitAll()

                        .requestMatchers(
                                "/",
                                "/home",
                                "/login",
                                "/register",
                                "/verify-otp",
                                "/forgot-password/**",
                                "/reset-password",
                                "/product/**",
                                "/category",
                                "/assets/**",
                                "/images/**",
                                "/css/**",
                                "/js/**",
                                "/uploads/**",
                                "/error"
                        ).permitAll()

                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                "/api/csrf",
                                "/api/categories/**",
                                "/api/products/**"
                        ).hasRole("ADMIN")

                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/profile")
                        .authenticated()

                        .anyRequest()
                        .authenticated()
                )

                .csrf(csrf -> csrf
                        .csrfTokenRepository(
                                CookieCsrfTokenRepository
                                        .withHttpOnlyFalse()
                        )
                )

                .exceptionHandling(exception -> exception
                        .defaultAuthenticationEntryPointFor(
                                (request, response, authException) ->
                                        writeApiError(
                                                response,
                                                HttpStatus.UNAUTHORIZED,
                                                "Bạn chưa đăng nhập"
                                        ),
                                PathPatternRequestMatcher
                                        .pathPattern("/api/**")
                        )
                        .defaultAccessDeniedHandlerFor(
                                (request, response, accessDeniedException) ->
                                        writeApiError(
                                                response,
                                                HttpStatus.FORBIDDEN,
                                                "Bạn không có quyền thực hiện thao tác này"
                                        ),
                                PathPatternRequestMatcher
                                        .pathPattern("/api/**")
                        )
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("username")
                        .successHandler(
                                (request, response, authentication) -> {
                                        boolean admin =
                                                authentication
                                                        .getAuthorities()
                                                        .stream()
                                                        .anyMatch(authority ->
                                                                "ROLE_ADMIN"
                                                                        .equals(
                                                                                authority
                                                                                        .getAuthority()
                                                                        )
                                                        );

                                        response.sendRedirect(
                                                request.getContextPath()
                                                        + (
                                                        admin
                                                                ? "/admin/categories"
                                                                : "/home"
                                                )
                                        );
                                }
                        )
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/home?logout")
                )

                .build();
        }
    private static void writeApiError(
            HttpServletResponse response,
            HttpStatus status,
            String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
                "{\"success\":false,\"message\":\"" + message
                        + "\",\"data\":null,\"errors\":null}");
    }
}
