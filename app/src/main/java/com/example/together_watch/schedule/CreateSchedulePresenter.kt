package com.example.together_watch.schedule

import com.example.together_watch.ui.schedule.Schedule

class CreateSchedulePresenter(
    private val view : CreateScheduleContract.View,
    private val model : CreateScheduleContract.Model
) : CreateScheduleContract.Presenter {
    override fun onSuccessButtonClick(schedule: Schedule) {
        model.savePersonalSchedule(schedule)
    }
}