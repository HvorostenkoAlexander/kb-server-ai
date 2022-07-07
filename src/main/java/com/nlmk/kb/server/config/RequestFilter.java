package com.nlmk.kb.server.config;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Component
public class RequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        final var id = ((HttpServletRequest) servletRequest).getHeader(KbConstants.REQUEST_ID_HEADER);
        if (id == null) {
            MDC.put(KbConstants.REQUEST_ID_KEY, UUID.randomUUID().toString());
        } else {
            MDC.put(KbConstants.REQUEST_ID_KEY, id);
        }

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.remove(KbConstants.REQUEST_ID_KEY);
        }
    }

}
