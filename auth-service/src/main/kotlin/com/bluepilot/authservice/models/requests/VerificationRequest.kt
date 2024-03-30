package com.bluepilot.authservice.models.requests

data class VerificationRequest(val username: String, val otp: String)
