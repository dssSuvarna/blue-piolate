package com.bluepilot.errors

import org.springframework.http.HttpStatus

class ResourceNotFound (
    override val statusCode: HttpStatus = HttpStatus.NOT_FOUND,
    override val message: String = ErrorMessages.RESOURCE_NOT_FOUND
) : BaseError(statusCode, message)