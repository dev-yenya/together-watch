package com.example.together_watch.schedule.update

import com.example.together_watch.data.FetchedSchedule

class UpdateSchedulePresenter(
    private val model: UpdateScheduleModel
) : UpdateScheduleContract.Presenter {
    override fun onSuccessButtonClick(scheduleToUpdate: FetchedSchedule) {
        model.updateSchedule(scheduleToUpdate)
    }
}