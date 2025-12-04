package org.example.expert.domain.common.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class AdminLoggingAspect {

    Logger logger = LoggerFactory.getLogger(AdminLoggingAspect.class);

    ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("@annotation(org.example.expert.domain.common.aop.LogAdmin)")
    public void logAdmin() {
    }

    @Around("logAdmin()")
    public Object loggingAdminAdvice(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();

        Object[] args = joinPoint.getArgs();

        String requestBody = objectMapper.writeValueAsString(args);
        logger.info("요청URL : {}", httpServletRequest.getRequestURI());
        logger.info("API 시작시간 : {}", LocalDateTime.now());
        logger.info("RequestBody : {}", requestBody);
        Object result = joinPoint.proceed();
        String resultBody = objectMapper.writeValueAsString(result);
        logger.info("API 종료시간 : {}", LocalDateTime.now());
        logger.info("ResultBody : {}", resultBody);

        return result;
    }
}
