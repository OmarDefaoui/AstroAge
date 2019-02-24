package com.nordef.astroagecalculator.calculation

import java.util.*

class RealAgeCalculation {
    private var startYear: Int = 0
    private var startMonth: Int = 0
    private var startDay: Int = 0
    private var endYear: Int = 0
    private var endMonth: Int = 0
    private var endDay: Int = 0
    private var resYear: Int = 0
    private var resMonth: Int = 0
    private var resDay: Int = 0
    private var end: Calendar? = null

    fun getCurrentDate(): String {
        end = Calendar.getInstance()
        endYear = end!!.get(Calendar.YEAR)
        endMonth = end!!.get(Calendar.MONTH)
        endMonth++
        endDay = end!!.get(Calendar.DAY_OF_MONTH)
        return endDay.toString() + ":" + endMonth + ":" + endYear
    }

    fun setDateOfBirth(sYear: Int, sMonth: Int, sDay: Int) {
        startYear = sYear
        startMonth = sMonth
        startDay = sDay
    }

    fun calcualteYear(): Int {
        resYear = endYear - startYear

        if (endMonth < startMonth) {

            resYear--

        }


        return resYear

    }

    fun calcualteMonth(): Int {
        if (endMonth >= startMonth) {
            resMonth = endMonth - startMonth
        } else {
            resMonth = endMonth - startMonth
            resMonth = 12 + resMonth
            resYear--
        }

        return resMonth
    }

    fun calcualteDay(): Int {

        if (endDay >= startDay) {
            resDay = endDay - startDay
        } else {
            resDay = endDay - startDay
            resDay = 30 + resDay
            if (resMonth == 0) {
                resMonth = 11
                resYear--
            } else {
                resMonth--
            }

        }

        return resDay
    }
}