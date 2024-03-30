package com.bluepilot.entities

import com.bluepilot.enums.OnboardingContextStatus
import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Table
import  java.sql.Date
import jakarta.persistence.Id
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime

@Entity
@Table(name = "onboarding_context", schema = DataBaseUtils.SCHEMA.USER_SERVICE)
data class OnboardingContext(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var firstName: String? = null,
    var middleName: String? = null,
    var lastName: String? = null,
    var contactNumber: Long? = null,
    var alternateContactNumber: Long? = null,
    var alternateContactRelation: String? = null,
    @OneToOne(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "local_address_id")
    var localAddress: UserAddress? = null,
    @OneToOne(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "permanent_address_id")
    var permanentAddress: UserAddress? = null,
    var dateOfBirth: Date? = null,
    var tenthPassoutYear: Int? = null,
    var tenthPercentage: BigDecimal? = null,
    var tenthInstitute: String? = null,
    var twelfthPassoutYear: Int? = null,
    var twelfthCourse: String? = null,
    var twelfthPercentage: BigDecimal? = null,
    var twelfthInstitute: String? = null,
    var degreePassoutYear: Int? = null,
    var degree: String? = null,
    var degreeCourse: String? = null,
    var degreePercentage: BigDecimal? = null,
    var degreeInstitute: String? =null,
    var academicDetailsDocument: String? = null,
    var gender: String? = null,
    var aadhaarNumber: String? = null,
    var aadhaarDocument: String? = null,
    var panNumber: String? = null,
    var panDocument: String? = null,
    var photo: String? = null,
    val personalEmail: String,
    var bloodGroup: String? = null,
    val inviteCode: String,
    @Enumerated(EnumType.STRING)
    var onboardingContextStatus: OnboardingContextStatus = OnboardingContextStatus.INVITED,
    @CreationTimestamp
    val createdAt: Timestamp = Timestamp.valueOf(LocalDateTime.now())
)