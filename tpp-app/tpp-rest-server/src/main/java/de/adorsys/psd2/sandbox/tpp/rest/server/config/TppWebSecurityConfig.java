package de.adorsys.psd2.sandbox.tpp.rest.server.config;

import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.server.auth.LoginAuthenticationFilter;
import de.adorsys.psd2.sandbox.tpp.rest.server.auth.TokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static de.adorsys.psd2.sandbox.tpp.rest.server.config.PermittedResources.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class TppWebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserMgmtStaffRestClient userMgmtStaffRestClient;
    private final UserMgmtRestClient ledgersUserMgmt;
    private final AuthRequestInterceptor authInterceptor;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests().antMatchers(INDEX_WHITELIST).permitAll()
            .and()
            .authorizeRequests().antMatchers(APP_WHITELIST).permitAll()
            .and()
            .authorizeRequests().antMatchers(ACTUATOR_WHITELIST).permitAll()
            .and()
            .authorizeRequests().antMatchers(SWAGGER_WHITELIST).permitAll()
            .and()
            .cors()
            .and()
            .authorizeRequests().anyRequest().authenticated();

        http.headers().frameOptions().disable();
        http.httpBasic().disable();

        http.addFilterBefore(new LoginAuthenticationFilter(userMgmtStaffRestClient), BasicAuthenticationFilter.class);
        http.addFilterBefore(new TokenAuthenticationFilter(ledgersUserMgmt, authInterceptor), BasicAuthenticationFilter.class);
    }
}
