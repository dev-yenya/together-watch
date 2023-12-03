package com.example.together_watch.schedule.updateAndDelete

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.example.together_watch.R
import com.example.together_watch.databinding.DialogBottomSheetUpdateAndDeleteBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class UpdateAndDeleteScheduleDialog(
    context: Context,
    private val presenter: UpdateAndDeleteScheduleContract.Presenter
) : BottomSheetDialog(context, R.style.DialogStyle), UpdateAndDeleteScheduleContract.View  {
    private val binding = DialogBottomSheetUpdateAndDeleteBinding.inflate(LayoutInflater.from(context))
    init {
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        setContentView(binding.root)
        setupClickListeners()
    }

    override fun setupClickListeners() {
        with(binding) {
            btnUpdate.setOnClickListener {/* 수정 */}
            btnDelete.setOnClickListener {
                presenter.onDeleteButtonClickedAndCheckedDeleted()
                showToast(R.string.msg_success_delete_schedule)
                hideBottomSheet()
            }
        }
    }

    override fun hideBottomSheet() {
        hide()
    }

    override fun showSchedule() {
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show()
    }
}