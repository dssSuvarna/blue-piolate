package com.bluepilot.coreservice.mappers

import com.bluepilot.coreservice.models.requests.CreateSystemResourcesRequest
import com.bluepilot.coreservice.models.responses.SystemResourcesResponse
import com.bluepilot.entities.SystemResources
import org.springframework.stereotype.Component

@Component
class SystemResourceMapper {
    companion object {
        fun mapToEntity(createSystemResourcesRequest: CreateSystemResourcesRequest): SystemResources {
            return SystemResources(
                type = createSystemResourcesRequest.type,
                systemId = createSystemResourcesRequest.systemId,
                operatingSystem = createSystemResourcesRequest.operatingSystem,
                osVersion = createSystemResourcesRequest.osVersion,
                ramType = createSystemResourcesRequest.ramType,
                ramSize = createSystemResourcesRequest.ramSize,
                ramFrequency = createSystemResourcesRequest.ramFrequency,
                storageSize = createSystemResourcesRequest.storageSize,
                processor = createSystemResourcesRequest.processor,
                diskType = createSystemResourcesRequest.diskType,
                additionalInfo = createSystemResourcesRequest.additionalInfo
            )
        }

        fun mapToResponse(systemResource: SystemResources?): SystemResourcesResponse? {
            return if(systemResource !=null) SystemResourcesResponse(
                id = systemResource.id,
                status = systemResource.status,
                type = systemResource.type,
                systemId = systemResource.systemId,
                operatingSystem = systemResource.operatingSystem,
                osVersion = systemResource.osVersion,
                ramType = systemResource.ramType,
                ramSize = systemResource.ramSize,
                ramFrequency = systemResource.ramFrequency,
                storageSize = systemResource.storageSize,
                processor = systemResource.processor,
                diskType = systemResource.diskType,
                additionalInfo = systemResource.additionalInfo
            ) else null
        }
    }
}