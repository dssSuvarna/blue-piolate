package com.bluepilot.userservice.models.responses


import com.bluepilot.enums.Gender
import com.bluepilot.enums.MaritalStatus
import com.bluepilot.enums.Nominee
import com.bluepilot.enums.RelationWithEmp
import com.bluepilot.enums.Required
import com.bluepilot.enums.SalaryCategory
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.sql.Date

data class UserESIAndPFDetailsResponse(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("empCode")
    var empCode: String? = null,
    @JsonProperty("uanNo")
    var uanNo: String? = null,
    @JsonProperty("pfNoOrPfMemberId")
    var pfNoOrPfMemberId: String? = null,
    @JsonProperty("esicNo")
    var esicNo: String? = null,
    @JsonProperty("adhaarName")
    var adhaarName: String? = null,
    @JsonProperty("gender")
    var gender: Gender? = null,
    @JsonProperty("maritalStatus")
    var maritalStatus: MaritalStatus? = null,
    @JsonProperty("empDob")
    var empDob: Date? = null,
    @JsonProperty("empDoj")
    var empDoj: Date? = null,
    @JsonProperty("mobNo")
    var mobNo: Long? = null,
    @JsonProperty("fatherOrHusbandName")
    var fatherOrHusbandName: String? = null,
    @JsonProperty("relWithEmp")
    var relWithEmp: RelationWithEmp? = null,
    @JsonProperty("pf")
    var pf: Required? = null,
    @JsonProperty("esi")
    var esi: Required? = null,
    @JsonProperty("pt")
    var pt: Required? = null,
    @JsonProperty("email")
    var email: String? = null,
    @JsonProperty("nationality")
    var nationality: String? = null,
    @JsonProperty("adhaarNo")
    var adhaarNo: String? = null,
    @JsonProperty("panNo")
    var panNo: String? = null,
    @JsonProperty("bankAccountNo")
    var bankAccountNo: String? = null,
    @JsonProperty("bankName")
    var bankName: String? = null,
    @JsonProperty("ifscCode")
    var ifscCode: String? = null,
    @JsonProperty("flatOrHouseNo")
    var flatOrHouseNo: String? = null,
    @JsonProperty("streetNo")
    var streetNo: String? = null,
    @JsonProperty("landMark")
    var landMark: String? = null,
    @JsonProperty("state")
    var state: String? = null,
    @JsonProperty("dist")
    var dist: String? = null,
    @JsonProperty("fatherName")
    var fatherName: String? = null,
    @JsonProperty("adhaarCardOfFather")
    var adhaarCardOfFather: String? = null,
    @JsonProperty("dobOfFather")
    var dobOfFather: Date? = null,
    @JsonProperty("motherName")
    var motherName: String? = null,
    @JsonProperty("adhaarCardOfMother")
    var adhaarCardOfMother: String? = null,
    @JsonProperty("dobOfMother")
    var dobOfMother: Date? = null,
    @JsonProperty("wifeName")
    var wifeName: String? = null,
    @JsonProperty("adhaarCardOfWife")
    var adhaarCardOfWife: String? = null,
    @JsonProperty("dobOfWife")
    var dobOfWife: Date? = null,
    @JsonProperty("childOne")
    var childOne: String? = null,
    @JsonProperty("adhaarCardOfChildOne")
    var adhaarCardOfChildOne: String? = null,
    @JsonProperty("genderOfChildOne")
    var genderOfChildOne: Gender? = null,
    @JsonProperty("dobOfChildOne")
    var dobOfChildOne: Date? = null,
    @JsonProperty("childTwo")
    var childTwo: String? = null,
    @JsonProperty("adhaarCardOfChildTwo")
    var adhaarCardOfChildTwo: String? = null,
    @JsonProperty("genderOfChildTwo")
    var genderOfChildTwo: Gender? = null,
    @JsonProperty("dobOfChildTwo")
    var dobOfChildTwo: Date? = null,
    @JsonProperty("childThree")
    var childThree: String? = null,
    @JsonProperty("adhaarCardOfChildThree")
    var adhaarCardOfChildThree: String? = null,
    @JsonProperty("genderOfChildThree")
    var genderOfChildThree: Gender? = null,
    @JsonProperty("dobOfChildThree")
    var dobOfChildThree: Date? = null,
    @JsonProperty("childFour")
    var childFour: String? = null,
    @JsonProperty("adhaarCardOfChildFour")
    var adhaarCardOfChildFour: String? = null,
    @JsonProperty("genderOfChildFour")
    var genderOfChildFour: Gender? = null,
    @JsonProperty("dobOfChildFour")
    var dobOfChildFour: Date? = null,
    @JsonProperty("childFive")
    var childFive: String? = null,
    @JsonProperty("adhaarCardOfChildFive")
    var adhaarCardOfChildFive: String? = null,
    @JsonProperty("genderOfChildFive")
    var genderOfChildFive: Gender? = null,
    @JsonProperty("dobOfChildFive")
    var dobOfChildFive: Date? = null,
    @JsonProperty("nominee")
    var nominee: Nominee? = null,
    @JsonProperty("salaryCategory")
    var salaryCategory: SalaryCategory? = null,
    @JsonProperty("grossSalaryOfFullMonth")
    var grossSalaryOfFullMonth: BigDecimal? = null,
    @JsonProperty("basic")
    var basic: BigDecimal? = null,
    @JsonProperty("hra")
    var hra: BigDecimal? = null,
    @JsonProperty("conveyAllow")
    var conveyAllow: BigDecimal? = null,
    @JsonProperty("cityCompAllow")
    var cityCompAllow: BigDecimal? = null,
    @JsonProperty("medAllow")
    var medAllow: BigDecimal? = null,
    @JsonProperty("eduAllow")
    var eduAllow: BigDecimal? = null,
    @JsonProperty("transport")
    var transport: BigDecimal? = null,
    @JsonProperty("tea")
    var tea: BigDecimal? = null,
    @JsonProperty("mobileAllow")
    var mobileAllow: BigDecimal? = null,
    @JsonProperty("newsPaper")
    var newsPaper: BigDecimal? = null,
    @JsonProperty("hostelAllow")
    var hostelAllow: BigDecimal? = null,
    @JsonProperty("washingAllow")
    var washingAllow: BigDecimal? = null,
    @JsonProperty("foodAllow")
    var foodAllow: BigDecimal? = null,
    @JsonProperty("total")
    var total: BigDecimal? = null,
    @JsonProperty("remark")
    var remark: String? = null
)