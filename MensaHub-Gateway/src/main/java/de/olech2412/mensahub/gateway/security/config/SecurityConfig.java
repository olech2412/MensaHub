package de.olech2412.mensahub.gateway.security.config;

import de.olech2412.mensahub.gateway.security.JWTAuthenticationEntryPoint;
import de.olech2412.mensahub.gateway.security.JWTAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Configuration
@Component
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter { // Class for configuring Spring Security

    @Autowired
    private JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /**
     * Creates a JwtAuthenticationFilter bean
     *
     * @return JwtAuthenticationFilter
     */
    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter();
    }

    /**
     * Creates a PasswordEncoder bean
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates an AuthenticationManager bean
     *
     * @return AuthenticationManager
     * @throws Exception if an error occurs
     */
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Configures the HttpSecurity object
     *
     * @param httpSecurity HttpSecurity object
     * @throws Exception if an error occurs
     */
    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors()
                .and()
                .csrf()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) // Exception handling for unauthorized requests
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Session management for stateless requests (REST API)
                .and()
                .authorizeRequests()
                .antMatchers("/auth/**", "/actuator/**")
                .permitAll() // Allow access to the authentication endpoint
                .anyRequest()
                .authenticated(); // All other requests need to be authenticated

        httpSecurity.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

}
