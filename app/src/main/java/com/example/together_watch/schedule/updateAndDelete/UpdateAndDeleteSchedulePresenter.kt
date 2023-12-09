package com.example.together_watch.schedule.updateAndDelete

import com.example.together_watch.data.FetchedSchedule
import com.example.together_watch.data.Schedule
import com.example.together_watch.schedule.create.CreateScheduleModel
import com.example.together_watch.schedule.create.CreateSchedulePresenter
import com.example.together_watch.schedule.update.UpdateScheduleDialog

class UpdateAndDeleteSchedulePresenter(
    private val model : UpdateAndDeleteScheduleContract.Model
) : UpdateAndDeleteScheduleContract.Presenter {
    override fun onEditButtonClicked() {

    }

    override fun onDeleteButtonClickedAndCheckedDeleted() : Boolean {
        val isDeleted = model.deleteAndReturnIsDeleted()
        return isDeleted
    }

    override fun loadScheduleData(): Schedule {
        return model.getSchedule()
    }

    override fun loadFetchedScheduleData(): FetchedSchedule {
        return model.getFetchedSchedule()
    }
}