import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import com.example.together_watch.R
import com.example.together_watch.databinding.DialogBottomSheetCreateBinding
import com.example.together_watch.schedule.CreateScheduleContract
import com.example.together_watch.ui.schedule.Schedule
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Calendar

class CreateScheduleDialog(
    context: Context,
    private val presenter: CreateScheduleContract.Presenter
) : BottomSheetDialog(context, R.style.DialogStyle), CreateScheduleContract.View {

    private val binding = DialogBottomSheetCreateBinding.inflate(LayoutInflater.from(context))

    companion object {
        const val DATE_FORMAT = "%d-%02d-%02d"
        const val TIME_FORMAT = "%02d:%02d"
    }

    init {
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        setContentView(binding.root)
        setupClickListeners()
    }

    override fun setupClickListeners() {
        binding.btnCancel.setOnClickListener { hideBottomSheet() }
        binding.btnSuccess.setOnClickListener {
            val schedule = getScheduleFromInput()
            presenter.onSuccessButtonClick(schedule)
            hideBottomSheet() }
        binding.etDate.setOnClickListener { showDatePickerDialog(binding.etDate) }
        binding.etStartTime.setOnClickListener { showTimePickerDialog(binding.etStartTime) }
        binding.etEndTime.setOnClickListener { showTimePickerDialog(binding.etEndTime) }
        binding.switchRepeat.setOnClickListener {
            val isChecked = binding.switchRepeat.isChecked
            if (isChecked) { binding.llRepeat.visibility = View.VISIBLE }
            else { binding.llRepeat.visibility = View.GONE }
        }
        binding.etFinishDate.setOnClickListener{ showDatePickerDialog(binding.etFinishDate)}
    }

    override fun getScheduleFromInput(): Schedule {
        val name = binding.etScheduleName.text.toString()
        val place = binding.etScheduleLocation.text.toString()
        val date = binding.etDate.text.toString()
        val startTime = binding.etStartTime.text.toString()
        val endTime = binding.etEndTime.text.toString()
        val isGroup = false

        return Schedule(
            name = name,
            place = place,
            date = date,
            startTime = startTime,
            endTime = endTime,
            isGroup = isGroup
        )
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
            context, DatePickerDialog.OnDateSetListener
            { _, y, m, d ->
                editText.hint = String.format(DATE_FORMAT, y, m + 1, d) },
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
                editText.hint = String.format(TIME_FORMAT, hourOfDay, minute) },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
    }

}

