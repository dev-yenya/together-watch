package com.example.together_watch.ui.bottomsheet

import android.widget.EditText

interface BottomSheetCreateContract {
    interface Model {
        fun savePersonalSchedule(schedule: Schedule)
    }
    interface View {
        fun showBottomSheet()
        fun hideBottomSheet()
        fun showDatePickerDialog(editText: EditText)
        fun showTimePickerDialog(editText: EditText)
    }
    interface Presenter {
        fun onSuccessButtonClick(schedule: Schedule)
    }
}

enum class BottomSheetType {
    REPEAT,
    NON_REPEAT
}