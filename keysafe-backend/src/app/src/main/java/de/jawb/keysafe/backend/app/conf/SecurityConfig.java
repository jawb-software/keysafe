package de.jawb.keysafe.backend.app.conf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LogManager.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.logout().disable()

        .authorizeRequests()
            .antMatchers(HttpMethod.POST, "/keysafes").permitAll()
            .antMatchers(HttpMethod.GET, "/status").permitAll()
            .antMatchers( "/swagger-ui/**").permitAll()
            .antMatchers( "/open-api/**").permitAll()
        .and()
        .authorizeRequests()
            .anyRequest().hasAnyRole("USER").and().httpBasic()
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

    @Bean
    public PasswordEncoder encoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}