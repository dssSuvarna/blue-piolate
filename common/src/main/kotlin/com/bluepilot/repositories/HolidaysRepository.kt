package com.bluepilot.repositories

import com.bluepilot.entities.HolidayList
import org.springframework.data.jpa.repository.JpaRepository

interface HolidaysRepository : JpaRepository<HolidayList, Long> {
    fun findByYear(year: Int): HolidayList?
}