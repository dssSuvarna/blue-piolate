package com.bluepilot.enums

import java.math.BigDecimal

enum class Day(val value: BigDecimal) {
    FULL_DAY(BigDecimal.valueOf(1L)),
    HALF_DAY(BigDecimal.valueOf(0.5))
}