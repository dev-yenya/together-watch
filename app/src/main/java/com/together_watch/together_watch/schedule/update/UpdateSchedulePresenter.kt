package com.together_watch.together_watch.schedule.update

import com.together_watch.together_watch.data.FetchedSchedule


class UpdateSchedulePresenter(
    private val model: UpdateScheduleModel
) : UpdateScheduleContract.Presenter {
    override fun onSuccessButtonClick(scheduleToUpdate: FetchedSchedule) {
        model.updateSchedule(scheduleToUpdate)
    }
}