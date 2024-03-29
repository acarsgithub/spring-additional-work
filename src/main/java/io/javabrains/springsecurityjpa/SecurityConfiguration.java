package io.javabrains.springsecurityjpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // ordering the functions within the chain from least to most secure will lead to vulnerabilities
        // having ("/**) being available to everyone first will nullify any authorizations afterwards
        http.authorizeRequests()
                .antMatchers("/admin-only").hasAnyRole("ADMIN", "USER")
                .antMatchers("/csrf").hasAnyRole("ADMIN", "USER")
                .antMatchers("/transaction").hasAnyRole("ADMIN", "USER")
                .antMatchers("xss-concat").permitAll()
                .antMatchers("/xss-thyme").permitAll()
                .antMatchers("/").permitAll()
                .and().formLogin();

        // disable CSRF to cause vulnerability
        http.csrf().disable();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
