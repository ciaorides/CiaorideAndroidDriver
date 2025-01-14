package com.ciaorides.ciaorides.utils

import com.ciaorides.ciaorides.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar

object DateUtils {

    fun datePicker(selectedDate: Long?): MaterialDatePicker<Long> {
        // Get the current year
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDom = calendar.get(Calendar.DAY_OF_MONTH)

        // Calculate the maximum selectable date (2006-12-25)
        calendar.set(Calendar.YEAR, currentYear - 18)
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.DAY_OF_MONTH, currentDom)
        val maxDate = calendar.timeInMillis

        // Build calendar constraints
        val mDateValidator: DateValidator = DateValidatorPointBackward.before(maxDate)
        val mConstraintsBuilder: CalendarConstraints.Builder =
            CalendarConstraints.Builder().setEnd(maxDate).setValidator(mDateValidator)

        // Build date picker dialog
        val mDateBuilder: MaterialDatePicker.Builder<Long> = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(selectedDate ?: maxDate)
            .setCalendarConstraints(mConstraintsBuilder.build())
            .setTheme(R.style.MaterialCalendarDateTheme)
        return mDateBuilder.build()
    }
}