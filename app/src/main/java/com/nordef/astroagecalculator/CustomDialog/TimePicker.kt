package com.nordef.astroagecalculator.CustomDialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TimePicker
import com.nordef.astroagecalculator.MainActivity
import com.nordef.astroagecalculator.R
import java.util.*

class TimePicker : DialogFragment(), View.OnClickListener {

    lateinit var timePicker: TimePicker
    lateinit var btn_ok: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dialog.window.requestFeature(Window.FEATURE_NO_TITLE);
        val view = inflater.inflate(R.layout.layout_time_picker, container, false)

        timePicker = view.findViewById(R.id.time_picker)
        btn_ok = view.findViewById(R.id.btn_ok)
        btn_ok.setOnClickListener(this)

        return view
    }

    override fun onClick(view: View?) {
        this.dismiss()

        val hour = timePicker.currentHour
        val minute = timePicker.currentMinute

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val millis = calendar.timeInMillis
        (activity as MainActivity).setTime(millis)
    }
}