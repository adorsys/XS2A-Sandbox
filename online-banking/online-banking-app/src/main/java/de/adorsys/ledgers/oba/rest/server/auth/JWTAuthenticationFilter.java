package de.adorsys.ledgers.oba.rest.server.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private static final List<String> EXCLUDED_URLS = Arrays.asList("/**/auth", "/**/login");
    private static final AntPathMatcher matcher = new AntPathMatcher();

    private final TokenAuthenticationService tokenAuthenticationService;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (log.isTraceEnabled()) {
            log.trace("doFilter start");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            if (log.isDebugEnabled()) {
                log.debug("Authentication is null. Try to get authentication from request...");
            }

            authentication = tokenAuthenticationService.getAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);

        if (log.isTraceEnabled()) {
            log.trace("doFilter end");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return EXCLUDED_URLS.stream()
                   .anyMatch(p -> matcher.match(p, request.getServletPath()));
    }
}
