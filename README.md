主要介绍在相关性能监测过程中的AOP的使用

1.导入aspectjrt.jar架包，添加依赖

2.为了便于编译和查看打印日志，在app module的build.gradle中添加相关代码

```java

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'org.aspectj:aspectjtools:1.8.8'
        classpath 'org.aspectj:aspectjweaver:1.8.8'
    }
}

android{
    ...
    defaultConfig{
        ...
    }
    
    buildTypes {
        ...
    }
}

dependencies {
    ...
}


import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main

final def log = project.logger
final def variants = project.android.applicationVariants

variants.all { variant ->
    if (!variant.buildType.isDebuggable()) {
        log.debug("Skipping non-debuggable build type '${variant.buildType.name}'.")
        return;
    }

    JavaCompile javaCompile = variant.javaCompile
    javaCompile.doLast {
        String[] args = ["-showWeaveInfo",
                         "-1.8",
                         "-inpath", javaCompile.destinationDir.toString(),
                         "-aspectpath", javaCompile.classpath.asPath,
                         "-d", javaCompile.destinationDir.toString(),
                         "-classpath", javaCompile.classpath.asPath,
                         "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]
        log.debug "ajc args: " + Arrays.toString(args)

        MessageHandler handler = new MessageHandler(true);
        new Main().run(args, handler);
        for (IMessage message : handler.getMessages(null, true)) {
            switch (message.getKind()) {
                case IMessage.ABORT:
                case IMessage.ERROR:
                case IMessage.FAIL:
                    log.error message.message, message.thrown
                    break;
                case IMessage.WARNING:
                    log.warn message.message, message.thrown
                    break;
                case IMessage.INFO:
                    log.info message.message, message.thrown
                    break;
                case IMessage.DEBUG:
                    log.debug message.message, message.thrown
                    break;
            }
        }
    }
}

```

3.自定义注解

```java
@Target(METHOD)
@Retention(RUNTIME)
public @interface BehaviorTrace {
    String value();
}
```

4.在需要监测的地方添加注解(给方法添加注解)

```java
@BehaviorTrace("摇一摇")
    public void mShake(View view){
        SystemClock.sleep(new Random().nextInt(2000));
    }
    @BehaviorTrace("语音消息")
    public void mAudio(View view){
        SystemClock.sleep(new Random().nextInt(2000));
    }
    @UserInfoBehaviorTrace("视频消息")
    @BehaviorTrace("视频消息")
    public void mVideo(View view ){
        SystemClock.sleep(new Random().nextInt(2000));
    }
    @UserInfoBehaviorTrace("说说功能")
    public void saySomething(View view){
        SystemClock.sleep(new Random().nextInt(2000));
    }
```

5.选择切面点并添加具体监测的逻辑代码

```java
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
        MethodSignature methodSignature =    (MethodSignature)joinPoint.getSignature();
        // 通过方法签名获取声明方法的类名
        String className=methodSignature.getDeclaringType().getSimpleName();
        // 方法名
        String methodName=methodSignature.getName();
        // 添加注解处传入的参数
        String funName = methodSignature.getMethod()
            .getAnnotation(BehaviorTrace.class).value();

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
```

日志如下：

com.example.aop D/xpf: 功能：摇一摇,MainActivity类的mShake方法执行了，用时1256 ms
com.example.aop D/xpf: 功能：语音消息,MainActivity类的mAudio方法执行了，用时1140 ms
com.example.aop D/xpf: 被执行了
com.example.aop D/xpf: 功能：视频消息,MainActivity类的mVideo方法执行了，用时356 ms
com.example.aop D/xpf: 被执行了

这样一来就可以在需要统计的方法，处添加注解，就能统一处理相同功能的监测，可通过不同的注解，实现不同功能的检测。

