package com.bluepilot.entities

data class CMSSheet(
    val clientCode: String,
    val productCode: String,
    val paymentType: String,
    val date: String,
    val debitAccountNumber: String,
    val salaryAmount: String,
    val bankCode: String,
    val beneficiaryName: String,
    val beneficiaryIfsc: String,
    val beneficiaryAccountNumber: String,
    val debitNarration:String,
    val creditNarration: String
)
