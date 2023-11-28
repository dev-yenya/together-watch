package com.example.together_watch.schedule

interface UpdateAndDeleteScheduleContract {
    interface Model {
        fun getSchedule()
    }
    interface View {
        fun showBottomSheet()
        fun hideBottomSheet()
    }
    interface Presenter {
        fun updateSchedule()
        fun deleteSchedule()
    }
}