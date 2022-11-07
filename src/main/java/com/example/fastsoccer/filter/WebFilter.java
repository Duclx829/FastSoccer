package com.example.fastsoccer.filter;

import com.example.fastsoccer.entity.UserEntity;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.ArrayUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class WebFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String path = ((HttpServletRequest) servletRequest).getServletPath();
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        UserEntity userEntity = (UserEntity) ((HttpServletRequest) servletRequest).getSession().getAttribute("user");
        try {
            if (userEntity != null && !path.startsWith("/vendor") && !path.startsWith("/upload") && !path.startsWith("/img")
                    && !path.startsWith("/js") && !path.startsWith("/api") && !path.startsWith("/css")) {
                String[] userPaths = {"/loadPage", "/showDetail", "/loadMatching", "/loadUserProfile", "/loadFind"};
                String newDirection = "";

                if (userEntity.getRole().equals("OWN") && ArrayUtils.contains(userPaths, path)) {
                    newDirection = "load-manager-own";
                } else if (userEntity.getRole().equals("ADMIN") && ArrayUtils.contains(userPaths, path)) {
                    newDirection = "admin";
                }
                if (!newDirection.isEmpty()) {
                    res.reset();
                    res.resetBuffer();
                    res.sendRedirect(newDirection);
                }
            }
        }catch (Exception e){}
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
