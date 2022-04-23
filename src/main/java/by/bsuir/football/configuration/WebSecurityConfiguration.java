package by.bsuir.football.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

import java.util.Collections;
import java.util.List;

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
                .antMatchers("/admin" /*"/match/update", "/match_registration", "/team_registration"*/).hasAuthority("ADMIN")
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

    /*public OAuth2AuthorizedClientService authorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = Collections.singletonList(configureGoogleAuthentication());
        return new InMemoryClientRegistrationRepository(registrations);
    }

    private ClientRegistration configureGoogleAuthentication() {
        return CommonOAuth2Provider.GOOGLE.getBuilder("google")
                .clientId(googleClientId)
                .clientSecret(googleClientSecret)
                .build();
    }*/

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
    }
}
