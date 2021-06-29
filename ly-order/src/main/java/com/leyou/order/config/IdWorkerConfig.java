package com.leyou.order.config;

import com.leyou.common.utils.IdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 初始化分布式ID对象
 */
@Configuration
public class IdWorkerConfig {

    @Bean
    public IdWorker idWorker(IdWorkProperties idWorkerProps){
        return new IdWorker(
                idWorkerProps.getWorkerId(),
                idWorkerProps.getDataCenterId());
    }

}

