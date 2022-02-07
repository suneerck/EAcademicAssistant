package com.app.eAcademicAssistant.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class DayModel2(
    var id : String,
    var isSelected : Boolean,
    var isEnabled : Boolean,
    var isWorkingDay : Int,
    val date: Long,
    var isCheked : Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeByte(if (isEnabled) 1 else 0)
        parcel.writeInt(isWorkingDay)
        parcel.writeLong(date)
        parcel.writeByte(if (isEnabled) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DayModel2> {
        override fun createFromParcel(parcel: Parcel): DayModel2 {
            return DayModel2(parcel)
        }

        override fun newArray(size: Int): Array<DayModel2?> {
            return arrayOfNulls(size)
        }
    }
}