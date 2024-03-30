package com.bluepilot.entities

import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.GenerationType

@Entity
@Table(name = "bank_details", schema = DataBaseUtils.SCHEMA.CORE_SERVICE)
data class BankDetails(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val accountNumber: Long,
    val ifsc: String,
    val bankName: String,
    val accountHolderName: String
)