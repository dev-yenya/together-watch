package com.example.together_watch.schedule.update

import android.content.Context
import android.view.LayoutInflater
import com.example.together_watch.data.FetchedSchedule
import com.example.together_watch.data.Schedule
import com.example.together_watch.databinding.DialogBottomSheetCreateBinding
import com.example.together_watch.schedule.create.CreateScheduleContract
import com.example.together_watch.schedule.create.CreateScheduleDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior

class UpdateScheduleDialog(
    context: Context,
    private val presenter: CreateScheduleContract.Presenter,
    private val scheduleToUpdate: FetchedSchedule
) : CreateScheduleDialog(context, presenter) {
    private val binding = DialogBottomSheetCreateBinding.inflate(LayoutInflater.from(context))

    init {
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        setContentView(binding.root)
        setupClickListeners()

    }
    override fun setupClickListeners() {
        super.setupClickListeners()
    }

    override fun getScheduleFromInput(): Schedule {
        return super.getScheduleFromInput()
    }

}