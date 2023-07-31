package com.example.demo

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfiguration {

    @Bean
    fun caffeineConfig(): Caffeine<*, *> {
        return Caffeine.newBuilder()
            .maximumWeight(124)
            .evictionListener { key: Any?, _: Any?, cause -> println("Evicted key $key due to $cause") }
            .weigher { _: Any, v: Any -> v.toByteArray().size }
            .recordStats()
    }

    @Bean
    @Primary
    fun cacheManagerCaffeine(caffeine: Caffeine<Any, Any>): CacheManager {
        val caffeineCacheManager = CaffeineCacheManager()
        caffeineCacheManager.setCaffeine(caffeine)
        return caffeineCacheManager
    }

    @Bean
    fun cacheManagerHashMap(): CacheManager = ConcurrentMapCacheManager()


    private fun Any.toByteArray(): ByteArray {
        return jacksonObjectMapper().writeValueAsBytes(this)
            .also { println("Weight of $this is ${it.size}") }
    }
}