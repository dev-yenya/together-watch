package com.together_watch.together_watch.schedule.updateAndDelete

import com.together_watch.together_watch.data.FetchedSchedule
import com.together_watch.together_watch.data.Schedule


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