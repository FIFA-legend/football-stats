package by.bsuir.football.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    private final BCryptPasswordEncoder encoder;

    @Autowired
    public WebSecurityConfiguration(UserDetailsService userDetailsService, BCryptPasswordEncoder encoder) {
        this.userDetailsService = userDetailsService;
        this.encoder = encoder;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests()
                .antMatchers("/match/update", "/match_registration", "/team_registration").authenticated()
                .antMatchers(
                        "/admin",
                        "/countries", "/countries/*", "/country/*",
                        "/leagues", "/leagues/*", "/league/*",
                        "/seasons", "/seasons/*", "/season/*",
                        "/venues", "/venues/*", "/venue/*",
                        "/teams", "/teams/*", "/team/*"
                ).hasAuthority("ADMIN")
                .anyRequest().permitAll()
            .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/matches", true)
            .and()
                .oauth2Login()
                .loginPage("/login")
                .authorizationEndpoint()
                    .baseUri("/login/oauth2")
                .and()
                .defaultSuccessUrl("/matches", true)
            .and()
                .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
            .and()
                .csrf()
                .disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
    }
}
