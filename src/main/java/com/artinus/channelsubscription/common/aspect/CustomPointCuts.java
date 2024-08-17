package com.artinus.channelsubscription.common.aspect;

import org.aspectj.lang.annotation.Pointcut;

public final class CustomPointCuts {

    @Pointcut("execution(* org.springdoc.webmvc.api.*.*(..))")
    public void swaggerUI() {
    }

    @Pointcut("execution(* org.springdoc.webmvc.ui.*.*(..))")
    public void springdoc() {
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {
    }

    @Pointcut("within(@com.artinus.channelsubscription.common.stereotype.UseCase)")
    public void useCase() {
    }

    @Pointcut(
            "within(@org.springframework.stereotype.Repository *)" +
                    " || within(@org.springframework.stereotype.Service *)" +
                    " || within(@org.springframework.web.bind.annotation.RestController *)"
    )
    public void springBeanPointcut() {
    }


    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void getApi() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postApi() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putApi() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void patchApi() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void deleteApi() {
    }

    @Pointcut("getApi() || postApi() || putApi() || patchApi() || deleteApi()")
    public void restApi() {
    }

    @Pointcut("execution(* com.artinus.channelsubscription.*.repository.*.*(..))")
    public void repository() {
    }

    @Pointcut("execution(* com.artinus.channelsubscription.*.service.*.*(..))")
    public void service() {
    }

    @Pointcut("execution(* com.artinus.channelsubscription.common.*.*(..))")
    public void common() {
    }

}
