package com.example.together_watch.schedule.updateAndDelete

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.example.together_watch.R
import com.example.together_watch.databinding.DialogBottomSheetUpdateAndDeleteBinding
import com.example.together_watch.schedule.create.CreateScheduleModel
import com.example.together_watch.schedule.create.CreateSchedulePresenter
import com.example.together_watch.schedule.update.UpdateScheduleDialog
import com.example.together_watch.schedule.update.UpdateScheduleModel
import com.example.together_watch.schedule.update.UpdateSchedulePresenter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class UpdateAndDeleteScheduleDialog(
    context: Context,
    private val presenter: UpdateAndDeleteScheduleContract.Presenter,
    private val forceRefresh: () -> Unit,
    private val getClickedSchedule: () -> Unit
) : BottomSheetDialog(context, R.style.DialogStyle), UpdateAndDeleteScheduleContract.View  {
    private val binding = DialogBottomSheetUpdateAndDeleteBinding.inflate(LayoutInflater.from(context))
    init {
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        setContentView(binding.root)
        showSchedule()
        setupClickListeners()
    }

    override fun setupClickListeners() {
        with(binding) {
            btnUpdate.setOnClickListener {
                hide()
                val selectedSchedule = presenter.loadFetchedScheduleData()
                val updateSchedulePresenter = UpdateSchedulePresenter(UpdateScheduleModel(forceRefresh, getClickedSchedule))
                UpdateScheduleDialog(context, selectedSchedule, updateSchedulePresenter).show()
            }
            btnDelete.setOnClickListener {
                presenter.onDeleteButtonClickedAndCheckedDeleted()
                showToast(R.string.msg_success_delete_schedule)
                hide()
            }
        }
    }

    override fun showSchedule() {
        val selectedSchedule = presenter.loadScheduleData()
        with(binding) {
            tvTitle.text = selectedSchedule.name
            tvTime.text = selectedSchedule.startTime + "~" + selectedSchedule.endTime
            tvPlace.text = selectedSchedule.place
        }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show()
    }
}