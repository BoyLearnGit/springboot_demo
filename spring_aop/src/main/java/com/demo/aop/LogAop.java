package com.demo.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author: lyl
 * @date: 2018/12/5 14:23.
 */
@Aspect
@Order(1)
@Configuration
public class LogAop {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    @DeclareParents(value = "com.demo.aop.TestService", defaultImpl = SuperMan.class)
    private Fly fly;

    @Pointcut(value = "execution(* com.demo.controller..*.*(..))")
    public void cutService(){

    }

    @Before("cutService()")
    public void dealBefore(JoinPoint joinPoint){
        log.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        System.out.println("before......");
    }
    @AfterReturning(returning = "res",pointcut = "cutService()")
    public Object dealAfterReturning(Object res){
        //虽然AfterReturning增强处理可以访问到目标方法的返回值，但它不可以改变目标方法的返回值。
        System.out.println("afterReturning.......");
        String a="liyanlu";
        res=res+a;
        System.out.println(res);
        return res;
    }

    @AfterThrowing(pointcut = "cutService()")
    public void dealAfterThrowing(){
        System.out.println("程序异常了呢！！！！");
    }

    @Around("cutService()")
    public Object dealAround(ProceedingJoinPoint joinPoint){
        System.out.println("方法环绕start.....");
        try {
            Object o=joinPoint.proceed();
            o=o+"!!!";
            return o;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;

    }

    @After("cutService()")
    public void dealAfter(JoinPoint joinPoint){
        log.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        System.out.println("after..........");
    }


}
