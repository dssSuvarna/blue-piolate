package com.bluepilot.repositories

import com.bluepilot.entities.Leave
import com.bluepilot.entities.User
import com.bluepilot.enums.LeaveStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.sql.Date

@Repository
interface LeaveRepository : JpaRepository<Leave, Long> {
    fun findByUserId(userId: Long): List<Leave>

    fun findByUserIdAndStatus(userId: Long, status: LeaveStatus): List<Leave>

    fun findAll(specification: Specification<Leave>, pageable: Pageable): Page<Leave>

    fun findAll(specification: Specification<Leave>, sort: Sort): List<Leave>

    fun findAllByUserIn(users: List<User>): List<Leave>

    @Query(
        value = """
         SELECT DISTINCT ON (id) * FROM (SELECT l2.* AS leave, json_array_elements(leave_dates) AS dt FROM user_service.leaves l2) AS leaves
         WHERE TO_TIMESTAMP(((dt ->> 'date') ::::BIGINT) / 1000) ::::DATE BETWEEN :fromDate AND :toDate AND status = :status ;
            """,
        nativeQuery = true
    )
    fun findLeavesByDateRange(
        @Param("fromDate") fromDate: Date,
        @Param("toDate") toDate: Date,
        @Param("status") status: String = LeaveStatus.APPROVED.name,
        pageable: Pageable
    ): Page<Leave>

    @Query(
        value = """
         SELECT DISTINCT ON (id) * FROM (SELECT l2.* AS leave, json_array_elements(leave_dates) AS dt FROM user_service.leaves l2) AS leaves
                WHERE TO_TIMESTAMP(((dt ->> 'date') ::::BIGINT) / 1000) ::::DATE BETWEEN :fromDate AND :toDate AND leave_type = :leaveType AND user_id = :userId  AND status = :status ;
        """,
        nativeQuery = true
    )
    fun findUserLeaves(
        fromDate: Date,
        toDate: Date,
        leaveType: String,
        userId: Long,
        status: String = LeaveStatus.APPROVED.name
    ): Set<Leave>
}