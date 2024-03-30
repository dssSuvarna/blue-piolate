package com.bluepilot.entities

import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.Id
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType

@Entity
@Table(name = "user_address", schema = DataBaseUtils.SCHEMA.USER_SERVICE)
data class UserAddress(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var houseNumber: String,
    var street: String,
    var area: String,
    var city: String,
    var district: String,
    var state: String,
    var pincode: Long
)