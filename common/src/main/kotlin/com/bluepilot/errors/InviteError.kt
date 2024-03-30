package com.bluepilot.errors

import com.bluepilot.errors.ErrorMessages.Companion.INVITE_ERROR
import org.springframework.http.HttpStatus

class InviteError (
        override val statusCode: HttpStatus = HttpStatus.BAD_REQUEST,
        override val message: String = INVITE_ERROR
) : BaseError(statusCode, message)