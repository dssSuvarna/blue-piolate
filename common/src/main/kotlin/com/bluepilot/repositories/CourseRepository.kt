package com.bluepilot.repositories

import com.bluepilot.entities.Courses
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CourseRepository: JpaRepository<Courses, Long>