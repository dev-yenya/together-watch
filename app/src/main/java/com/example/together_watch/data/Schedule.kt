package com.example.together_watch.ui.schedule

import java.time.LocalTime
import java.time.LocalDate

data class Schedule(
    val title: String,
    val type: ScheduleType,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val location: String?,
)

enum class ScheduleType {
    PERSONAL, // 개인 일정
    APPOINTMENT, // 약속 일정
    MEETING // 미팅 일정 등
}
