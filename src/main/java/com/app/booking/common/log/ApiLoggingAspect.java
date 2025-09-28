package com.app.booking.common.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.logstash.logback.marker.Markers.appendEntries;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiLoggingAspect {
    static String API = "api";
    static String METHOD = "method";
    static String ENDPOINT = "endpoint";
    static String STATUS = "status";
    static String ARGS = "args";
    static String USER_ID = "userId";
    static String DURATION_MS = "durationMs";
    static String ERROR = "error";
    ObjectMapper objectMapper;

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public Object logApi(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attrs != null;
        HttpServletRequest request = attrs.getRequest();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String api = method + " " + uri;

        long start = System.currentTimeMillis();
        Object[] args = joinPoint.getArgs();

        Object[] logArgs = Arrays.stream(args)
                .filter(Objects::nonNull)
                .map(arg -> (arg instanceof Maskable m) ? m.maskSensitive() : arg)
                .toArray();

        String userId = Objects.toString(MDC.get("username"), "public");

        Map<String, Object> logMap = new HashMap<>();
        logMap.put(API, api);
        logMap.put(STATUS, "API CALL");
        logMap.put(ARGS, Arrays.asList(logArgs));
        logMap.put(USER_ID, userId);

        log.info(appendEntries(logMap), "Api call: {} {}", userId, api);

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            logMap.put(DURATION_MS, duration);
            logMap.put(STATUS, "API SUCCESS");
            log.info(appendEntries(logMap), "Api success: {} {}, in {} ms", userId, api, duration);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            logMap.put(DURATION_MS, duration);
            logMap.put(STATUS, "API FAILED");
            logMap.put(ERROR, e.getMessage());

            log.info(appendEntries(logMap), "Api failed: {} {}, in {} ms", userId, api, duration);
            throw e;
        }
    }
}

