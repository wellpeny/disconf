package com.baidu.disconf2.client.core.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baidu.disconf2.client.common.annotations.DisconfFile;
import com.baidu.disconf2.client.common.annotations.DisconfFileItem;
import com.baidu.disconf2.client.common.annotations.DisconfItem;
import com.baidu.disconf2.client.core.DisconfCoreMgr;
import com.baidu.disconf2.utils.ClassUtils;

/**
 * 配置拦截
 * 
 * @author liaoqiqi
 * @version 2014-6-11
 */
@Service
@Aspect
public class DisconfAspectJ {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(DisconfAspectJ.class);

    @Pointcut(value = "execution(public * get*(..))")
    public void anyPublicMethod() {
    }

    /**
     * 
     * @param pjp
     * @param disconfFileItem
     * @return
     * @throws Throwable
     */
    @Around("anyPublicMethod() && @annotation(disconfFileItem)")
    public Object decideAccess(ProceedingJoinPoint pjp,
            DisconfFileItem disconfFileItem) throws Throwable {

        MethodSignature ms = (MethodSignature) pjp.getSignature();
        Method method = ms.getMethod();

        String methodName = method.getName();

        //
        // Field名
        //
        String fieldName = ClassUtils.getFieldNameByGetMethodName(methodName);

        //
        // 文件名
        //
        Class<?> cls = method.getDeclaringClass();
        DisconfFile disconfFile = cls.getAnnotation(DisconfFile.class);

        //
        // 请求仓库配置数据
        //
        Object ret = DisconfCoreMgr.getInstance().getConfigFile(
                disconfFile.filename(), fieldName);
        if (ret != null) {
            return ret;
        }

        Object rtnOb = null;

        try {
            // 返回原值
            rtnOb = pjp.proceed();
        } catch (Throwable t) {
            LOGGER.info(t.getMessage());
            throw t;
        }

        return rtnOb;
    }

    /**
     * 
     * @param pjp
     * @param disconfFileItem
     * @return
     * @throws Throwable
     */
    @Around("anyPublicMethod() && @annotation(disconfItem)")
    public Object decideAccess(ProceedingJoinPoint pjp, DisconfItem disconfItem)
            throws Throwable {

        return null;
    }
}