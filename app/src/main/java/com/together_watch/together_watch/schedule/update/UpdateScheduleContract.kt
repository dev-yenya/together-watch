package com.together_watch.together_watch.schedule.update

import android.widget.EditText
import com.together_watch.together_watch.data.FetchedSchedule
import com.together_watch.together_watch.data.Schedule
import java.time.LocalDate

interface UpdateScheduleContract {
    interface Model {
        fun updateSchedule(selectedSchedule: FetchedSchedule)
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
        fun onSuccessButtonClick(fetchedSchedule: FetchedSchedule)
    }
}