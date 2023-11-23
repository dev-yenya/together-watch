package com.example.together_watch.ui.bottomsheet

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