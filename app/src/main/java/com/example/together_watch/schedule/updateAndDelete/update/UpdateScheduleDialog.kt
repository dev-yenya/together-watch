package com.example.together_watch.schedule.updateAndDelete.update

import android.content.Context
import com.example.together_watch.data.FetchedSchedule
import com.example.together_watch.data.Schedule
import com.example.together_watch.schedule.create.CreateScheduleContract
import com.example.together_watch.schedule.create.CreateScheduleDialog

class UpdateScheduleDialog(
    context: Context,
    private val presenter: CreateScheduleContract.Presenter,
    private val scheduleToUpdate: FetchedSchedule
) : CreateScheduleDialog(context, presenter) {

    // You can override methods or add new methods specific to the update dialog here

    override fun setupClickListeners() {
        super.setupClickListeners()
        // Additional setup specific to the update dialog
    }

    override fun getScheduleFromInput(): Schedule {
        // You may override this method if you need to provide different behavior for updating
        // For example, populate the input fields with the existing schedule data
        return super.getScheduleFromInput()
    }

    // Additional methods specific to the update dialog can be added here
}