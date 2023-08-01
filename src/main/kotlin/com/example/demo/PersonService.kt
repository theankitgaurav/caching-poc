package com.example.demo

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.UUID

@Component
class PersonService {

    @Cacheable("personCache")
    fun fetch(name: String): Person {
        return Person(name)
    }

    @Cacheable("personCacheByNameAndAge")
    fun fetchByNameAndAge(name: String, age: Int): Person {
        return Person(name, age)
    }

}

data class Person(
    val name: String,
    val age: Int = 10,
    val id: String = UUID.randomUUID().toString(),
) : Serializable