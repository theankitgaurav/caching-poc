package com.example.demo

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary


@SpringBootTest
@Import(CaffeineCacheConfiguration::class)
class PersonServiceCaffeineCacheTest {

    @Autowired
    private lateinit var service: PersonService

    @Autowired
    private lateinit var cacheManager: CacheManager

    @Test
    fun `cache should not hold more than max weight in bytes`() {
        service.fetch("person1")
        service.fetch("person2")
        service.fetch("person3")

        val cache = cacheManager.getCache("personCache")
        Thread.sleep(1000)

        val caffeineCache = cache?.nativeCache as Cache<*, *>

        println("Stats: ${caffeineCache.stats()}")
        Assertions.assertEquals(caffeineCache.asMap().size, 2)
    }
}


@TestConfiguration
class CaffeineCacheConfiguration {
    @Bean
    fun caffeineConfig(): Caffeine<*, *> {
        return Caffeine.newBuilder()
            .maximumWeight(20)
            .evictionListener { key: Any?, _: Any?, cause -> println("Evicted key $key due to $cause") }
//            .weigher { _: Any, v: Any -> v.toByteArray().size }
            .weigher { _: Any, _: Any -> 10 }
            .recordStats()
    }

    @Bean
    @Primary
    fun cacheManagerCaffeine(caffeine: Caffeine<Any, Any>): CacheManager {
        val caffeineCacheManager = CaffeineCacheManager()
        caffeineCacheManager.setCaffeine(caffeine)
        return caffeineCacheManager
    }

    private fun Any.toByteArray(): ByteArray {
        return jacksonObjectMapper().writeValueAsBytes(this)
            .also { println("Weight of $this is ${it.size}") }
    }
}
