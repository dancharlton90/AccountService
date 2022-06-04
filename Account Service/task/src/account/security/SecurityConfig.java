package account.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity(debug=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;




    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint) // Handle auth error
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler)
                .and()
                .authorizeRequests() // manage access
                .antMatchers("/api/auth/signup").permitAll()
                .antMatchers("/api/auth/changepass").hasAnyAuthority(
                        Role.ROLE_USER.toString(),
                        Role.ROLE_ACCOUNTANT.toString(),
                        Role.ROLE_ADMINISTRATOR.toString())
                .antMatchers("/api/empl/payment").hasAnyAuthority(
                        Role.ROLE_USER.toString(),
                        Role.ROLE_ACCOUNTANT.toString())
                .antMatchers("/api/acct/payments").hasAuthority(Role.ROLE_ACCOUNTANT.toString())
                .antMatchers("/api/auth/changepass").authenticated()
                .antMatchers("/api/admin/**").hasAuthority(Role.ROLE_ADMINISTRATOR.toString())
                .antMatchers("/api/security/**").hasAuthority(Role.ROLE_AUDITOR.toString())
                // other matchers
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(13);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(getEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
}
