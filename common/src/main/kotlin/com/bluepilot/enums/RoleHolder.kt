package com.bluepilot.enums

enum class RoleHolder(
    val roleType: Role,
    val permissions: Set<String>
) {
    ADMIN(Role.ADMIN,adminPermissions),
    HR(Role.HR, hrPermissions),
    EMPLOYEE(Role.EMPLOYEE, userPermissions)
}

private val userPermissions = setOf(
    "user.view",
    "document.upload",
    "document.delete",
    "document.view",
    "user.update.password",
    "user.onboarding.context.update",
    "user.onboarding.context.view",
    "user.update",
    "user.leave.apply",
    "user.leave.approve",
    "user.leave.view",
    "user.salary.details.view",
    "user.export.pdf",
    "user.course.create",
    "user.course.view",
    "user.training.update",
    "user.training.view",
    "user.course.assign",
    "user.course.start",
    "user.training.start",
    "user.attendance.update",
    "holiday.list.view"
)

private val hrPermissions = setOf(
    "user.register",
    "user.update.bankdetails",
    "user.onboarding.context.invite",
    "system.resources.update",
    "system.resources.view",
    "user.export.sheet",
    "user.status.change",
    "holiday.list.update",
    "user.update.salary.details",
    "user.resources.update",
    "user.resources.view",
    "user.employee.salary.details.view",
    "user.employee.salary.generate",
    "user.employee.salary.update",
    "user.update.weekoff"
).plus(userPermissions)

private val adminPermissions = setOf<String>().plus(hrPermissions)