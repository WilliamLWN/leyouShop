package com.leyou.order.config;

import com.leyou.common.utils.IdWorker;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 *
 */
@Data
@Component
@ConfigurationProperties(prefix = "ly.worker")
public class IdWorkProperties {
    private long workerId;
    private long dataCenterId;
}
