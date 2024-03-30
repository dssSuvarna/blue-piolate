package com.bluepilot.coreservice.utils

import com.bluepilot.enums.EmployeeSalaryStatus
import com.bluepilot.enums.UserStatus

object StatusTransition {
    val userStatusTransition = mapOf(
        UserStatus.ONBOARDED to listOf(UserStatus.ACTIVE, UserStatus.DEACTIVE),
        UserStatus.ACTIVE to listOf(UserStatus.DEACTIVE),
        UserStatus.CREATED to listOf(),
        UserStatus.DEACTIVE to listOf()
    )

    val employeeSalaryStatusTransition = mapOf(
        EmployeeSalaryStatus.TO_BE_VERIFIED to listOf(
            EmployeeSalaryStatus.UPDATED,
            EmployeeSalaryStatus.VERIFIED
        ),
        EmployeeSalaryStatus.UPDATED to listOf(EmployeeSalaryStatus.VERIFIED),
        EmployeeSalaryStatus.VERIFIED to listOf(EmployeeSalaryStatus.UPDATED, EmployeeSalaryStatus.PAID),
        EmployeeSalaryStatus.PAID to listOf()
    )
}