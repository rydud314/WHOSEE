package com.example.seesaw

import android.os.Parcel
import android.os.Parcelable

data class Card(
    val name: String,
    val position: String,
    val workplace: String,
    val email: String,
    val cardId: String,
    val gender: String,
    val imageName: String,
    val introduction: String,
    val job: String,
    val pofol: String,
    val sns: String,
    val tel: String

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(position)
        parcel.writeString(workplace)
        parcel.writeString(email)
        parcel.writeString(cardId)
        parcel.writeString(gender)
        parcel.writeString(imageName)
        parcel.writeString(introduction)
        parcel.writeString(job)
        parcel.writeString(pofol)
        parcel.writeString(sns)
        parcel.writeString(tel)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}
