package com.bluepilot.bluepilotgateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class BluePilotGatewayApplication

fun main(args: Array<String>) {
	runApplication<BluePilotGatewayApplication>(*args)
}
