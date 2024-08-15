package com.artinus.channelsubscription.common.config.state;

import com.artinus.channelsubscription.subscription.entity.SubscriptionStatus;
import com.artinus.channelsubscription.subscription.service.SubscriptionEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.data.redis.RedisPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.redis.RedisStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

@Configuration
public class StateMachinePersisterConfig {

    @Bean
    public StateMachineRuntimePersister<SubscriptionStatus, SubscriptionEvent, String> stateMachineRuntimePersister(
            final RedisStateMachineRepository redisStateMachineRepository) {
        return new RedisPersistingStateMachineInterceptor<>(redisStateMachineRepository);
    }

}
