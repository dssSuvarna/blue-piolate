package com.bluepilot.errors

import com.bluepilot.errors.ErrorMessages.Companion.INVALID_CREDENTIALS
import org.springframework.http.HttpStatus

class InvalidCredentials(
     override val statusCode: HttpStatus = HttpStatus.UNAUTHORIZED,
     override val message: String = INVALID_CREDENTIALS
) : BaseError(statusCode, message)