package com.app.ticket_common_library.config.redis;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.StringJoiner;


@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CacheKeyConfig {

    @Bean("simpleKeyGenerator")
    public KeyGenerator simpleKeyGenerator() {
        return (target, method, params) ->
                method.getName() + ":" + String.join(":", Arrays.stream(params)
                        .map(String::valueOf)
                        .toList());
    }

    @Bean("pageableKeyGenerator")
    public KeyGenerator pageableKeyGenerator() {
        return (target, method, params) -> {
            StringJoiner key = new StringJoiner(":");
            key.add(method.getName());

            for (Object param : params) {
                if (param instanceof Pageable pageable) {
                    key.add("page=" + pageable.getPageNumber());
                    key.add("size=" + pageable.getPageSize());
                    key.add("sort=" + pageable.getSort());
                } else {
                    key.add(String.valueOf(param));
                }
            }
            return key.toString();
        };
    }
}
