package com.bluepilot.enums

class Template(
    var template: String,
    val subject: String
)

enum class NotificationEventType(
    val template: Template
) {
    INVITE_USER(
        Template(template = "invite-user.html", subject = "Invitation For Onboarding")
    ),
    RE_INVITE_USER(
        Template(template = "re-invite-user.html", subject = "Re-Invitation For Onboarding")
    ),

    UPDATED_PASSWORD(
        Template(template = "updated-password.html", subject = "Password Update")
    ),
    ESI_AND_PF_UPDATE(
        Template(template = "notify.html", subject = "ESI AND PF DETAILS UPDATED")
    ),

    LEAVE_APPLIED_APPLICATION(
        Template(template = "leave-applied-application.html", subject = "LEAVE APPLIED APPLICATION")
    ),

    LEAVE_APPROVED(
    Template(template = "leave-approved.html", subject = "LEAVE APPROVED")
    ),
    LEAVE_REJECTED(
        Template(template = "leave-rejected.html", subject = "LEAVE REJECTED")
    ),

    WELCOME_ONBOARDED_EMPLOYEE(
        Template(template = "welcome-onboarded-employee.html", subject = "WELCOME")
    ),

    ONBOARDED_EMPLOYEE(
        Template(template = "onboarded-employee.html", subject = "EMPLOYEE ONBOARDED")
    ),

    OTP_TEMPLATE(
        Template(template = "otp-template.html", subject = "OTP")
    ),

    PASSWORD_RESET_SUCCESSFULLY(
        Template(template = "password-reset-successfully.html", subject = "RESET PASSWORD")
    );
}
