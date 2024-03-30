package com.bluepilot.repositories

import com.bluepilot.entities.UserCourse
import org.springframework.data.jpa.repository.JpaRepository

interface UserCourseRepository: JpaRepository<UserCourse, Long>