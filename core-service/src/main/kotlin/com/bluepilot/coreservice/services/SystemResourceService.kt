package com.bluepilot.coreservice.services

import com.bluepilot.coreservice.mappers.SystemResourceMapper
import com.bluepilot.coreservice.models.requests.CreateSystemResourcesRequest
import com.bluepilot.coreservice.models.requests.UpdateSystemResourceRequest
import com.bluepilot.coreservice.models.responses.SystemResourcesResponse
import com.bluepilot.entities.SystemResources
import com.bluepilot.enums.SystemResourceStatus
import com.bluepilot.errors.ErrorMessages
import com.bluepilot.errors.NotAllowed
import com.bluepilot.errors.NotFoundError
import com.bluepilot.errors.ResourceNotFound
import com.bluepilot.exceptions.NotAllowedException
import com.bluepilot.exceptions.NotFoundException
import com.bluepilot.repositories.SystemResourcesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SystemResourceService @Autowired constructor(
    private val systemResourcesRepository: SystemResourcesRepository,
) {
    fun addSystemResources(createSystemResourcesRequest: CreateSystemResourcesRequest): SystemResourcesResponse {
        val systemResource = systemResourcesRepository.save(
            SystemResourceMapper.mapToEntity(createSystemResourcesRequest)
        )
        return SystemResourceMapper.mapToResponse(systemResource)!!
    }

    fun updateSystemResource(request: UpdateSystemResourceRequest): SystemResourcesResponse {
        val systemResource = systemResourcesRepository.findBySystemId(request.systemId)
            ?: throw NotFoundException(ResourceNotFound())
        systemResource.apply {
            operatingSystem = request.operatingSystem
            osVersion = request.osVersion
            ramType = request.ramType
            ramSize = request.ramSize
            ramFrequency = request.ramFrequency
            storageSize = request.storageSize
            processor = request.processor
            diskType = request.diskType
            additionalInfo = request.additionalInfo
        }
        return SystemResourceMapper.mapToResponse(systemResource)!!
    }

    fun deleteSystemResourceBySystemId(systemId: String) {
        val systemResource =
            systemResourcesRepository.findBySystemId(systemId) ?: throw NotFoundException(ResourceNotFound())
        if(systemResource.status == SystemResourceStatus.UNASSIGNED) {
            systemResourcesRepository.delete(systemResource)
        } else
            throw NotAllowedException(NotAllowed(message = "Active resource cannot be deleted"))
    }

    fun fetchAllSystemResources(): List<SystemResourcesResponse> {
        return systemResourcesRepository.findAll().map {
            SystemResourceMapper.mapToResponse(it)!!
        }
    }

    fun fetchSystemResourceBySystemId(systemResourceId: Long): SystemResourcesResponse {
        val systemResource = systemResourcesRepository.findById(systemResourceId)
            .orElseThrow { throw NotFoundException(ResourceNotFound()) }
        return SystemResourceMapper.mapToResponse(systemResource)!!
    }

    fun getSystemResourceById(systemResourceId: Long): SystemResources {
        return systemResourcesRepository.findById(systemResourceId)
            .orElseThrow { throw NotFoundException(NotFoundError(message = ErrorMessages.SYSTEM_RESOURCE_NOT_FOUND)) }
    }
}