package com.example.together_watch.ui.schedule

import java.time.LocalDate
import java.time.LocalTime

data class Schedule(
    val name: String,
    val place: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val isGroup: Boolean
)

fun Schedule.toMap(): Map<String, Any?> {
    return mapOf(
        "name" to name,
        "place" to place,
        "date" to date.toString(),
        "startTime" to startTime.toString(),
        "endTime" to endTime.toString(),
        "isGroup" to isGroup
    )
}

enum class RepeatType {
    WEEKLY,
    MONTHLY
}