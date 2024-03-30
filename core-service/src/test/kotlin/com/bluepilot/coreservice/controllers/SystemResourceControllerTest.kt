package com.bluepilot.coreservice.controllers

import com.bluepilot.configs.JwtService
import com.bluepilot.coreservice.BaseTestConfig
import com.bluepilot.coreservice.generators.SystemResourceRequestGenerator
import com.bluepilot.coreservice.models.responses.SystemResourcesResponse
import com.bluepilot.coreservice.services.SystemResourceService
import com.bluepilot.repositories.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import com.fasterxml.jackson.core.type.TypeReference

@SpringBootTest
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
class SystemResourceControllerTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val systemResourceService: SystemResourceService
) : BaseTestConfig() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun shouldAddSystemResources() {
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"
        val systemResourceRequest =
            SystemResourceRequestGenerator.getAddSystemResourceRequest()

        val result = mockMvc.post("/admin/resource") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(systemResourceRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper()
            .readValue(result.response.contentAsString, SystemResourcesResponse::class.java)
        assertEquals(response.systemId, systemResourceRequest.systemId)
        assertEquals(response.operatingSystem, systemResourceRequest.operatingSystem)
        assertEquals(response.ramSize, systemResourceRequest.ramSize)
        assertEquals(response.storageSize, systemResourceRequest.storageSize)
        assertEquals(response.diskType, systemResourceRequest.diskType)
    }

    @Test
    fun shouldDeleteSystemResource() {
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"

        val systemResourceRequest =
            SystemResourceRequestGenerator.getAddSystemResourceRequest()
        val systemResource = systemResourceService.addSystemResources(systemResourceRequest)

        val response = mockMvc.delete("/admin/resource/${systemResource.systemId}") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn().response

        assertEquals(true, response.contentAsString.contains("Resource deleted"))
    }

    @Test
    fun shouldGetSystemResourceBySystemId() {
        val systemResourceRequest =
            SystemResourceRequestGenerator.getAddSystemResourceRequest()
        val systemResource = systemResourceService.addSystemResources(systemResourceRequest)

        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"

        val result = mockMvc.get("/admin/resource/${systemResource.id}") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString, object : TypeReference<SystemResourcesResponse?>() {}
        )!!

        assertEquals(response.systemId, systemResourceRequest.systemId)
        assertEquals(response.operatingSystem, systemResourceRequest.operatingSystem)
        assertEquals(response.ramSize, systemResourceRequest.ramSize)
        assertEquals(response.storageSize, systemResourceRequest.storageSize)
        assertEquals(response.diskType, systemResourceRequest.diskType)
    }

    @Test
    fun shouldGetAllSystemResources() {
        val systemResourceRequest1 =
            SystemResourceRequestGenerator.getAddSystemResourceRequest(systemId = "systemId1")
        val systemResource1 = systemResourceService.addSystemResources(systemResourceRequest1)
        val systemResourceRequest2 =
            SystemResourceRequestGenerator.getAddSystemResourceRequest(systemId = "systemId2")
        val systemResource2 = systemResourceService.addSystemResources(systemResourceRequest2)
        val systemResourceRequest3 =
            SystemResourceRequestGenerator.getAddSystemResourceRequest(systemId = "systemId3")
        val systemResource3 = systemResourceService.addSystemResources(systemResourceRequest3)
        val systemResourceRequest4 =
            SystemResourceRequestGenerator.getAddSystemResourceRequest(systemId = "systemId4")
        val systemResource4 = systemResourceService.addSystemResources(systemResourceRequest4)

        val systemResourcesList = listOf(systemResource1, systemResource2, systemResource3, systemResource4)

        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"

        val result = mockMvc.get("/admin/resource") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString, object : TypeReference<List<SystemResourcesResponse?>>() {}
        )!!
        assertEquals(systemResourcesList.size, response.size)
    }
}