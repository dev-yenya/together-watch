package com.example.together_watch.schedule.updateAndDelete

import com.example.together_watch.data.FetchedSchedule
import com.example.together_watch.data.Schedule

interface UpdateAndDeleteScheduleContract {
    interface Model {
        fun getSchedule() : Schedule
        fun getFetchedSchedule() : FetchedSchedule
        fun deleteAndReturnIsDeleted() : Boolean
    }
    interface View {
        fun showSchedule()
        fun setupClickListeners()

    }
    interface Presenter {
        fun initialize()
        fun onEditButtonClicked()
        fun onDeleteButtonClickedAndCheckedDeleted() : Boolean
        fun loadScheduleData() : Schedule

    }
}