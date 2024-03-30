package com.bluepilot.coreservice.models.requests

import com.bluepilot.enums.DiskType
import com.bluepilot.enums.OSType
import jakarta.validation.constraints.NotNull

data class UpdateSystemResourceRequest(
    @field:NotNull
    val systemId: String,
    @field:NotNull
    val operatingSystem: OSType,
    @field:NotNull
    val osVersion: String,
    @field:NotNull
    val ramType: String,
    @field:NotNull
    val ramSize: String,
    @field:NotNull
    val ramFrequency: String,
    @field:NotNull
    val storageSize: String,
    @field:NotNull
    val processor: String,
    @field:NotNull
    val diskType: DiskType,
    var additionalInfo: Map<String, Any>
)