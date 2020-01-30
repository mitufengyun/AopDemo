package com.example.aop.aspect;

import android.util.Log;

import com.example.aop.annotation.BehaviorTrace;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * 指定注解作为切面
 */
@Aspect
public class BehaviorTraceAspect {
    //定义切面的规则
    //1.就在原来应用中哪些注释的地方放到当前切面进行处理
    //execution(注释名   注释用的地方)
    @Pointcut("execution(@com.example.aop.annotation.BehaviorTrace * *(..))")
    public void methodAnnotatedWithBehaviorTrace(){}

    //2.对进入切面的内容如何处理
    //advice
    //@Before()  在切入点之前运行
    //@After()   在切入点之后运行
    //@Around()  在切入点前后都运行
    @Around("methodAnnotatedWithBehaviorTrace()")
    public Object handleJoinPoint(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature=(MethodSignature)joinPoint.getSignature();
        String className=methodSignature.getDeclaringType().getSimpleName();
        String methodName=methodSignature.getName();
        String funName=methodSignature.getMethod().getAnnotation(BehaviorTrace.class).value();

        //统计时间
        long begin=System.currentTimeMillis();
        Object result= null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        long duration=System.currentTimeMillis()-begin;
        Log.d("xpf",String.format("功能：%s,%s类的%s方法执行了，用时%d ms",funName,className,methodName,duration));
        return result;
    }
}


