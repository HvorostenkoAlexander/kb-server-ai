package com.nlmk.kb.server.config;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Component
public class RequestFilter implements Filter {

    private final String REQUEST_ID_KEY = "requestID";

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        String xRequestId = ((HttpServletRequest) servletRequest).getHeader("X-Request-ID");

        MDC.put(REQUEST_ID_KEY, xRequestId == null ? UUID.randomUUID().toString() : xRequestId);

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.remove(REQUEST_ID_KEY);
        }
    }

    @Override
    public void destroy() {}
}

