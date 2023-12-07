package com.example.together_watch.schedule.updateAndDelete

import com.example.together_watch.data.Schedule

class UpdateAndDeleteSchedulePresenter(
    private val model : UpdateAndDeleteScheduleContract.Model
) : UpdateAndDeleteScheduleContract.Presenter {
    override fun initialize() {
        TODO("Not yet implemented")
    }

    override fun onEditButtonClicked() {
        TODO("Not yet implemented")
    }

    override fun onDeleteButtonClickedAndCheckedDeleted() : Boolean {
        val isDeleted = model.deleteAndReturnIsDeleted()
        return isDeleted
    }

    override fun loadScheduleData(): Schedule {
        return model.getSchedule()
    }
}