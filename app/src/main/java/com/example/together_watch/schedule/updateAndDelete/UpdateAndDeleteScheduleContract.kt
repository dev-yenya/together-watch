package com.example.together_watch.schedule.updateAndDelete

interface UpdateAndDeleteScheduleContract {
    interface Model {
        fun getSchedule()
    }
    interface View {
        fun showBottomSheet()
        fun hideBottomSheet()
        fun showSchedule()
        fun setupClickListeners()
    }
    interface Presenter {
        fun updateSchedule()
        fun deleteSchedule()
    }
}