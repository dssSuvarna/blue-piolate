package com.bluepilot.entities

import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.GenerationType
import jakarta.persistence.OneToOne
import jakarta.persistence.JoinColumn
import jakarta.persistence.CascadeType
import jakarta.persistence.FetchType
import java.sql.Date

@Entity
@Table(name = "user_details", schema = DataBaseUtils.SCHEMA.USER_SERVICE)
data class UserDetails(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var professionalEmail: String,
    var personalEmail: String,
    var contactNumber: Long,
    var alternateContactNumber: Long,
    var alternateContactRelation: String,
    val dateOfBirth: Date,
    val dateOfJoining: Date,
    val adhaarNumber: String,
    val panNumber: String,
    var photo: String,
    var adhaarDoc: String,
    var panDoc: String,
    val gender: String,
    var bloodGroup: String,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "local_address_id", referencedColumnName = "id")
    var localAddress: UserAddress,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "permanent_address_id", referencedColumnName = "id")
    val permanentAddress: UserAddress,
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "bank_details_id", referencedColumnName = "id")
    var bankDetails: BankDetails?,
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "academic_details_id")
    val academicDetails: AcademicDetails,
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "esi_and_pf_details_id", referencedColumnName = "id")
    var esiAndPFDetails: ESIAndPFDetails?,
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "leave_details_id", referencedColumnName = "id")
    var leaveDetails: LeaveDetails,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "salary_details_id")
    var salaryDetails: SalaryDetails?,
    var saturdayOff: Boolean = false
)
