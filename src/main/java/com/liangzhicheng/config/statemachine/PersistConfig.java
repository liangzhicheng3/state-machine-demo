package com.liangzhicheng.config.statemachine;

import com.liangzhicheng.common.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.RepositoryStateMachinePersist;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.redis.RedisStateMachineContextRepository;
import org.springframework.statemachine.redis.RedisStateMachinePersister;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class PersistConfig<E, S> {

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 持久化到内存map中
     */
    @Bean(name = "stateMachinePersist")
    public static StateMachinePersister getPersist() {
        return new DefaultStateMachinePersister(new StateMachinePersist() {

            private final Map<Object, Object> map = new HashMap<>();

            @Override
            public void write(StateMachineContext stateMachineContext, Object obj) throws Exception {
                log.info("持久化状态机，stateMachineContext【{}】，obj【{}】", JSONUtil.toJSONString(stateMachineContext), JSONUtil.toJSONString(obj));
                map.put(obj, stateMachineContext);
            }

            @Override
            public StateMachineContext read(Object obj) throws Exception {
                log.info("获取状态机，obj【{}】", JSONUtil.toJSONString(obj));
                StateMachineContext stateMachineContext = (StateMachineContext) map.get(obj);
                log.info("获取状态机结果，stateMachineContext【{}】", JSONUtil.toJSONString(stateMachineContext));
                return stateMachineContext;
            }

        });
    }

    /**
     * 持久化到redis中，在分布式系统中使用
     */
    @Bean(name = "stateMachineRedisPersist")
    public RedisStateMachinePersister<E, S> getRedisPersist() {
        RedisStateMachineContextRepository<E, S> redisStateMachineContextRepository =
                new RedisStateMachineContextRepository<>(redisConnectionFactory);
        RepositoryStateMachinePersist repositoryStateMachinePersist =
                new RepositoryStateMachinePersist<>(redisStateMachineContextRepository);
        return new RedisStateMachinePersister<>(repositoryStateMachinePersist);
    }

}