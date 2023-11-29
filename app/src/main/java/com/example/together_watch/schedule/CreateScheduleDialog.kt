import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.together_watch.R
import com.example.together_watch.databinding.DialogBottomSheetCreateBinding
import com.example.together_watch.schedule.CreateScheduleContract
import com.example.together_watch.ui.schedule.RepeatType
import com.example.together_watch.ui.schedule.Schedule
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.Calendar

class CreateScheduleDialog(
    context: Context,
    private val presenter: CreateScheduleContract.Presenter
) : BottomSheetDialog(context, R.style.DialogStyle), CreateScheduleContract.View {

    private val binding = DialogBottomSheetCreateBinding.inflate(LayoutInflater.from(context))
    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

    init {
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        setContentView(binding.root)
        setupClickListeners()
    }

    override fun setupClickListeners() {
        with(binding) {
            btnCancel.setOnClickListener { hideBottomSheet() }
            btnSuccess.setOnClickListener {
                handleSuccessButtonClick()
            }
            etDate.setOnClickListener { showDatePickerDialog(etDate) }
            etStartTime.setOnClickListener { showTimePickerDialog(etStartTime) }
            etEndTime.setOnClickListener { showTimePickerDialog(etEndTime) }
            switchRepeat.setOnCheckedChangeListener { _, isChecked ->
                llRepeat.visibility = if (isChecked) View.VISIBLE else View.GONE
            }
            etEndDate.setOnClickListener { showDatePickerDialog(etEndDate) }
        }
    }

    private fun handleSuccessButtonClick() {
        val schedule = getScheduleFromInput()
        val isRepeat = isRepeat()

        if(isScheduleComplete(schedule, isRepeat)) {
            if (getStartTime().isAfter(getEndTime())) {
                showToast(R.string.msg_time_order)
            }
            else if (isRepeat && getStartDate().isAfter(getEndDate())) {
                showToast(R.string.msg_date_order)
            }
            else {
                presenter.onSuccessButtonClick(schedule, isRepeat, getRepeatType(), getEndDate())
                hideBottomSheet()
                showToast(R.string.msg_success_create_schedule)
            }
        } else {
            showToast(R.string.msg_fill_schedule)
        }
    }

    override fun getScheduleFromInput(): Schedule {
        with(binding) {
            val name = etScheduleName.text.toString()
            val place = etScheduleLocation.text.toString()
            val date = LocalDate.parse(etDate.text.toString(), dateFormat)
            val startTime = LocalTime.parse(etStartTime.text.toString(), timeFormat)
            val endTime = LocalTime.parse(etEndTime.text.toString(), timeFormat)
            val isGroup = false

            return Schedule(name, place, date, startTime, endTime, isGroup)
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
            return name.isNotEmpty()
                    && place.isNotEmpty()
                    && date.toString().isNotEmpty()
                    && startTime.toString().isNotEmpty()
                    && endTime.toString().isNotEmpty()
                    && (!isRepeat || !binding.etEndDate.text.isNullOrEmpty())
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

    private fun getStartDate(): LocalDate {
        return LocalDate.parse(binding.etDate.text.toString(), dateFormat)
    }
    private fun getEndDate(): LocalDate {
        val endDate = binding.etEndDate.text.toString()
        return if (endDate.isNotEmpty())
            LocalDate.parse(binding.etEndDate.text.toString(), dateFormat) else LocalDate.of(1970, Month.JANUARY, 1)
    }

    private fun getRepeatType(): RepeatType {
        return if (binding.radioGroupRepeatType.checkedRadioButtonId == R.id.radio_btn_month)
            RepeatType.MONTHLY else RepeatType.WEEKLY
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show()
    }
}
