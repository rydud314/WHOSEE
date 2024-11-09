package com.example.seesaw

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(
    val title: String,
    val startDate: String,
    val startTime: String,
    val endDate: String,
    val endTime: String,
    val participant: String,
    val description: String

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!

    )

    override fun describeContents(): Int {
        return 0
    }

}