package com.bluepilot.coreservice.models.responses

import com.bluepilot.enums.OnboardingContextStatus
import com.fasterxml.jackson.annotation.JsonProperty

data class OnboardingContextResponse(
        @JsonProperty("onboardingContextId")
        val onboardingContextId: Long,
        @JsonProperty("firstName")
        val firstName: String,
        @JsonProperty("personalMail")
        val personalMail: String,
        @JsonProperty("onboardingStatus")
        val onboardingStatus: OnboardingContextStatus = OnboardingContextStatus.INVITED,
        @JsonProperty("inviteCode")
        val inviteCode: String
)
