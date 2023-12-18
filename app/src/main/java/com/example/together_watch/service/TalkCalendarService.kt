package com.example.together_watch.service

import com.example.together_watch.data.talkCalendar.EventReq

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import com.example.together_watch.data.talkCalendar.EventRes
import retrofit2.Call

interface TalkCalendarService {
    @POST("/v2/api/calendar/create/event")
    fun createEvent(
        @Header("Authorization") authorization: String,
        @Body request: EventReq
    ) : Call<EventRes>
}