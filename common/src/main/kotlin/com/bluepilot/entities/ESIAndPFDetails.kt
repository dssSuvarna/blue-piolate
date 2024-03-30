package com.bluepilot.entities

import com.bluepilot.enums.Gender
import com.bluepilot.enums.MaritalStatus
import com.bluepilot.enums.RelationWithEmp
import com.bluepilot.enums.Required
import com.bluepilot.enums.Nominee
import com.bluepilot.enums.SalaryCategory
import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.Entity
import jakarta.persistence.GenerationType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType
import java.math.BigDecimal
import java.sql.Date

@Entity
@Table(name = "esi_and_pf_details", schema = DataBaseUtils.SCHEMA.USER_SERVICE)
data class ESIAndPFDetails(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var empCode: String? = null,
    var uanNo: String? = null,
    var pfNoOrPfMemberId: String? = null,
    var esicNo: String? = null,
    var adhaarName: String? = null,
    @Enumerated(EnumType.STRING)
    var gender: Gender? = null,
    @Enumerated(EnumType.STRING)
    var maritalStatus: MaritalStatus? = null,
    var empDob: Date? = null, //format (DD/MM/YYYY)
    var empDoj: Date? = null, //format (DD/MM/YYYY)
    var mobNo: Long? = null,
    var fatherOrHusbandName: String? = null,
    @Enumerated(EnumType.STRING)
    var relWithEmp: RelationWithEmp? = null,
    @Enumerated(EnumType.STRING)
    var pf: Required? = null,
    @Enumerated(EnumType.STRING)
    var esi: Required? = null,
    @Enumerated(EnumType.STRING)
    var pt: Required?= null,
    var email: String?= null,
    var nationality: String?= null,
    var adhaarNo: String?= null,
    var panNo: String?= null,
    var bankAccountNo: String?= null,
    var bankName: String?= null,
    var ifscCode: String? = null,
    var flatOrHouseNo: String?= null,
    var streetNo: String?= null,
    var landMark: String?= null,
    var state: String?= null,
    var dist: String?= null,
    var fatherName: String?= null,
    var adhaarCardOfFather: String?= null,
    var dobOfFather: Date?= null, //format(dd/mm/yyyy)
    var motherName: String?= null,
    var adhaarCardOfMother: String?= null,
    var dobOfMother: Date?= null, //format(dd/mm/yyyy)
    var wifeName: String? = null,
    var adhaarCardOfWife: String? = null,
    var dobOfWife: Date? = null, //format(dd/mm/yyyy)
    var childOne: String? = null,
    var adhaarCardOfChildOne: String? = null,
    @Enumerated(EnumType.STRING)
    var genderOfChildOne: Gender? = null,
    var dobOfChildOne: Date? = null, //format(DD/MM/YYYY)
    var childTwo: String? = null,
    var adhaarCardOfChildTwo: String? = null,
    @Enumerated(EnumType.STRING)
    var genderOfChildTwo: Gender? = null,
    var dobOfChildTwo: Date? = null, //format(DD/MM/YYYY)
    var childThree: String? = null,
    var adhaarCardOfChildThree: String? = null,
    @Enumerated(EnumType.STRING)
    var genderOfChildThree: Gender? = null,
    var dobOfChildThree: Date? = null, //format(DD/MM/YYYY)
    var childFour: String? = null,
    var adhaarCardOfChildFour: String? = null,
    @Enumerated(EnumType.STRING)
    var genderOfChildFour: Gender? = null,
    var dobOfChildFour: Date? = null, //format(DD/MM/YYYY)
    var childFive: String? = null,
    var adhaarCardOfChildFive: String? = null,
    @Enumerated(EnumType.STRING)
    var genderOfChildFive: Gender? = null,
    var dobOfChildFive: Date? = null, //format(DD/MM/YYYY)
    @Enumerated(EnumType.STRING)
    var nominee: Nominee? = null,
    @Enumerated(EnumType.STRING)
    var salaryCategory: SalaryCategory? = null,
    var grossSalaryOfFullMonth: BigDecimal? = null,
    var basic: BigDecimal? = null,
    var hra: BigDecimal? = null,
    var conveyAllow: BigDecimal? = null,
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
    var total: BigDecimal? = null,
    var remark: String? = null
)

