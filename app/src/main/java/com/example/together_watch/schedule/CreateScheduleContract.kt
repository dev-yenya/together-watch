package com.example.together_watch.schedule

import android.widget.EditText
import com.example.together_watch.ui.schedule.Schedule

interface CreateScheduleContract {
    interface Model {
        fun saveSchedule(schedule: Schedule)
    }
    interface View {
        fun showBottomSheet()
        fun hideBottomSheet()
        fun showDatePickerDialog(editText: EditText)
        fun showTimePickerDialog(editText: EditText)
        fun setupClickListeners()
        fun getScheduleFromInput(): Schedule
    }
    interface Presenter {
        fun onSuccessButtonClick(schedule: Schedule)
    }
}