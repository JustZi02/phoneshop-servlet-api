package com.es.phoneshop.web;

import com.es.phoneshop.model.security.DefaultDosProtectionService;
import com.es.phoneshop.model.security.DosProtectionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DosFilterServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private FilterConfig config;

    private DosFilter dosFilter;

    @Before
    public void setup() throws ServletException {
        dosFilter = new DosFilter();
        dosFilter.init(config);
    }

    @Test
    public void testDoFilterAllowed() throws Exception {
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        assertTrue(DefaultDosProtectionService.getInstance().isAllowed("192.168.1.1"));
        dosFilter.doFilter(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterBlocked() throws Exception {
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        DosProtectionService dosProtectionService = DefaultDosProtectionService.getInstance();
        for (int i = 0; i < 20; i++) {
            dosProtectionService.isAllowed("192.168.1.1");
        }
        assertFalse(dosProtectionService.isAllowed("192.168.1.1"));
        dosFilter.doFilter(request, response, filterChain);
        verify(response).setStatus(429);
        verify(filterChain, never()).doFilter(request, response);
    }
}
