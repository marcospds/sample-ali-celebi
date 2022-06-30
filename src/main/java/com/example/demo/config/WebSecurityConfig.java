package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    private UserServiceImpl userServiceImpl;
//    private JwtTokenUtil jwtTokenUtil;
//    private AuthEntryPoint unauthorizedHandler;

//    public WebSecurityConfig(AuthEntryPoint unauthorizedHandler, JwtTokenUtil jwtTokenUtil) {
//        this.unauthorizedHandler = unauthorizedHandler;
//        this.jwtTokenUtil = jwtTokenUtil;
//    }

//    @Override
//    @Bean
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }

//    @Autowired
//    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userServiceImpl)
//                .passwordEncoder(encoder());
//    }

//    @Bean
//    public AuthenticationFilter authenticationTokenFilterBean() throws Exception {
//        return new AuthenticationFilter(userServiceImpl, jwtTokenUtil);
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(
                        "/auth/*",
                        "/token/*",
                        "/webjars/**",
                        "/",
                        "/uploads/**",
                        "favicon.ico"
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
//                .authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

//        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}
