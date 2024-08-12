package com.artinus.channelsubscription.common.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private Logger logger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
    }

    @Around("com.artinus.channelsubscription.common.aspect.CustomPointCuts.restApi() " +
            "|| com.artinus.channelsubscription.common.aspect.CustomPointCuts.repository() " +
            "|| com.artinus.channelsubscription.common.aspect.CustomPointCuts.service()" +
            "&& !com.artinus.channelsubscription.common.aspect.CustomPointCuts.swaggerUI()" +
            "&& !com.artinus.channelsubscription.common.aspect.CustomPointCuts.springdoc() " +
            "&& !com.artinus.channelsubscription.common.aspect.CustomPointCuts.common()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch("LogExecutionTime Aop");

        stopWatch.start();
        Object proceed = joinPoint.proceed();
        stopWatch.stop();

        String msg = joinPoint.getSignature().getDeclaringType() + "." + joinPoint.getSignature().getName() + " : " +
                "running time = " + stopWatch.getTotalTimeMillis() + "ms";
        logger(joinPoint).info(msg);

        return proceed;
    }

}
