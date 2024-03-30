package com.bluepilot.errors

import org.springframework.http.HttpStatusCode

data class ErrorResponse(val statusCode: Int, val status: HttpStatusCode, val message: String)
