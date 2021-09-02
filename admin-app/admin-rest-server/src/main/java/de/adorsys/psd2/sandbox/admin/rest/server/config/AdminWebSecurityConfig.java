package de.adorsys.psd2.sandbox.admin.rest.server.config;

import de.adorsys.psd2.sandbox.auth.EnableSandboxSecurityFilter;
import de.adorsys.psd2.sandbox.admin.rest.server.auth.DisableEndpointFilter;
import de.adorsys.psd2.sandbox.auth.filter.LoginAuthenticationFilter;
import de.adorsys.psd2.sandbox.auth.filter.RefreshTokenFilter;
import de.adorsys.psd2.sandbox.auth.filter.TokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static de.adorsys.psd2.sandbox.admin.rest.server.config.PermittedResources.*;

@SuppressWarnings("PMD.UnusedImports")
@EnableSandboxSecurityFilter
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AdminWebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final LoginAuthenticationFilter loginAuthenticationFilter;
    private final RefreshTokenFilter refreshTokenFilter;
    private final Environment environment;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

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
        http.addFilterBefore(new DisableEndpointFilter(environment), BasicAuthenticationFilter.class);
        http.addFilterBefore(loginAuthenticationFilter, BasicAuthenticationFilter.class);
        http.addFilterBefore(refreshTokenFilter, BasicAuthenticationFilter.class);
        http.addFilterBefore(tokenAuthenticationFilter, BasicAuthenticationFilter.class);
    }
}
