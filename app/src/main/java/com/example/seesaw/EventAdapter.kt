package com.example.seesaw;

import android.content.Context;
import android.content.Intent
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView
import android.widget.TextView;
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.api.services.calendar.model.Event;
import java.text.SimpleDateFormat
import java.util.Locale

public class EventAdapter(private val context: Context, private val events: List<Event>) : BaseAdapter() {


    override fun getCount(): Int {
        return events.size
    }

    override fun getItem(position: Int): Any {
        return events[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.listitem_event, parent, false)

        val eventTitle = view.findViewById<TextView>(R.id.eventTitle)
        val eventStart = view.findViewById<TextView>(R.id.eventTime)


        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)

        val event = events[position]
        val startTime = event.start.dateTime ?: event.start.date
        val endTime = event.end.dateTime ?: event.end.date

        eventTitle.text = event.summary ?: "No Title"
        eventStart.text = "${dateFormat.format(startTime.value)} ~ ${dateFormat.format(endTime.value)}"

        return view
    }





}
