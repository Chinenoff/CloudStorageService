package com.example.cloudstorageservice.config;

import com.example.cloudstorageservice.security.CustomUserDetailsService;
import com.example.cloudstorageservice.security.filters.SecurityExceptionHandlerFilter;
import com.example.cloudstorageservice.security.filters.TokenAuthenticationFilter;
import com.example.cloudstorageservice.security.jwt.JwtAuthServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthServiceImpl jwtAuthService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .httpBasic().disable()
                .logout().disable()
                .addFilterBefore(cloudSecurityHandler(), LogoutFilter.class)
                .addFilterBefore(cloudTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests().antMatchers("/login").permitAll()
                .and()
                .authorizeRequests().antMatchers("/logout").permitAll()
                .and()
                .authorizeRequests().anyRequest().authenticated();
    }

    @Bean
    public TokenAuthenticationFilter cloudTokenFilter() {
        return new TokenAuthenticationFilter(customUserDetailsService, jwtAuthService);
    }

    @Bean
    public SecurityExceptionHandlerFilter cloudSecurityHandler() {
        return new SecurityExceptionHandlerFilter();
    }

    @Bean
    public FilterRegistrationBean<TokenAuthenticationFilter> myFilterRegistrationBean(TokenAuthenticationFilter filter) {
        FilterRegistrationBean<TokenAuthenticationFilter> frb = new FilterRegistrationBean<>(filter, new ServletRegistrationBean[0]);
        frb.setEnabled(false);
        return frb;
    }

}
