package com.bluepilot.errors

import org.springframework.http.HttpStatus

class UnauthorizeError(
    override val statusCode: HttpStatus = HttpStatus.UNAUTHORIZED,
    override val message: String = ErrorMessages.INVALID_ACCESS
) : BaseError(statusCode, message)