package com.bluepilot.coreservice.services

import com.bluepilot.coreservice.BaseTestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.ZonedDateTime


@SpringBootTest
class LeaveDetailsServiceTest @Autowired constructor(
    val leaveDetailsService: LeaveDetailsService
) : BaseTestConfig() {

    @Test
    fun shouldGiveAsExpectedLeaveDetails() {
        val instantExpected1 = "2022-01-14T09:33:52Z"
        val zonedDateTime1 = ZonedDateTime.parse(instantExpected1)

        Mockito.mockStatic(ZonedDateTime::class.java).use { mockedLocalDateTime ->
            mockedLocalDateTime.`when`<Any> { ZonedDateTime.now() }
                .thenReturn(zonedDateTime1)
            assertThat(ZonedDateTime.now()).isEqualTo(zonedDateTime1)
            assertThat(leaveDetailsService.getTotalLeaves().setScale(1)).isEqualTo(BigDecimal(20.0).setScale(1))
            assertThat(leaveDetailsService.getTotalPrivilegeLeaves().setScale(1)).isEqualTo(BigDecimal(12.0).setScale(1))
            assertThat(leaveDetailsService.getTotalSickLeaves().setScale(1)).isEqualTo(BigDecimal(8.0).setScale(1))
        }

        val instantExpected2 = "2022-01-16T09:33:52Z"
        val zonedDateTime2 = ZonedDateTime.parse(instantExpected2)

        Mockito.mockStatic(ZonedDateTime::class.java).use { mockedLocalDateTime ->
            mockedLocalDateTime.`when`<Any> { ZonedDateTime.now() }
                .thenReturn(zonedDateTime2)
            assertThat(ZonedDateTime.now()).isEqualTo(zonedDateTime2)
            assertThat(leaveDetailsService.getTotalLeaves().setScale(1)).isEqualTo(BigDecimal(18.0).setScale(1))
            assertThat(leaveDetailsService.getTotalPrivilegeLeaves().setScale(1)).isEqualTo(BigDecimal(11.0).setScale(1))
            assertThat(leaveDetailsService.getTotalSickLeaves().setScale(1)).isEqualTo(BigDecimal(7.0).setScale(1))
        }
    }
}