package com.bluepilot.errors

import com.bluepilot.errors.ErrorMessages.Companion.USER_NOT_FOUND
import org.springframework.http.HttpStatus

class UserNotFound (
        override val statusCode: HttpStatus = HttpStatus.NOT_FOUND,
        override val message: String = USER_NOT_FOUND
) : BaseError(statusCode, message)