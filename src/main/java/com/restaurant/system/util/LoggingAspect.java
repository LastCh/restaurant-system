package com.restaurant.system.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.restaurant.system.service.*.*(..))")
    public void logServiceMethodEntry(JoinPoint joinPoint) {
        logger.info("Entering method: {} with arguments: {}",
                joinPoint.getSignature().getName(),
                joinPoint.getArgs());
    }

    @After("execution(* com.restaurant.system.service.*.*(..))")
    public void logServiceMethodExit(JoinPoint joinPoint) {
        logger.info("Exiting method: {}",
                joinPoint.getSignature().getName());
    }
}
