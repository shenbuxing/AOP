package cn.sjy.aop.Impl;

import cn.sjy.aop.annotation.DoWhiteList;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Aspect
@Component
public class DoJoinPont {
    private List<String> list = new ArrayList<>(Arrays.asList("aaa", "bbb", "sjy"));

    @Pointcut("@annotation(cn.sjy.aop.annotation.DoWhiteList)")
     public void aopPoint(){
        System.out.println("aopPoint");
     }
     @Around("aopPoint()")
     public Object doRouter(ProceedingJoinPoint jp){
         try {
             Method method = getMethod(jp);
             DoWhiteList whiteList = method.getAnnotation(DoWhiteList.class);
             String filedValue = getFiledValue(whiteList.key(), jp.getArgs());
             String args = Arrays.toString(jp.getArgs());
             log.info("middleware whitelist handler method：{} value：{}", method.getName(), filedValue);
             if (list.contains(filedValue)) {
                 return jp.proceed();
             }
             // 拦截
             return returnObject(whiteList, method);
         } catch (Throwable e) {
             throw new RuntimeException(e);
         }

     }
    private Method getMethod(JoinPoint jp) throws NoSuchMethodException {
        Signature sig = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;
        return jp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
    }

    // 返回对象
    private Object returnObject(DoWhiteList whiteList, Method method) throws IllegalAccessException, InstantiationException {
        Class<?> returnType = method.getReturnType();
        String returnJson = whiteList.returnJson();
        if ("".equals(returnJson)) {
            return returnType.newInstance();
        }
        return JSON.parseObject(returnJson, returnType);
    }

    // 如果实参有很多，也能根据field值，从实参中取我想要的值
    private String getFiledValue(String filed, Object[] args) {
        String filedValue = null;
        for (Object arg : args) {
            try {
                if (null == filedValue || "".equals(filedValue)) {
                    filedValue = BeanUtils.getProperty(arg, filed);
                } else {
                    break;
                }
            } catch (Exception e) {
                if (args.length == 1) {
                    return args[0].toString();
                }
            }
        }
        return filedValue;
    }
}
