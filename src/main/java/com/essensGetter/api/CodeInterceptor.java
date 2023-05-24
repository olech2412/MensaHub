package com.essensGetter.api;

import com.essensGetter.api.JPA.entities.APIAccess;
import com.essensGetter.api.JPA.repository.APIAccessRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Component
@Log4j2
public class CodeInterceptor implements HandlerInterceptor {

    private final APIAccessRepository repository;

    public CodeInterceptor(APIAccessRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String code = request.getParameter("code");
        APIAccess apiAccess = repository.findAPIAccessByToken(code);
        log.debug("Request from " + request.getRemoteAddr() + " with code " + code);
        if (apiAccess == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid code");
            log.warn("Invalid code " + code);
            return false;
        }

        if(apiAccess.getCalls() >= apiAccess.getMaxCalls()){
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Max calls reached, please contact the administrator");
            log.warn("User " + apiAccess.getEmail() + " has reached the max calls and tried to access the API");
            return false;
        }

        if(!apiAccess.getEnabled()){
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "This code is disabled, please contact the administrator");
            log.warn("User " + apiAccess.getEmail() + " tried to access the API with a disabled code");
            return false;
        }

        if(request.getMethod().equals("GET") && !apiAccess.getCanRead()){
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not allowed to perform this action");
            log.warn("User " + apiAccess.getEmail() + " tried to access the API with a code that is not allowed to read");
            return false;
        }

        if(request.getMethod().equals("POST") && !apiAccess.getCanWrite()){
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not allowed to perform this action");
            log.warn("User " + apiAccess.getEmail() + " tried to access the API with a code that is not allowed to write");
            return false;
        }

        apiAccess.calls++; // Increment the number of calls
        apiAccess.setLastCall(LocalDateTime.now());
        repository.save(apiAccess);
        return true;
    }
}
