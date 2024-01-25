package com.together_watch.together_watch.schedule.update

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.together_watch.together_watch.R
import com.together_watch.together_watch.data.FetchedSchedule
import com.together_watch.together_watch.data.Schedule
import com.together_watch.together_watch.databinding.DialogBottomSheetCreateBinding
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.Calendar

class UpdateScheduleDialog(
    context: Context,
    private val scheduleToUpdate: FetchedSchedule,
    private val presenter: UpdateSchedulePresenter
) : BottomSheetDialog(context, R.style.DialogStyle), UpdateScheduleContract.View {
    private val binding = DialogBottomSheetCreateBinding.inflate(LayoutInflater.from(context))
    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

    init {
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        setContentView(binding.root)
        setView()
        setScheduleData()
        setupClickListeners()
    }

    override fun setupClickListeners() {
        with(binding) {
            btnCancel.setOnClickListener { hideBottomSheet() }
            btnSuccess.setOnClickListener { handleSuccessButtonClick() }
            etDate.setOnClickListener { showDatePickerDialog(etDate) }
            etStartTime.setOnClickListener { showTimePickerDialog(etStartTime) }
            etEndTime.setOnClickListener { showTimePickerDialog(etEndTime) }
            switchRepeat.setOnCheckedChangeListener { _, isChecked ->
                llRepeat.visibility = if (isChecked) View.VISIBLE else View.GONE
            }
            etEndDate.setOnClickListener { showDatePickerDialog(etEndDate) }
        }
    }

    fun setView() {
        with(binding) {
            tvPersonalSchedule.text = context.getString(R.string.update_schedule)
            llSwitch.visibility = View.GONE
        }
    }

    private fun setScheduleData() {
        with(binding) {
            etScheduleName.setText(scheduleToUpdate.schedule.name)
            etDate.setText(scheduleToUpdate.schedule.date)
            etStartTime.setText(scheduleToUpdate.schedule.startTime)
            etEndTime.setText(scheduleToUpdate.schedule.endTime)
            etScheduleLocation.setText(scheduleToUpdate.schedule.place)
        }
    }

    private fun handleSuccessButtonClick() {
        val schedule = getScheduleFromInput()
        val fetchedSchedule = FetchedSchedule(scheduleToUpdate.id, schedule)
        val isRepeat = isRepeat()

        if(isScheduleComplete(schedule, isRepeat)) {
            if (getStartTime().isAfter(getEndTime())) {
                showToast(R.string.msg_time_order)
            }
            else {
                presenter.onSuccessButtonClick(fetchedSchedule)
                hideBottomSheet()
                showToast(R.string.msg_success_update_schedule)
            }
        } else {
            showToast(R.string.msg_fill_schedule)
        }
    }

    override fun getScheduleFromInput(): Schedule {
        with(binding) {
            val name = etScheduleName.text.toString()
            val place = etScheduleLocation.text.toString()
            val date = runCatching {
                LocalDate.parse(etDate.text.toString(), dateFormat)
            }.getOrNull()
            val startTime = runCatching {
                LocalTime.parse(etStartTime.text.toString(), timeFormat)
            }.getOrNull()
            val endTime = runCatching {
                LocalTime.parse(etEndTime.text.toString(), timeFormat)
            }.getOrNull()
            val isGroup = false
            return Schedule(name, place, date.toString(), startTime.toString(), endTime.toString(), isGroup)
        }
    }

    override fun showBottomSheet() {
        show()
    }

    override fun hideBottomSheet() {
        dismiss()
    }

    override fun showDatePickerDialog(editText: EditText) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { _, y, m, d ->
                editText.setText(dateFormat.format(LocalDate.of(y, m + 1, d)))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DATE)
        ).show()
    }

    override fun showTimePickerDialog(editText: EditText) {
        val cal = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                editText.setText(timeFormat.format(LocalTime.of(hourOfDay, minute)))
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun roundToNearest10(minute: Int): Int {
        return (minute / 10) * 10
    }

    private fun isScheduleComplete(schedule: Schedule, isRepeat: Boolean): Boolean {
        with(schedule) {
            return !name.isNullOrEmpty()
                    && !place.isNullOrEmpty()
                    && !date.isNullOrEmpty()
                    && !startTime.isNullOrEmpty()
                    && !endTime.isNullOrEmpty()
        }
    }

    private fun isRepeat(): Boolean {
        return binding.switchRepeat.isChecked
    }

    private fun getStartTime(): LocalTime {
        return LocalTime.parse(binding.etStartTime.text.toString(), timeFormat)
    }

    private fun getEndTime(): LocalTime {
        return LocalTime.parse(binding.etEndTime.text.toString(), timeFormat)
    }


    private fun showToast(messageResId: Int) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show()
    }

}