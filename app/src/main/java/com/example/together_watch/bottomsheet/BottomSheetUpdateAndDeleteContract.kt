package com.example.together_watch.bottomsheet

interface BottomSheetUpdateAndDeleteContract {
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