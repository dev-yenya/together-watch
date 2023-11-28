package com.example.together_watch.schedule

import com.example.together_watch.ui.schedule.Schedule

class CreateSchedulePresenter(
    private val model : CreateScheduleContract.Model
) : CreateScheduleContract.Presenter {
    override fun onSuccessButtonClick(schedule: Schedule) {
        model.saveSchedule(schedule)
    }
}