package com.bluepilot.entities

import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.GenerationType
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne

@Entity
@Table(name = "user_resources", schema = DataBaseUtils.SCHEMA.USER_SERVICE)
data class UserResource(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var idCard: String,
    var professionalEmail: String,
    @OneToOne
    @JoinColumn(name = "system_resource_id", referencedColumnName = "id")
    var systemResource: SystemResources? = null,
    val userId: Long,
)