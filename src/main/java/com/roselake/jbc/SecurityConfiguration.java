package com.roselake.jbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    //******************************************************
    // By default, if access is not specified, it is denied
    // you have to specifically permit access to each page, directory, or group of pages in your application (!)
    //******************************************************

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        //******************************************************************************
        // creates an object that can be re-used to encode passwords in your application
        // this method is called to provide an instance of a BCryptPasswordEncoder
        // BCryptPasswordEncoder's encode() method is called to return a String
        //          this String is the password hashed using the BCrypt hashing function
        //******************************************************************************
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return new MyUserDetailsService(userRepository);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //*****************************************************************************
        // .authorizeRequest :: tells application which requests should be authorized
        // .and() :: adds additional authentication rules
        // .formLogin() :: indicates the application should show a login form
        // .permitAll() :: everyone can see it even if they're not authenticated
        //*****************************************************************************

        http.authorizeRequests()
                .antMatchers("/", "/h2-console/**", "/register", "/css/**", "/image/**").permitAll()
                // everyone is allowed to view root, h2-console, registration page, and all static files
                .antMatchers("/admin").access("hasAuthority('ADMIN')")
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").defaultSuccessUrl("/", true).permitAll()
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/?logout").permitAll()
                .and()
                .httpBasic();
        http.csrf().disable();  // no tokens
        http.headers().frameOptions().disable();

    }

    // if login stops working, this may or may not need to be commented out... !
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //******************************************************
        // overrides the default configure method, and configures users who can use the application
        // by default, SpringBoot will assign a new random password to "user" if you don't configure this
        // once this is set up, you can log-in with any users specified here
        //******************************************************

        // USING a DATABASE
        auth.userDetailsService(userDetailsServiceBean()).passwordEncoder(passwordEncoder());
        // this sets up the Authentication for our current app
        // when we pass the userDetailsServiceBean() in, we're setting the current user
        // when we pass the passwordEncoder(), we're telling it what type of authentication to use


        // IN-MEMORY
        ////******************************************************
        //// when users are created, they can have at least one (or more!) "authorities" assigned to them.
        //// these "authorities" can restrict the user even further
        ////******************************************************
        //// note: the passwordEncoder() method, below, is the one we defined as a static @Bean, above!
        //// it simply returns a new BCryptPasswordEncoder on which we then call the encode() method to encode our password!
        ////******************************************************
        //auth.inMemoryAuthentication()
        //.withUser("user").password(passwordEncoder().encode("password")).authorities("USER")
        //.and()
        //.withUser("rose").password(passwordEncoder().encode("lake")).authorities("ADMIN");

    }

}
