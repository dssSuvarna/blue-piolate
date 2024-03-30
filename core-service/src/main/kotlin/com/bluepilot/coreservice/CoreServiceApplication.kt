package com.bluepilot.coreservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableDiscoveryClient
@EntityScan("com.bluepilot.entities")
@ComponentScan("com.bluepilot")
@EnableJpaRepositories(basePackages= ["com.bluepilot.repositories"])
class CoreServiceApplication

fun main(args: Array<String>) {
	runApplication<CoreServiceApplication>(*args)
}
