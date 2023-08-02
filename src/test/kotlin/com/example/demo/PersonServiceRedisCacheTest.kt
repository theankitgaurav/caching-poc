package com.example.demo

import io.lettuce.core.support.caching.RedisCache
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import java.time.Duration


@SpringBootTest
@Import(RedisCacheConfig::class)
class PersonServiceRedisCacheTest {

    @Autowired
    private lateinit var service: PersonService

    @Autowired
    private lateinit var cacheManager: CacheManager

    @BeforeEach
    fun setUp() {
        cacheManager.getCache("personCache")?.invalidate()
    }

    @Test
    fun `cache should not hold more than max weight in bytes`() {
        service.fetch("person1")
        service.fetch("person2")
        service.fetch("person3")
        service.fetch("person3")

        service.fetchByNameAndAge("person1", 10)
        service.fetchByNameAndAge("person2", 10)

        cacheManager.getCache("personCache")
        Thread.sleep(1000)

        println("Done")
    }
}

@TestConfiguration
class RedisCacheConfig {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val config = RedisStandaloneConfiguration("localhost", 6379)
        return LettuceConnectionFactory(config)
    }

    @Bean
    @Primary
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory): RedisCacheManager {
        val connection = redisConnectionFactory.connection
        connection.setConfig("maxmemory-policy", "allkeys-lfu")
        connection.setConfig("maxmemory", "2m")
        val cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5))
        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(cacheConfiguration)
            .build()
    }
}



