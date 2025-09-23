package com.app.booking.config.redis;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Configuration
@ConfigurationProperties(prefix = "custom-cache")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CacheTtlProperties {
    Long defaultTtl = 300L;
    Map<String, Long> ttls = new HashMap<>();
}
