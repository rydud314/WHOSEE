//package com.example.seesaw
//
//import android.os.Parcel
//import android.os.Parcelable
//
//data class NameCardData(
//    val name: String,
//    val job: String,
//    val introduction: String,
//    val workplace: String,
//    val age: String,
//    val gender: String,
//    val annual: String,
//    val call: String,
//    val email: String,
//    val SNS: String,
//    val portpolio: String
//) : Parcelable {
//    constructor(parcel: Parcel) : this(
//        parcel.readString() ?: "",
//        parcel.readString() ?: "",
//        parcel.readString() ?: "",
//        parcel.readString() ?: "",
//        parcel.readString() ?: "",
//        parcel.readString() ?: "",
//        parcel.readString() ?: "",
//        parcel.readString() ?: "",
//        parcel.readString() ?: "",
//        parcel.readString() ?: "",
//        parcel.readString() ?: ""
//    )
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeString(name)
//        parcel.writeString(job)
//        parcel.writeString(introduction)
//        parcel.writeString(workplace)
//        parcel.writeString(age)
//        parcel.writeString(gender)
//        parcel.writeString(annual)
//        parcel.writeString(call)
//        parcel.writeString(email)
//        parcel.writeString(SNS)
//        parcel.writeString(portpolio)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<NameCardData> {
//        override fun createFromParcel(parcel: Parcel): NameCardData {
//            return NameCardData(parcel)
//        }
//
//        override fun newArray(size: Int): Array<NameCardData?> {
//            return arrayOfNulls(size)
//        }
//    }
//}
