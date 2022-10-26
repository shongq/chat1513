package com.example.prj1513.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .csrf().disable()
                .headers()
                    .frameOptions().sameOrigin()
                .and()
                    .formLogin()
                .and()
                    .authorizeRequests()
                        .antMatchers("/chat/**").hasRole("USER")
                        .anyRequest().permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.inMemoryAuthentication()
                .withUser("shk0716")
                .password(passwordEncoder().encode("123"))
                .roles("USER")
                .and()
                .withUser("varohso")
                .password(passwordEncoder().encode("123"))
                .roles("USER")
                .and()
                .withUser("guest")
                .password(passwordEncoder().encode("123"))
                .roles("GUEST");
    }
}
