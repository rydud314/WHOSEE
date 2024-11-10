package com.example.seesaw;

import android.content.Context;
import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.services.calendar.model.Event;
import java.text.SimpleDateFormat
import java.util.Locale

public class EventAdapter(private val events: List<Event>,
                          private val context: Context, private val account: GoogleSignInAccount) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventTitle: TextView = itemView.findViewById(R.id.eventTitle)
        val eventDate: TextView = itemView.findViewById(R.id.eventTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        val event = events[position]
        var startTime = ""
        var endTime = ""

        if(event.start.dateTime != null && event.end.dateTime != null){
            startTime = dateFormat.format(event.start.dateTime.value)
            endTime = dateFormat.format(event.end.dateTime.value)
        }
        else{
            startTime = event.start.date.value.toString()
            endTime = event.end.date.value.toString()
        }

        holder.eventTitle.text = event.summary ?: "No Title"
        holder.eventDate.text = "${startTime} ~ ${endTime}"

        holder.itemView.setOnClickListener {
            Log.d(TAG, "캘린더 리사뷰 인텐드 객= ${event.summary}")

            /*
            val sd = event.start.date ?: event.start.dateTime?.toString() ?: "No Start Date"
            val st = event.start.dateTime?.toString() ?: ""

            val ed = event.end.date ?: event.end.dateTime?.toString() ?: "No End Date"
            val et = event.end.dateTime?.toString() ?: ""
             */
            
            val eventBundle = Bundle()
            eventBundle.putString("eventId", event.id?.toString() ?: "No eventId")
            eventBundle.putString("eventTitle", event.summary?.toString() ?: "No title")
            eventBundle.putString("eventStartTime", startTime)
            eventBundle.putString("eventEndTime", endTime)
            eventBundle.putString("eventDescription", event.description?.toString() ?: "")

            //putString("eventStartDate", sd.toString())
            //putString("eventEndDate", ed.toString())

            val account = account //calendar 구글 로그인 계정
            
            val intent = Intent(context, EventDetail::class.java)
            intent.putExtra("eventBundle", eventBundle)
            intent.putExtra("calendarAccount", account)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = events.size

}

