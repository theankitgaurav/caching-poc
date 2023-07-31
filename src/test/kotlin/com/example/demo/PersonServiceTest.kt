package com.example.demo

import com.github.benmanes.caffeine.cache.Cache
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Import


@SpringBootTest
@Import(CacheConfiguration::class)
class PersonServiceTest {

    @Autowired
    private lateinit var service: PersonService

    @Autowired
    private lateinit var cacheManager: CacheManager

    @Test
    fun `test cache`() {
        service.fetch("person1")
        service.fetch("person2")
        service.fetch("person2")
        service.fetch("person3")
        service.fetch("person3")
        service.fetch("person3")

        val cache = cacheManager.getCache("personCache")
        Thread.sleep(1000)

        val caffeineCache = cache?.nativeCache as Cache<*, *>
        println(caffeineCache.asMap())
        println("Stats: ${caffeineCache.stats()}")
    }
}

