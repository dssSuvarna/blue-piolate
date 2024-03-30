package com.bluepilot.errors

import org.springframework.http.HttpStatus

data class NotAllowed(
    override val statusCode: HttpStatus = HttpStatus.NOT_ACCEPTABLE,
    override val message: String = ErrorMessages.NOT_ALLOWED
) : BaseError(statusCode, message)
