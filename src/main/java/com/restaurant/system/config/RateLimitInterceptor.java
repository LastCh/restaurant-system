package com.restaurant.system.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();

        // Apply rate limiting only to auth endpoints
        if (requestURI.contains("/api/auth/signin") || requestURI.contains("/api/auth/refresh")) {
            String clientIP = getClientIP(request);
            Bucket bucket = cache.computeIfAbsent(clientIP, k -> createBucket());

            if (bucket.tryConsume(1)) {
                return true;
            } else {
                log.warn("Rate limit exceeded for IP: {}", clientIP);
                response.setStatus(429);
                response.setContentType("application/json");
                try {
                    response.getWriter().write("{\"message\": \"Too many requests. Try again later.\"}");
                } catch (Exception e) {
                    log.error("Error writing rate limit response", e);
                }
                return false;
            }
        }

        return true;
    }

    // Create bucket with 5 attempts per 15 minutes
    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(5)
                .refillIntervally(5, Duration.ofMinutes(15))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    // Get client IP address (handles proxies)
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
