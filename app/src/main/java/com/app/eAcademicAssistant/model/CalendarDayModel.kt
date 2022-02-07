package com.app.eAcademicAssistant.model

import java.util.*

class CalendarDayModel(
    var isSelected : Boolean,
    var isEnabled : Boolean,
    var isWorkingDay : Int,
    val date: Date,
    val event: Any?
)