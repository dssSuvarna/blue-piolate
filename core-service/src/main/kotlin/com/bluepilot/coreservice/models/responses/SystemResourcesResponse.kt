package com.bluepilot.coreservice.models.responses

import com.bluepilot.enums.DiskType
import com.bluepilot.enums.OSType
import com.bluepilot.enums.SystemResourceStatus
import com.bluepilot.enums.SystemType
import com.fasterxml.jackson.annotation.JsonProperty

data class SystemResourcesResponse(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("type")
    val type: SystemType,
    @JsonProperty("systemId")
    val systemId: String,
    @JsonProperty("operatingSystem")
    val operatingSystem: OSType,
    @JsonProperty("osVersion")
    val osVersion: String,
    @JsonProperty("ramType")
    val ramType: String,
    @JsonProperty("ramSize")
    val ramSize: String,
    @JsonProperty("ramFrequency")
    val ramFrequency: String,
    @JsonProperty("storageSize")
    val storageSize: String,
    @JsonProperty("processor")
    val processor: String,
    @JsonProperty("diskType")
    val diskType: DiskType,
    @JsonProperty("additionalInfo")
    val additionalInfo: Map<String, Any>,
    @JsonProperty("status")
    var status: SystemResourceStatus
)
