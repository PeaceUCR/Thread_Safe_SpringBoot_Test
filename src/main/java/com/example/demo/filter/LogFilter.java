package com.example.demo.filter;


import org.springframework.stereotype.Component;

import javax.servlet.*;

import java.io.IOException;


//https://stackoverflow.com/questions/4122870/what-is-the-use-of-filter-and-chain-in-servlet
//javax.servlet.Filter
@Component
public class LogFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
