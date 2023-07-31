package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
class CacheDemoApplication

fun main(args: Array<String>) {
	runApplication<CacheDemoApplication>(*args)
}