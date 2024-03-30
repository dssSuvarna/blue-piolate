package com.bluepilot.coreservice.services

import com.bluepilot.coreservice.BaseTestConfig
import com.bluepilot.coreservice.generators.SystemResourceRequestGenerator
import com.bluepilot.entities.SystemResources
import com.bluepilot.enums.SystemResourceStatus
import com.bluepilot.errors.ErrorMessages
import com.bluepilot.exceptions.NotAllowedException
import com.bluepilot.exceptions.NotFoundException
import com.bluepilot.repositories.SystemResourcesRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SystemResourceServiceTest @Autowired constructor(
    private val systemResourcesRepository: SystemResourcesRepository,
    private val systemResourceService: SystemResourceService
) : BaseTestConfig() {
    @Test
    fun shouldNotDeleteAssignedResource() {
        val exception: Throwable = Assertions.assertThrows(NotAllowedException::class.java) {
            val systemResourceRequest =
                SystemResourceRequestGenerator.getAddSystemResourceRequest()
            val savedSystemResource = systemResourcesRepository.save(
                SystemResources(
                    type = systemResourceRequest.type,
                    systemId = systemResourceRequest.systemId,
                    operatingSystem = systemResourceRequest.operatingSystem,
                    osVersion = systemResourceRequest.osVersion,
                    ramType = systemResourceRequest.ramType,
                    ramSize = systemResourceRequest.ramSize,
                    ramFrequency = systemResourceRequest.ramFrequency,
                    storageSize = systemResourceRequest.storageSize,
                    processor = systemResourceRequest.processor,
                    diskType = systemResourceRequest.diskType,
                    additionalInfo = systemResourceRequest.additionalInfo,
                    status = SystemResourceStatus.ASSIGNED
                )
            )
            //Throws NotAllowedException as ASSIGNED resource cannot be deleted
            systemResourceService.deleteSystemResourceBySystemId(savedSystemResource.systemId)
        }.error

        Assertions.assertEquals("Active resource cannot be deleted", exception.message)
    }

    @Test
    fun shouldThrowNotFoundException() {
        //Should throw NotFoundException if invalid systemId is provided
        val invalidSystemId = 0L
        val exception: Throwable = Assertions.assertThrows(NotFoundException::class.java) {
            val systemResourceRequest =
                SystemResourceRequestGenerator.getAddSystemResourceRequest(systemId = "systemId2")
            systemResourceService.addSystemResources(systemResourceRequest)
            //Throws NotAllowedException as ASSIGNED resource cannot be deleted
            systemResourceService.fetchSystemResourceBySystemId(invalidSystemId)
        }.error

        Assertions.assertEquals(ErrorMessages.RESOURCE_NOT_FOUND, exception.message)
    }
}