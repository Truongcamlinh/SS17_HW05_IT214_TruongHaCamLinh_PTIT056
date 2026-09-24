package com.grabfood.menu.config;

import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomCacheErrorHandlerTest {
    private final CustomCacheErrorHandler handler = new CustomCacheErrorHandler();
    private final Cache cache = mock(Cache.class);

    @Test
    void allCacheErrorsAreSwallowed() {
        when(cache.getName()).thenReturn("menuCache");
        RuntimeException redisDown = new RuntimeException("Connection refused");

        assertThatCode(() -> handler.handleCacheGetError(redisDown, cache, 1L)).doesNotThrowAnyException();
        assertThatCode(() -> handler.handleCachePutError(redisDown, cache, 1L, "value")).doesNotThrowAnyException();
        assertThatCode(() -> handler.handleCacheEvictError(redisDown, cache, 1L)).doesNotThrowAnyException();
        assertThatCode(() -> handler.handleCacheClearError(redisDown, cache)).doesNotThrowAnyException();
    }
}
