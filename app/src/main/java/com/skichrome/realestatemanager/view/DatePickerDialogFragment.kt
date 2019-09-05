package com.skichrome.realestatemanager.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.lang.ref.WeakReference
import java.util.*

class DatePickerDialogFragment(
    private val callback: WeakReference<DatePickerListener>,
    private val tag: Int,
    private val cal: Calendar
) :
    DialogFragment(), DatePickerDialog.OnDateSetListener
{
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_MONTH]
        val month = calendar[Calendar.MONTH]
        val year = calendar[Calendar.YEAR]

        val dpd = DatePickerDialog(context!!, this, year, month, day)

        if (tag == 1)
            dpd.datePicker.minDate = cal.time.time

        return dpd
    }

    override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, day: Int)
    {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        callback.get()?.onDateSet(calendar, tag)
    }

    interface DatePickerListener
    {
        fun onDateSet(calendar: Calendar, tag: Int)
    }
}