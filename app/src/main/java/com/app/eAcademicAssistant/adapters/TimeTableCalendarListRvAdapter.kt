package com.app.eAcademicAssistant.adapters

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.timeTableManagement.TimeTableCalendarListActivity
import com.app.eAcademicAssistant.activities.timeTableManagement.TimeTableSelectSubjectsActivity
import com.app.eAcademicAssistant.model.CalendarModel
import com.app.eAcademicAssistant.model.DayModel
import com.app.eAcademicAssistant.model.DayModel2
import com.app.eAcademicAssistant.utills.DatabaseUtils
import com.app.eAcademicAssistant.utills.OkHttpUtils
import com.app.eAcademicAssistant.utills.URLUtils
import com.teamayka.smsadminsuper.utils.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class TimeTableCalendarListRvAdapter(
    private var year: Int,
    private var semCourseId: String,
    private val months: ArrayList<Int>,
    internal val context: TimeTableCalendarListActivity
) :
    RecyclerView.Adapter<TimeTableCalendarListRvAdapter.ViewHolder>() {

    private var callGetData: Call? = null
    private var callCreate: Call? = null

    val days = ArrayList<DayModel2>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_rv_calendar, p0, false)
        return ViewHolder(v)
    }

//    private val list = listOf(months.last()) + months + listOf(months.first())

    override fun getItemCount(): Int {
        return months.size
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, months[holder.adapterPosition])

        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1

        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, months[holder.adapterPosition])
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 1)
        cal.set(Calendar.MINUTE, 1)
        cal.set(Calendar.SECOND, 1)
        val startTime = cal.timeInMillis // start of month
        cal.add(
            Calendar.MONTH,
            1
        ) // add one month to calendar. so the end time will be start of next month
        cal.add(Calendar.DAY_OF_MONTH, -1)
        cal.set(Calendar.HOUR_OF_DAY, 24)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val endTime = cal.timeInMillis // end of month

        getData(holder, calendar, startTime, endTime, holder.adapterPosition)

        holder.tvCreate.setOnClickListener {
            val v = LayoutInflater.from(context)
                .inflate(R.layout.layout_dialog_loading, null)
            val builder = AlertDialog.Builder(context)
            builder.setCancelable(false)
            builder.setView(v)

            val dialog = builder.create()
            dialog.show()
            dialog.setOnDismissListener {
                OkHttpUtils.cancelCalls(callCreate)
            }
            val intent = Intent(context, TimeTableSelectSubjectsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("days", days)
            intent.putExtra("sem_id", context.semId)
            intent.putExtra("course_id", context.courseId)
            context.startActivity(intent)

            dialog.dismiss()

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.rvMonth.layoutManager = GridLayoutManager(holder.itemView.context, 7)
        holder.rvMonth.removeItemDecoration(itemDecorator)
        holder.rvMonth.addItemDecoration(itemDecorator)
        holder.tvMonth.text = getMonth(holder.adapterPosition)
    }

    private fun getMonth(position: Int): String {
        return when (position) {
            0 -> context.getString(R.string.january)
            1 -> context.getString(R.string.february)
            2 -> context.getString(R.string.march)
            3 -> context.getString(R.string.april)
            4 -> context.getString(R.string.may)
            5 -> context.getString(R.string.jun)
            6 -> context.getString(R.string.july)
            7 -> context.getString(R.string.august)
            8 -> context.getString(R.string.september)
            9 -> context.getString(R.string.october)
            10 -> context.getString(R.string.november)
            11 -> context.getString(R.string.december)
            else -> context.getString(R.string.nil)
        }
    }

    private fun getData(
        holder: ViewHolder,
        calendar: Calendar,
        startTime: Long,
        endTime: Long,
        adapterPosition: Int
    ) {
        holder.tvCreate.isEnabled = false
        holder.pbLoading.visibility = View.VISIBLE
        holder.rvMonth.visibility = View.GONE
        holder.tvRetry.visibility = View.GONE

        holder.tvRetry.setOnClickListener {
            getData(
                holder,
                calendar,
                startTime,
                endTime,
                adapterPosition
            )
        }

        val jaStatus = JSONArray()
        jaStatus.put(1)

        val arraySemCourseId = JSONArray()
        arraySemCourseId.put(semCourseId)

        val jsonObject = JSONObject()
        jsonObject.put("userId", DatabaseUtils.getInstance(context).getUser()?.userId)
        jsonObject.put("arraySemCourseLinkingId", arraySemCourseId)

        val data = JSONObject()
        data.put("data", jsonObject)

        android.util.Log.e("_____________req", "getDays: $data")

        val body = MultipartBody.Builder()
        body.setType(MultipartBody.FORM)
        body.addFormDataPart("json_data", data.toString())

        val request = Request.Builder()
            .url(URLUtils.URL_SEM_DAY_LINKING_LIST)
            .post(body.build())
            .build()

        val client = OkHttpUtils.getOkHttpClient()
        callGetData = client.newCall(request)
        callGetData?.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                android.util.Log.e("___________onFailure", "getDays")
                if (call == null || call.isCanceled)
                    return
                context.runOnUiThread {
                    holder.pbLoading.visibility = View.GONE
                    holder.rvMonth.visibility = View.GONE
                    holder.tvRetry.visibility = View.VISIBLE
                }
            }

            override fun onResponse(call: Call?, response: Response?) {
                if (call == null || call.isCanceled)
                    return
                val resp = response?.body()?.string()
                Log.e("___________resp "," getDays :$resp")
                try {
                val jo = JSONObject(resp)
                when (response?.code()) {
                    OkHttpUtils.STATUS_OK -> {
                        val joData = jo.getJSONObject("data")
                        val jaList = joData.getJSONArray("list")
                        val list = ArrayList<CalendarModel>()
                        for (i in 0 until jaList.length()) {
                            val joListItem = jaList.getJSONObject(i)
                            val date = joListItem.getLong("_date")
                            val isWorkingDay = joListItem.getInt("_is_working_day")
                            val status = joListItem.getString("_status")
                            val id = joListItem.getString("_id")

                            list.add(
                                CalendarModel(
                                    date,
                                    isWorkingDay,
                                    status,
                                    id
                                )
                            )
                        }

                        days.clear()
                        while (days.size < 42) {
                            val item = findSelected(list, calendar.time)
                            days.add(
                                DayModel2(
                                    item?.id ?: "",
                                    item != null,
                                    item != null,
                                    item?.isWorkingDay ?: 0,
                                    calendar.time.time,
                                    false
                                )
                            )
                            calendar.add(Calendar.DAY_OF_MONTH, 1)
                        }

                        context.runOnUiThread {
                            holder.pbLoading.visibility = View.GONE
                            holder.rvMonth.visibility = View.VISIBLE
                            holder.tvRetry.visibility = View.GONE

                            holder.tvCreate.isEnabled = true

                            context.runOnUiThread {
                                holder.rvMonth.adapter =
                                    TimeTableCalendarMonthRvAdapter(
                                        year,
                                        months[adapterPosition],
                                        context,
                                        this@TimeTableCalendarListRvAdapter
                                    )
                            }
                        }
                    }
                    OkHttpUtils.STATUS_ERROR -> {
                        val message = jo.getString("message")

                        context.runOnUiThread {
                            holder.pbLoading.visibility = View.GONE
                            holder.rvMonth.visibility = View.GONE
                            holder.tvRetry.visibility = View.VISIBLE
                        }
                    }
                    else -> {
                        context.runOnUiThread {
                            holder.pbLoading.visibility = View.GONE
                            holder.rvMonth.visibility = View.GONE
                            holder.tvRetry.visibility = View.VISIBLE
                        }
                    }
                }
                } catch (e: Exception) {
                    android.util.Log.e("__________exc", "getDays : " + e.message)
                    context.runOnUiThread {
                        holder.pbLoading.visibility = View.GONE
                        holder.rvMonth.visibility = View.GONE
                        holder.tvRetry.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

//    private fun findEvent(
//        list: ArrayList<CalendarModel>,
//        date: Date
//    ): Any? {
////        for (i in 0 until list.size) {
////            val day = DateFormat.format("d", date)
////            if (day == list[i].date)
////                return list[i]
////        }
//        return null
//    }

    private fun findSelected(
        list: ArrayList<CalendarModel>,
        date: Date
    ): CalendarModel? {

        for (i in 0 until list.size) {
            val d = CalendarUtils.millisToDate(date.time.toString());
            val b = CalendarUtils.millisToDate(list[i].date.toString());

            if (d == b) {
                Log.e("________WWW___-_CCC ",": b : $b :d : $d : ${list[i].status}")
                return list[i]
            }
        }
        return null
    }


//    private fun doCreate(
//        dialog: AlertDialog
//    ) {
//
//        val jaData = JSONArray()
//
//        for (i in 0 until days.size) {
//            // days[i].isEnabled is used to identify data from api. if already from api(enabled), add to input list.
//            if (days[i].isEnabled) {
//                val jo = JSONObject()
//                jo.put("id", days[i].id)
//                jo.put("date", days[i].date.time)
//                jo.put("isWorkingDay", days[i].isWorkingDay)
//                jo.put("status", if(days[i].isSelected) 1 else 3)
//                jaData.put(jo)
//            }
//        }
//
//        val joData = JSONObject()
//        joData.put("userId", DatabaseUtils.getInstance(activity).getUser()?.userId)
//        joData.put("arrayData", jaData)
//
//        val data = JSONObject()
//        data.put("data", joData)
//
//        android.util.Log.e("____________req", "_create_batch : $joData")
//
//        val body = MultipartBody.Builder()
//        body.setType(MultipartBody.FORM)
//        body.addFormDataPart("json_data", data.toString())
//
//        val request = Request.Builder()
//            .url(URLUtils.URL_ADMIN_CALENDAR_EDIT_DAY)
//            .post(body.build())
//            .build()
//
//        val client = OkHttpUtils.getOkHttpClient()
//        callCreate = client.newCall(request)
//        callCreate?.enqueue(object : Callback {
//            override fun onFailure(call: Call?, e: IOException?) {
//                android.util.Log.e("___________onFailure", "_create_batch")
//                if (call == null || call.isCanceled)
//                    return
//                activity.runOnUiThread {
//                    dialog.cancel()
//
//                    if (e is UnknownHostException)
//                        SnackBarUtils.showSnackBar(
//                            activity,
//                            activity.getString(R.string.error_message_connect_error)
//                        )
//                    else
//                        SnackBarUtils.showSnackBar(
//                            activity,
//                            activity.getString(R.string.error_message_went_wrong)
//                        )
//                }
//            }
//
//            override fun onResponse(call: Call?, response: Response?) {
//                if (call == null || call.isCanceled)
//                    return
//                val resp = response?.body()?.string()
//                android.util.Log.e("______________resp", "_create_batch : $resp")
//                try {
//                    val jo = JSONObject(resp)
//                    when (response?.code()) {
//                        OkHttpUtils.STATUS_OK -> {
//                            val joData = jo.getJSONObject("data")
//
//                            activity.runOnUiThread {
//                                dialog.cancel()
//                                activity.finish()
//                            }
//                        }
//                        OkHttpUtils.STATUS_ERROR -> {
//                            val message = jo.getString("message")
//
//                            activity.runOnUiThread {
//                                dialog.cancel()
//
//                                SnackBarUtils.showSnackBar(activity, message)
//                            }
//                        }
//                        else -> {
//                            activity.runOnUiThread {
//                                dialog.cancel()
//
//                                SnackBarUtils.showSnackBar(
//                                    activity,
//                                    activity.getString(R.string.error_message_server_error)
//                                )
//                            }
//                        }
//                    }
//                } catch (e: Exception) {
//                    android.util.Log.e("__________exc", "_create_batch : " + e.message)
//                    activity.runOnUiThread {
//                        dialog.cancel()
//
//                        SnackBarUtils.showSnackBar(
//                            activity,
//                            activity.getString(R.string.error_message_server_error)
//                        )
//                    }
//                }
//            }
//        })
//    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvMonth: RecyclerView = itemView.findViewById(R.id.rvMonth)
        val pbLoading: ProgressBar = itemView.findViewById(R.id.pbLoading)
        val tvMonth: TextView = itemView.findViewById(R.id.tvMonth)
        val tvRetry: TextView = itemView.findViewById(R.id.tvRetry)
        val tvCreate: TextView = itemView.findViewById(R.id.tvCreate)
    }

    private val itemDecorator = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.left = 1
            outRect.right = 1
            outRect.top = 1
            outRect.bottom = 1
        }
    }
}