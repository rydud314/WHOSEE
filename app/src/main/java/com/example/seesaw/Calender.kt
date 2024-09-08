package com.example.seesaw

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Collections

class Calender : AppCompatActivity() {

    private lateinit var btn_add_schedule : Button

    private val applicationName = "Google Calendar API Java Quickstart"
    /**
     * Global instance of the JSON factory.
     */
    private val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
    /**
     * Directory to store authorization tokens for this application.
     */
    private val tokensDirectoryPath = "tokens"

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private val scopes: List<String> = Collections.singletonList(CalendarScopes.CALENDAR_READONLY)
    private val credentialsFilePath = "/credentials.json"

    /**
     * Creates an authorized Credential object.
     *
     * @param httpTransport The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    @Throws(IOException::class)
    private fun getCredentials(httpTransport: NetHttpTransport): Credential {
        // Load client secrets.
        val inputStream: InputStream? = Calendar::class.java.getResourceAsStream(credentialsFilePath)
        if (inputStream == null) {
            throw FileNotFoundException("Resource not found: $credentialsFilePath")
        }
        val clientSecrets = GoogleClientSecrets.load(jsonFactory, InputStreamReader(inputStream))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
            httpTransport, jsonFactory, clientSecrets, scopes)
            .setDataStoreFactory(FileDataStoreFactory(File(tokensDirectoryPath)))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user") //returns an authorized Credential object.
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        Log.d(ContentValues.TAG, "캘린더 = 입장")

        btn_add_schedule = findViewById(R.id.btn_add_schedule)

        // Build a new authorized API client service.
        val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val calendarService =
            Calendar.Builder(httpTransport, jsonFactory, getCredentials(httpTransport))
                .setApplicationName(applicationName)
                .build()

        // List the next 10 events from the primary calendar.
        val currentTime = DateTime(System.currentTimeMillis())
        val events: Events = calendarService.events().list("primary")
            .setMaxResults(10)
            .setTimeMin(currentTime)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute()
        val eventItems: List<Event> = events.items
        if (eventItems.isEmpty()) {
            println("No upcoming events found.")
        } else {
            println("Upcoming events")
            for (event in eventItems) {
                var start: DateTime? = event.start.dateTime
                if (start == null) {
                    start = event.start.date
                }
                println("${event.summary} ($start)")
            }
        }

        btn_add_schedule.setOnClickListener {
            val intent = Intent(this, AddSchedule::class.java)
            startActivity(intent)
        }
    }
}