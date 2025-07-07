package com.hyperskill.aggregator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "transaction")
public class TransactionProperties {
    private String host1;
    private String host2;
    private int maxRetries;
}
