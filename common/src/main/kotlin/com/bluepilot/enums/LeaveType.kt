package com.bluepilot.enums

enum class LeaveType(private val label: String) {
    SICK_LEAVE("SL"),
    PRIVILEGE_LEAVE("PL"),
    COMPENSATORY_OFF("CO"),
    LOP("LOP");

    fun getLabel(): String {
        return this.label
    }
}