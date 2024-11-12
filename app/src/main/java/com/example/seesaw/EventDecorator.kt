package com.example.seesaw

import android.app.Activity
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class EventDecorator(context: Activity?,  eventList: List<CalendarDay>):
    DayViewDecorator {
    private val drawable : Drawable?
    val eventList = eventList

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return eventList.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.setBackgroundDrawable(drawable!!)
    }

    init {
        drawable = ContextCompat.getDrawable(context!!, R.drawable.cal)

    }
}