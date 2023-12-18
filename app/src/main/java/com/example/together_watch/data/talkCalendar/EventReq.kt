package com.example.together_watch.data.talkCalendar

data class EventReq(
    val calendar_id: String,
    val event: EventDetails
)

data class EventDetails(
    val title: String,
    val time: TimeDetails,
    val rrlue: String,
    val description: String,
    val location: LocationDetails,
    val reminders: List<Int>,
    val color: String
)

data class TimeDetails(
    val start_at: String,
    val end_at: String,
    val time_zone: String,
    val all_day: Boolean,
    val lunar: Boolean
)

data class LocationDetails(
    val name: String,
    val location_id: Int,
    val address: String,
    val latitude: Double,
    val longitude: Double
)
