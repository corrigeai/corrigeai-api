package org.corrige.ai.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.corrige.ai.security.jwt.JwtAuthenticationEntryPoint;
import org.corrige.ai.security.jwt.JwtAuthenticationFilter;
import org.corrige.ai.util.ServerConstants;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserAuthenticationProvider authProvider;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthEndPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, ServerConstants.SERVER_REQUEST + ServerConstants.AUTHENTICATION_REQUEST + "/login").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "**").permitAll()
    			.antMatchers(HttpMethod.GET, "/v2/api-docs").permitAll()
    			.antMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll()
    			.antMatchers(HttpMethod.GET, "/swagger-resources/**").permitAll()
    			.antMatchers(HttpMethod.GET, "/webjars/**").permitAll()
                .antMatchers(HttpMethod.POST, ServerConstants.SERVER_REQUEST + ServerConstants.USER_REQUEST).permitAll()
                .antMatchers(ServerConstants.SERVER_REQUEST + ServerConstants.NOTIFICATION_REQUEST + "/**").permitAll()
                .antMatchers(ServerConstants.SERVER_REQUEST + ServerConstants.NOTIFICATION_REQUEST).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthEndPoint);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider);
    }

}
