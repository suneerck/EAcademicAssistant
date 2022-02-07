package com.app.eAcademicAssistant.utills

object URLUtils1 {
    private const val PORT_API = 9000


    const val URL_ROOT = "http://178.128.177.153"

    private const val URL_BASE = "$URL_ROOT:$PORT_API/"




    // TIME TABLE AND SCHOOL CALENDAR
    const val URL_ADMIN_CALENDAR_GET_DAY_LIST = "${URL_BASE}admin_calendar_get_day_list"

    const val URL_ADMIN_CALENDAR_ADD_DAY = "${URL_BASE}admin_calendar_add_day"

}