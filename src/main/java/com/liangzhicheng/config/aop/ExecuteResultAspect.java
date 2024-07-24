package com.liangzhicheng.config.aop;

import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.enums.OrderStatusChangeEvent;
import com.liangzhicheng.common.enums.OrderStatusEnum;
import com.liangzhicheng.config.aop.annotation.ExecuteResult;
import com.liangzhicheng.modules.entity.OrderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@RequiredArgsConstructor
@Slf4j
@Component
@Aspect
public class ExecuteResultAspect {

    private final StateMachine<OrderStatusEnum, OrderStatusChangeEvent> orderStateMachine;

    @Pointcut("@annotation(com.liangzhicheng.config.aop.annotation.ExecuteResult)")
    private void executeResultPointCut(){}

    @Around("executeResultPointCut()")
    public Object executeResultAround(ProceedingJoinPoint joinPoint) throws Throwable{
        //获取参数
        Object[] args = joinPoint.getArgs();
        log.info("参数args【{}】", args);
        Message message = (Message) args[0];
        OrderEntity order = (OrderEntity) message.getHeaders().get(Constants.ORDER_HEADER);
        //获取方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        //获取ExecuteResult注解
        ExecuteResult executeResult = method.getAnnotation(ExecuteResult.class);
        //获取业务执行key
        String key = executeResult.key();
        Object result = null;
        try{
            //执行方法
            result = joinPoint.proceed();
            //如果业务执行正常，则保存信息
            //成功则为1
            orderStateMachine.getExtendedState().getVariables().put(key + order.getId(), 1);
        }catch(Throwable e){
            log.error("状态机业务的切面发生异常【{}】", e.getMessage());
            //如果业务执行异常，则保存信息
            //将异常信息放置变量信息中，失败则为0
            orderStateMachine.getExtendedState().getVariables().put(key + order.getId(), 0);
            throw e;
        }
        return result;
    }

}