package com.example.demo

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PersonService {

    @Cacheable("personCache")
    fun fetch(name: String): Person {
        return Person(name)
    }

}

data class Person(
    val name: String,
    val id: String = UUID.randomUUID().toString(),
)