//package com.ru.gbox.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * @author Abhit
// */
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
//@Slf4j
//public class SimpleCorsFilter implements Filter {
//
//    // Reference: https://stackoverflow.com/a/30638914/8499307
//
//
//    public SimpleCorsFilter() {
//    }
//
//    /**
//     * Allowing all origins, headers and methods here is only intended to keep this example simple.
//     * This is not a default recommended configuration. Make adjustments as
//     * necessary to your use case.
//     */
////    @Bean
////    public org.springframework.web.filter.CorsFilter corsFilter() {
////        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
////        CorsConfiguration config = new CorsConfiguration();
////        config.setAllowCredentials(true);
////        config.addAllowedOrigin("*");
////        config.addAllowedHeader("*");
////        config.addAllowedMethod("*");
////        source.registerCorsConfiguration("/**", config);
////        return new org.springframework.web.filter.CorsFilter(source);
////    }
//
////    @Override
////    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//////        HttpServletResponse response = (HttpServletResponse) res;
//////        HttpServletRequest request = (HttpServletRequest) req;
//////
//////        response.setHeader("Access-Control-Allow-Origin", "*");
//////        response.setHeader("Access-Control-Allow-Methods", "*");
//////        response.setHeader("Access-Control-Allow-Headers", "*");
//////        response.setHeader("Access-Control-Max-Age", "3600");
//////        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
//////        // TODO: Try using below line without AuthCorsFilter should work fine
//////        // if ("OPTIONS".equalsIgnoreCase(((HttpServletRequest) req).getMethod())) {
//////            response.setStatus(HttpServletResponse.SC_OK);
//////        } else {
//////            chain.doFilter(req, res);
//////        }
////
////        final HttpServletResponse response = (HttpServletResponse) res;
////        log.info("request came for url {}", ((HttpServletRequest) req).getRequestURL());
////        response.setHeader("Access-Control-Allow-Origin", "*");
////        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
////        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
////        response.setHeader("Access-Control-Max-Age", "3600");
////        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(((HttpServletRequest) req).getMethod())) {
////            response.setStatus(HttpServletResponse.SC_OK);
////        } else {
////            chain.doFilter(req, res);
////        }
////    }
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//        HttpServletResponse response = (HttpServletResponse) res;
//        HttpServletRequest request = (HttpServletRequest) req;
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS, PATCH, DELETE");
//        response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Access-Control-Allow-Headers", "*");
//
//        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//            response.setStatus(HttpServletResponse.SC_OK);
//        } else {
//            chain.doFilter(req, res);
//        }
//    }
//
//    @Override
//    public void init(FilterConfig config) throws ServletException {
//    }
//
//    @Override
//    public void destroy() {
//    }
//
//}
