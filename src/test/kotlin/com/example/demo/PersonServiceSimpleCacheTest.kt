package com.example.demo

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import java.util.concurrent.ConcurrentMap

@SpringBootTest
class PersonServiceSimpleCacheTest {

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

        val simpleCache = cache?.nativeCache as ConcurrentMap<*, *>

        println("Stats: ${simpleCache.entries}")
        Assertions.assertEquals(simpleCache.entries.size, 3, "Cache eviction not possible")
    }
}