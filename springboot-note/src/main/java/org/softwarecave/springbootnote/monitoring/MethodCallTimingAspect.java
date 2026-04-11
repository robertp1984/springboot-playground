package org.softwarecave.springbootnote.monitoring;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
@ConditionalOnProperty(prefix = "app.methodCallTiming", name = "enabled", havingValue = "true")
public class MethodCallTimingAspect {

    private final long threshold;

    public MethodCallTimingAspect(@Value("${app.methodCallTiming.threshold}") long threshold) {
        this.threshold = threshold;
        log.info("MethodCallTimingAspect initialized with threshold {} ms", threshold);
    }

    @Pointcut("execution (public * org.softwarecave..*.*(..))")
    public void methodCallTiming() {
    }

    @Around("methodCallTiming()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = null;
        Throwable throwable = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            throwable = e;
        }
        long end = System.currentTimeMillis();
        long duration = end - start;
        if (duration >= threshold) {
            Signature signature = joinPoint.getSignature();
            String methodDescription = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
            log.warn("Method {} took {} ms", methodDescription, duration);
        }
        if (throwable != null) {
            throw throwable;
        } else {
            return result;
        }
    }

}
