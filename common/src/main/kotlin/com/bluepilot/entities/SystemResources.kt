package com.bluepilot.entities

import com.bluepilot.enums.DiskType
import com.bluepilot.enums.OSType
import com.bluepilot.enums.SystemResourceStatus
import com.bluepilot.enums.SystemType
import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "system_resources", schema = DataBaseUtils.SCHEMA.CORE_SERVICE)
data class SystemResources(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Enumerated(EnumType.STRING)
    val type: SystemType,
    val systemId: String,
    @Enumerated(EnumType.STRING)
    var operatingSystem: OSType,
    var osVersion: String,
    var ramType: String,
    var ramSize: String,
    var ramFrequency: String,
    var storageSize: String,
    var processor: String,
    @Enumerated(EnumType.STRING)
    var diskType: DiskType,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    var additionalInfo: Map<String, Any>,
    @Enumerated(EnumType.STRING)
    var status: SystemResourceStatus = SystemResourceStatus.UNASSIGNED
)
