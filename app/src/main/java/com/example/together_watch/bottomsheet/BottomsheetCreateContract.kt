package com.example.together_watch.bottomsheet

interface BottomsheetCreateContract {
    interface Model {
        fun getSchedule()
    }
    interface View {
        fun showBottomSheet()
        fun replaceRepeatBottomSheet()
        fun hideBottomSheet()
    }
    interface Presenter {
        fun createPersonalSchedule()
    }
}