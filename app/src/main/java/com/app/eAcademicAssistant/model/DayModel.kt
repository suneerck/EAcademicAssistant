package com.app.eAcademicAssistant.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class DayModel(
    var id : String,
    var isSelected : Boolean,
    var isEnabled : Boolean,
    var isWorkingDay : Int,
    val date: Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeByte(if (isEnabled) 1 else 0)
        parcel.writeInt(isWorkingDay)
        parcel.writeLong(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DayModel> {
        override fun createFromParcel(parcel: Parcel): DayModel {
            return DayModel(parcel)
        }

        override fun newArray(size: Int): Array<DayModel?> {
            return arrayOfNulls(size)
        }
    }
}