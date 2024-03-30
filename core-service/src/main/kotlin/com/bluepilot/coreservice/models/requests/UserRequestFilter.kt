package com.bluepilot.coreservice.models.requests

import com.bluepilot.enums.UserStatus

data class UserRequestFilter(
    val status: UserStatus? = null,
)