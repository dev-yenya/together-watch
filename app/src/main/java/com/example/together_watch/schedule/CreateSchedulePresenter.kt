package com.example.together_watch.schedule

import com.example.together_watch.data.RepeatType
import com.example.together_watch.data.Schedule
import java.time.LocalDate

class CreateSchedulePresenter(
    private val model : CreateScheduleContract.Model
) : CreateScheduleContract.Presenter {

    override fun onSuccessButtonClick(
        schedule: Schedule,
        isRepeat: Boolean,
        repeatType: RepeatType,
        endDate: LocalDate
    ) {
        if (isRepeat) {
            model.saveRepeatSchedule(schedule, repeatType, endDate)
        } else {
            model.saveSchedule(schedule)
        }
    }
}