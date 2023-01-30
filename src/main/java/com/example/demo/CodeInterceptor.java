package com.example.demo;

import com.example.demo.JPA.APIAccess;
import com.example.demo.JPA.repository.APIAccessRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Log4j2
public class CodeInterceptor implements HandlerInterceptor {

    private final APIAccessRepository repository;

    public CodeInterceptor(APIAccessRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String code = request.getParameter("code");
        log.info("Request from " + request.getRemoteAddr() + " with code " + code);
        if (repository.findAPIAccessByToken(code) == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid code");
            log.warn("Invalid code " + code);
            return false;
        }
        APIAccess apiAccess = repository.findAPIAccessByToken(code);
        apiAccess.calls++; // Increment the number of calls
        repository.save(apiAccess);
        return true;
    }
}
