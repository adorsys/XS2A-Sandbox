package de.adorsys.psd2.sandbox.tpp.rest.server.auth;

import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppRestApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisableEndpointFilterTest {
    @InjectMocks
    private DisableEndpointFilter filter;

    @Mock
    private HttpServletRequest request = new MockHttpServletRequest();
    @Mock
    private HttpServletResponse response = new MockHttpServletResponse();
    @Mock
    private FilterChain chain;
    @Mock
    private Environment env;

    @Test
    void doFilterInternal_uri_not_present_in_map() throws IOException, ServletException {
        // Given
        SecurityContextHolder.clearContext();
        when(request.getServletPath()).thenReturn(TppRestApi.BASE_PATH + "/login");

        // When
        filter.doFilter(request, response, chain);

        // Then
        verify(chain, times(1)).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_uri_is_in_map_not_in_profile() throws IOException, ServletException {
        // Given
        SecurityContextHolder.clearContext();
        when(request.getServletPath()).thenReturn(TppRestApi.BASE_PATH + "/register");

        // When
        filter.doFilter(request, response, chain);

        // Then
        verify(chain, times(1)).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_uri_is_in_map_and_in_profile_false() throws IOException, ServletException {
        // Given
        SecurityContextHolder.clearContext();
        when(request.getServletPath()).thenReturn(TppRestApi.BASE_PATH + "/register");
        when(env.getProperty(anyString(),eq(Boolean.class))).thenReturn(false);

        // When
        filter.doFilter(request, response, chain);

        // Then
        verify(chain, times(1)).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_uri_is_in_map_and_in_profile_true() throws IOException, ServletException {
        // Given
        SecurityContextHolder.clearContext();
        when(request.getServletPath()).thenReturn(TppRestApi.BASE_PATH + "/register");
        when(env.getProperty(anyString(),eq(Boolean.class))).thenReturn(true);

        // When
        filter.doFilter(request, response, chain);

        // Then
        verify(chain, times(0)).doFilter(any(), any());
    }
}
