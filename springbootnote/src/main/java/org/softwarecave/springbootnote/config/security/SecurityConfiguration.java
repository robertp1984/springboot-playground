package org.softwarecave.springbootnote.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class SecurityConfiguration {

    public static final String ACTUATOR_VIEWER = "ACTUATOR_VIEWER";
    public static final String STICKY_NOTES_VIEWER = "STICKY_NOTES_VIEWER";
    public static final String STICKY_NOTES_ADMIN = "STICKY_NOTES_ADMIN";
    public static final String STICKY_NOTES_MANAGER = "STICKY_NOTES_MANAGER";

    public static final String API_V1_STICKY_NOTES_ALL = "/api/v1/stickyNotes/**";
    public static final String API_V1_STICKY_NOTES_BASE = "/api/v1/stickyNotes";
    public static final String API_V1_HELLO_BASE = "/api/v1/hello";

    @Bean
    public SecurityFilterChain createSecurityFilterChain(HttpSecurity http) {
        http
                .authorizeHttpRequests(configurer -> authorizeRequestsConfig(configurer))
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    private AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry authorizeRequestsConfig(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry configurer) {
        return configurer
                // actuator endpoints
                .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/actuator", "/actuator/**").hasRole(ACTUATOR_VIEWER)
                // application endpoints
                .requestMatchers(API_V1_HELLO_BASE).anonymous()
                .requestMatchers(HttpMethod.GET, API_V1_STICKY_NOTES_BASE, API_V1_STICKY_NOTES_ALL).hasRole(STICKY_NOTES_VIEWER)
                .requestMatchers(HttpMethod.POST, API_V1_STICKY_NOTES_BASE).hasRole(STICKY_NOTES_MANAGER)
                .requestMatchers(HttpMethod.PUT, API_V1_STICKY_NOTES_ALL).hasRole(STICKY_NOTES_MANAGER)
                .requestMatchers(HttpMethod.DELETE, API_V1_STICKY_NOTES_ALL).hasRole(STICKY_NOTES_ADMIN)
                .requestMatchers(HttpMethod.PATCH, API_V1_STICKY_NOTES_ALL).hasRole(STICKY_NOTES_MANAGER)
                // deny all other requests
                .anyRequest().denyAll();
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setUsersByUsernameQuery("select username, password, enabled from users where username = ?");
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select u.username, r.rolename from users u join user_roles ur on u.id=ur.user_id join roles r on ur.role_id=r.id where username = ?");
        return jdbcUserDetailsManager;
    }

}
