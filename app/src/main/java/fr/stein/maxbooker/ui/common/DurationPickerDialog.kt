package fr.stein.maxbooker.ui.common

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import fr.stein.maxbooker.R
import java.time.Duration

class DurationPickerDialog(
    private val showDaysPicker: Boolean = true,
    private val showHoursPicker: Boolean = true,
    private val showMinutesPicker: Boolean = true,
    private val showSecondsPicker: Boolean = true,
    private val defaultDaysValue: Int = 0,
    private val defaultHoursValue: Int = 0,
    private val defaultMinutesValue: Int = 0,
    private val defaultSecondsValue: Int = 0,
    private val onAccept: (DurationPickerDialog) -> Unit = {}): DialogFragment() {

        private lateinit var daysPicker: NumberPicker
        private lateinit var hoursPicker: NumberPicker
        private lateinit var minutesPicker: NumberPicker
        private lateinit var secondsPicker: NumberPicker
        private lateinit var dialog: AlertDialog


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)
            // Get the layout inflater.
            val inflater = requireActivity().layoutInflater;

            // Inflate and set the layout for the dialog.
            // Pass null as the parent view because it's going in the dialog
            // layout.
            val view = inflater.inflate(R.layout.dialog_duration_picker, null)
            builder.setView(view)
                // Add action buttons.
                .setPositiveButton("Accepter") { _, _ ->
                    onAccept(this)
                }
                .setNegativeButton("Annuler") { _, _ ->
                    getDialog()?.cancel()
                }

            daysPicker = view.findViewById(R.id.dialog_duration_picker_days_picker)
            val daysText = view.findViewById<TextView>(R.id.dialog_duration_picker_days_text)
            daysPicker.minValue = 0
            daysPicker.maxValue = 365
            daysPicker.value = defaultDaysValue
            if (!showDaysPicker) {daysPicker.visibility = View.GONE;daysText.visibility = View.GONE}
            daysPicker.setOnValueChangedListener(pickerValueChangeListener)

            hoursPicker = view.findViewById(R.id.dialog_duration_picker_hours_picker)
            val hoursText = view.findViewById<TextView>(R.id.dialog_duration_picker_hours_text)
            hoursPicker.minValue = 0
            hoursPicker.maxValue = 23
            hoursPicker.value = defaultHoursValue
            if (!showHoursPicker) {hoursPicker.visibility = View.GONE;hoursText.visibility = View.GONE}
            hoursPicker.setOnValueChangedListener(pickerValueChangeListener)

            minutesPicker = view.findViewById(R.id.dialog_duration_picker_minutes_picker)
            val minutesText = view.findViewById<TextView>(R.id.dialog_duration_picker_minutes_text)
            minutesPicker.minValue = 0
            minutesPicker.maxValue = 59
            minutesPicker.value = defaultMinutesValue
            if (!showMinutesPicker) {minutesPicker.visibility = View.GONE;minutesText.visibility = View.GONE}
            minutesPicker.setOnValueChangedListener(pickerValueChangeListener)

            secondsPicker = view.findViewById(R.id.dialog_duration_picker_seconds_picker)
            val secondsText = view.findViewById<TextView>(R.id.dialog_duration_picker_seconds_text)
            secondsPicker.minValue = 0
            secondsPicker.maxValue = 59
            secondsPicker.value = defaultSecondsValue
            if (!showSecondsPicker) {secondsPicker.visibility = View.GONE;secondsText.visibility = View.GONE}
            secondsPicker.setOnValueChangedListener(pickerValueChangeListener)


            dialog = builder.create()
            if (defaultDaysValue == 0 && defaultHoursValue == 0 && defaultMinutesValue == 0 && defaultSecondsValue == 0){
                dialog.setOnShowListener {
                    dialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled = false
                }
            }

            return dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun getSelectedDuration(): Duration{
        return Duration.ofDays(daysPicker.value.toLong()) +
                Duration.ofHours(hoursPicker.value.toLong()) +
                Duration.ofMinutes(minutesPicker.value.toLong()) +
                Duration.ofSeconds(secondsPicker.value.toLong())
    }

    private val pickerValueChangeListener = NumberPicker.OnValueChangeListener { _, _, _ ->
        val selectedDuration = getSelectedDuration()
        dialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled =
            !selectedDuration.isZero && !selectedDuration.isNegative
    }
}