package com.example.demo.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class ControllerAop {
    private static final Logger logger = LoggerFactory.getLogger(ControllerAop.class);

    @Before("execution(* com.example.demo..controller.*.*(..))")
            // excution( 반환자 class.함수 (인자) )
    public void doBefore(JoinPoint joinPoint){
        StringBuilder builder = new StringBuilder();
        builder.append("Before 요청 : ").append(joinPoint.getSignature().toShortString()).append(", 인자 = ");
        // Before 요청 uri="uri", 요청 인자=

        Object[] args = joinPoint.getArgs();
        //메서드에 들어가는 매개변수들에 대한 배열  ㄹvvl
        for(Object obj : args) {
            if (obj != null)
                builder.append("(").append(obj.getClass().getSimpleName()).append(":").append(obj).append("), ");
            // (type:value), ...
        }

        logger.debug(builder.toString());
    }

//    @After("execution(* com.example.demo.controller.*.*(..))")
//    public void doAfter(JoinPoint joinPoint) {
//        //System.out.println("end " + joinPoint.getSignature().toString());
//        logger.debug("end" + joinPoint.getSignature().toShortString());
//    }

    @AfterReturning(pointcut = "execution(* com.example.demo.controller.*.*(..))", returning = "returnValue")
    public void doAfterReturning(JoinPoint joinPoint, Object returnValue) {
        StringBuilder builder = new StringBuilder();
        builder.append("After 요청 : ").append(joinPoint.getSignature().toShortString()).append(", 반환 인자 = ").append(returnValue.toString());

        logger.debug(builder.toString());
    }
}
