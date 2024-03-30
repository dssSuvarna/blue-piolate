package com.bluepilot.test.generators

import com.bluepilot.entities.AcademicDetails
import com.bluepilot.entities.AuthRole
import com.bluepilot.entities.AuthUser
import com.bluepilot.entities.BankDetails
import com.bluepilot.entities.ESIAndPFDetails
import com.bluepilot.entities.LeaveDetails
import com.bluepilot.entities.SalaryDetails
import com.bluepilot.entities.User
import com.bluepilot.entities.UserAddress
import com.bluepilot.entities.UserDetails
import com.bluepilot.enums.AuthUserStatus
import com.bluepilot.enums.Role
import com.bluepilot.enums.UserStatus
import com.bluepilot.repositories.AuthRoleRepository
import com.bluepilot.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.sql.Date

@Component
class UserGenerator @Autowired constructor(
    val userRepository: UserRepository,
    val authRoleRepository: AuthRoleRepository
) {
    fun getUser(
        id: Long = 2L,
        firstName: String = "firstname",
        lastName: String = "lastname",
        employeeCode: String = "VIN11",
        professionalEmail: String = "professional email",
        personalEmail: String = "personal email",
        gender: String = "gender",
        contactNumber: Long = 1234567890L,
        alternateContactNumber: Long = 1234567890L,
        alternateContactRelation: String = "relation with contact",
        dateOfBirth: Date = Date.valueOf("2020-01-01"),
        dateOfJoining: Date = Date.valueOf("2020-01-01"),
        bloodGroup: String = "O+",
        houseNumber: String = "houseNumber",
        street: String = "street",
        area: String = "area",
        city: String = "city",
        district: String = "district",
        state: String = "state",
        pincode: Long = 123456L,
        aadhaarNumber: String = "1234567890123",
        panNumber: String = "1234567890",
        status: UserStatus = UserStatus.CREATED,
        userName: String = "usernametest",
        password: String = "passwordtest",
        photo: String = "link",
        aadhaarDoc: String = "link",
        panDoc: String = "link",
        tenthPassOutYear: Int = 2000,
        tenthPercentage: BigDecimal = BigDecimal(100.00),
        twelfthPassOutYear: Int = 2000,
        twelfthCourse: String = "course",
        twelfthPercentage: BigDecimal = BigDecimal(100.00),
        degreePassOutYear: Int = 2000,
        degree: String = "degree",
        degreeCourse: String = "course",
        degreePercentage: BigDecimal = BigDecimal(100.00),
        document: String = "",
        accountNumber: Long = 123456789L,
        ifscCode: String = "ifsc-code",
        bankName: String = "Bank",
        authRole: AuthRole = authRoleRepository.getAuthRoleByName(Role.ADMIN)!!,
        withBankDetails: BankDetails? = generateBankDetails(accountNumber, ifscCode, bankName, "account holder name"),
        withSalarayDetails: SalaryDetails? = null,
        reporter: User = userRepository.findById(1L).get()
    ): User {
        return User(
            firstName = firstName,
            lastName = lastName,
            employeeCode = employeeCode,
            designation = "designation",
            authUser = generateAuthUser(userName, password, authRole),
            status = status,
            userDetails = generateUserDetails
                (
                professionalEmail,
                personalEmail,
                contactNumber,
                alternateContactNumber,
                alternateContactRelation,
                dateOfBirth,
                dateOfJoining,
                aadhaarNumber,
                panNumber,
                photo,
                aadhaarDoc,
                panDoc,
                gender,
                bloodGroup,
                houseNumber,
                street,
                area,
                city,
                district,
                state,
                pincode,
                tenthPassOutYear,
                tenthPercentage,
                twelfthPassOutYear,
                twelfthCourse,
                twelfthPercentage,
                degreePassOutYear,
                degree,
                degreeCourse,
                degreePercentage,
                document,
                withBankDetails,
                withSalarayDetails
            ),
            reporter = reporter
        )
    }

    fun generateAuthUser(userName: String, password: String, authRole: AuthRole): AuthUser {
        return AuthUser(
            username = userName,
            password = password,
            status = AuthUserStatus.ENABLED,
            role = authRole
        )
    }

    fun generateUserDetails(
        professionalEmail: String,
        personalEmail: String,
        contactNumber: Long,
        alternateContactNumber: Long,
        alternateContactRelation: String,
        dateOfBirth: Date,
        dateOfJoining: Date,
        aadhaarNumber: String,
        panNumber: String,
        photo: String,
        aadhaarDoc: String,
        panDoc: String,
        gender: String,
        bloodGroup: String,
        houseNumber: String,
        street: String,
        area: String,
        city: String,
        district: String,
        state: String,
        pincode: Long,
        tenthPassOutYear: Int,
        tenthPercentage: BigDecimal,
        twelfthPassOutYear: Int,
        twelfthCourse: String,
        twelfthPercentage: BigDecimal,
        degreePassOutYear: Int,
        degree: String,
        degreeCourse: String,
        degreePercentage: BigDecimal,
        document: String,
        bankDetails: BankDetails?,
        salaryDetails: SalaryDetails?
    ): UserDetails {
        return UserDetails(
            professionalEmail = professionalEmail,
            personalEmail = personalEmail,
            contactNumber = contactNumber,
            alternateContactNumber = alternateContactNumber,
            alternateContactRelation = alternateContactRelation,
            dateOfBirth = dateOfBirth,
            dateOfJoining = dateOfJoining,
            adhaarNumber = aadhaarNumber,
            panNumber = panNumber,
            photo = photo,
            adhaarDoc = aadhaarDoc,
            panDoc = panDoc,
            gender = gender,
            bloodGroup = bloodGroup,
            localAddress = generateAddress(houseNumber, street, area, city, district, state, pincode),
            permanentAddress = generateAddress(houseNumber, street, area, city, district, state, pincode),
            academicDetails = generateAcademicDetails
                (
                tenthPassOutYear,
                tenthPercentage,
                twelfthPassOutYear,
                twelfthCourse,
                twelfthPercentage,
                degreePassOutYear,
                degree,
                degreeCourse,
                degreePercentage,
                document
            ),
            esiAndPFDetails = ESIAndPFDetails(id = 0),
            bankDetails = bankDetails,
            leaveDetails = LeaveDetails(
                totalLeaves = BigDecimal(20.0),
                totalSickLeave = BigDecimal(8.0),
                totalPrivilegeLeave = BigDecimal(12.0),
            ),
            salaryDetails = salaryDetails
        )
    }

    fun generateAddress(
        houseNumber: String,
        street: String,
        area: String,
        city: String,
        district: String,
        state: String,
        pincode: Long
    ): UserAddress {
        return UserAddress(
            houseNumber = houseNumber,
            street = street,
            area = area,
            city = city,
            district = district,
            state = state,
            pincode = pincode
        )
    }

    fun generateAcademicDetails(
        tenthPassOutYear: Int,
        tenthPercentage: BigDecimal,
        twelfthPassOutYear: Int,
        twelfthCourse: String,
        twelfthPercentage: BigDecimal,
        degreePassOutYear: Int,
        degree: String,
        degreeCourse: String,
        degreePercentage: BigDecimal,
        document: String
    ): AcademicDetails {
        return AcademicDetails(
            tenthPassOutYear = tenthPassOutYear,
            tenthPercentage = tenthPercentage,
            tenthInstitute = "tenth-institute",
            twelfthPassOutYear = twelfthPassOutYear,
            twelfthCourse = twelfthCourse,
            twelfthInstitute = "twelfth-institute",
            twelfthPercentage = twelfthPercentage,
            degreePassOutYear = degreePassOutYear,
            degree = degree,
            degreeInstitute = "degree-institute",
            degreeCourse = degreeCourse,
            degreePercentage = degreePercentage,
            document = document
        )
    }

    fun generateBankDetails(
        accountNumber: Long,
        ifscCode: String,
        bankName: String,
        accountHolderName: String
    ): BankDetails {
        return BankDetails(
            accountNumber = accountNumber,
            ifsc = ifscCode,
            bankName = bankName,
            accountHolderName = accountHolderName
        )
    }
}

