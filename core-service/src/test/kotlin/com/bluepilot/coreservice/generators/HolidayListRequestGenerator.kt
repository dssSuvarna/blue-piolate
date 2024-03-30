package com.bluepilot.coreservice.generators

import com.bluepilot.coreservice.models.requests.UpdateHolidayListRequest
import com.bluepilot.entities.Holiday
import com.bluepilot.entities.MonthHolidays
import java.sql.Date

object HolidayListRequestGenerator {
    fun generateHolidayListRequest(
        id: Long = 0,
        year: Int = 2000,
        month1: String = "RandomMonth1",
        month2: String = "RandomMonth2"
    ): UpdateHolidayListRequest {
        return UpdateHolidayListRequest(
            id = id,
            year = year,
            holidays = generateHolidayList(month1, month2)
        )
    }

    private fun generateHolidayList(month1: String , month2: String): List<MonthHolidays> {
        return listOf(
            MonthHolidays(
                month = month1,
                holidays = listOf(
                    Holiday(
                        name = "New Year's Day",
                        date = Date(2022, 3, 23),
                        description = "Celebration of the new year"
                    ),
                    Holiday(
                        name = "Martin Luther King Jr. Day",
                        date = Date(2022, 3, 23),
                        description = "Commemoration of MLK's achievements"
                    )
                )
            ),
            MonthHolidays(
                month = month2,
                holidays = listOf(
                    Holiday(
                        name = "Valentine's Day",
                        date = Date(2022, 3, 23),
                        description = "Celebration of love and affection"
                    )
                )
            )
        )
    }
}