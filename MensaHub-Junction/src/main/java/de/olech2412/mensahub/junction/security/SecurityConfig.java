package de.olech2412.mensahub.junction.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import de.olech2412.mensahub.junction.gui.views.LoginView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import javax.sql.DataSource;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Autowired
    private DataSource dataSource;

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        // Authentifizierung für die Benutzer der Webanwendung (aus Datenbank)
        auth.jdbcAuthentication().passwordEncoder(passwordEncoder())
                .dataSource(dataSource)
                .usersByUsernameQuery("select username, password, verified_email from api_user where username=?")
                .authoritiesByUsernameQuery("select username, role from api_user where username=?");

        auth.jdbcAuthentication().passwordEncoder(passwordEncoder())
                .dataSource(dataSource)
                .usersByUsernameQuery("select username, password, enabled from users where username=?")
                .authoritiesByUsernameQuery("select username, role from users where username=?");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
            authorizationManagerRequestMatcherRegistry.requestMatchers("/images/**").permitAll();
            authorizationManagerRequestMatcherRegistry.requestMatchers("/manifest.webmanifest").permitAll();
            authorizationManagerRequestMatcherRegistry.requestMatchers("/icons/**").permitAll();
            authorizationManagerRequestMatcherRegistry.requestMatchers("/login").permitAll();
            authorizationManagerRequestMatcherRegistry.requestMatchers("/sw.js").permitAll();
        });

        super.configure(http);

        setLoginView(http, LoginView.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(
                "/icons/**",
                "/images/**",
                "/manifest.webmanifest",
                "/static/**",
                "/public/**",
                "/resources/**",
                "/META-INF/**",
                "/sw.js"
        );
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager();
    }

    private BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}