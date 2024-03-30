package com.bluepilot.coreservice.generators

import com.bluepilot.coreservice.models.requests.CreateSystemResourcesRequest
import com.bluepilot.enums.DiskType
import com.bluepilot.enums.OSType
import com.bluepilot.enums.SystemType

object SystemResourceRequestGenerator {
    fun getAddSystemResourceRequest(
        type: SystemType = SystemType.values().random(),
        systemId: String = "systemId",
        operatingSystem: OSType = OSType.values().random(),
        osVersion: String = "version",
        ramType: String = "ramType",
        ramSize: String = "32GB",
        ramFrequency: String = "2400MHZ",
        storageSize: String = "512GB",
        processor: String = "processor",
        diskType: DiskType = DiskType.values().random(),
        additionalInfo: Map<String, Any> = mapOf(Pair("key1", "value1"), Pair("key2", "value2"))
    ): CreateSystemResourcesRequest {
        return CreateSystemResourcesRequest(
            type,
            systemId,
            operatingSystem,
            osVersion,
            ramType,
            ramSize,
            ramFrequency,
            storageSize,
            processor,
            diskType,
            additionalInfo
        )
    }
}