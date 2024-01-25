package com.together_watch.together_watch.schedule.create

import android.widget.EditText
import com.together_watch.together_watch.data.RepeatType
import com.together_watch.together_watch.data.Schedule
import java.time.LocalDate

interface CreateScheduleContract {
    interface Model {
        fun saveSchedule(schedule: Schedule)
        fun saveRepeatSchedule(
            schedule: Schedule,
            repeatType: RepeatType,
            endDate: LocalDate
        )
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
        fun onSuccessButtonClick(
            schedule: Schedule,
            isRepeat: Boolean,
            repeatType: RepeatType,
            endDate: LocalDate
        )
    }
}