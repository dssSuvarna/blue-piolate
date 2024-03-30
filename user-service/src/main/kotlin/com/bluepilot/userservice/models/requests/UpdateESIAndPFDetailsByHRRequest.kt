package com.bluepilot.userservice.models.requests

import com.bluepilot.enums.Required
import com.bluepilot.enums.SalaryCategory
import java.math.BigDecimal
import java.sql.Date

data class UpdateESIAndPFDetailsByHRRequest(
    val userId: Long,
    val empCode: String,
    val uanNo: String,
    val pfNoOrPfMemberId: String,
    val esicNo: String,
    val empDoj: Date,
    val pf: Required,
    val esi: Required,
    val pt: Required,
    val email: String,
    val nationality: String,
    val bankAccountNo: String,
    val bankName: String,
    val ifscCode: String,
    val salaryCategory: SalaryCategory,
    val grossSalaryOfFullMonth: BigDecimal,
    val basic: BigDecimal,
    val hra: BigDecimal,
    val conveyAllow: BigDecimal,
    var cityCompAllow: BigDecimal? = null,
    var medAllow: BigDecimal? = null,
    var eduAllow: BigDecimal? = null,
    var transport: BigDecimal? = null,
    var tea: BigDecimal? = null,
    var mobileAllow: BigDecimal? = null,
    var newsPaper: BigDecimal? = null,
    var hostelAllow: BigDecimal? = null,
    var washingAllow: BigDecimal? = null,
    var foodAllow: BigDecimal? = null,
    val total: BigDecimal,
    val remark: String
)