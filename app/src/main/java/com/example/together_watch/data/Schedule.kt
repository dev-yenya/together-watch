package com.example.together_watch.data

data class Schedule(
    val name: String,
    val place: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val isGroup: Boolean
)

data class FetchedSchedule(
    val id: String,
    val schedule: Schedule
)

fun Schedule.toMap(): Map<String, Any?> {
    return mapOf(
        "name" to name,
        "place" to place,
        "date" to date,
        "startTime" to startTime,
        "endTime" to endTime,
        "isGroup" to isGroup
    )
}

enum class RepeatType {
    WEEKLY,
    MONTHLY
}