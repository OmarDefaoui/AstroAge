package com.nordef.astroagecalculator.CustomDialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.DatePicker
import com.nordef.astroagecalculator.MainActivity
import com.nordef.astroagecalculator.R
import java.util.*

class DatePicker : DialogFragment(), View.OnClickListener {

    lateinit var datePicker: DatePicker
    lateinit var btn_ok: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dialog.window.requestFeature(Window.FEATURE_NO_TITLE);
        val view = inflater.inflate(R.layout.layout_date_picker, container, false)

        datePicker = view.findViewById(R.id.date_picker)
        btn_ok = view.findViewById(R.id.btn_ok)
        btn_ok.setOnClickListener(this)

        return view
    }

    override fun onClick(view: View?) {
        this.dismiss()

        val day = datePicker.dayOfMonth
        val month = datePicker.month
        val year = datePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)

        val millis = calendar.timeInMillis
        (activity as MainActivity).setDate(millis, day, month + 1, year)
    }
}