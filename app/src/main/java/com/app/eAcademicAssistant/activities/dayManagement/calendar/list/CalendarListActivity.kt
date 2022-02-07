package com.app.eAcademicAssistant.activities.dayManagement.calendar.list

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.activities.dayManagement.calendar.create.CalendarCreateActivity
import com.app.eAcademicAssistant.activities.dayManagement.calendar.edit.CalendarEditActivity
import com.app.eAcademicAssistant.adapters.CalendarListRvAdapter
import com.app.eAcademicAssistant.componants.CirclePagerIndicatorDecoration
import com.app.eAcademicAssistant.model.YearModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.teamayka.smsadminsuper.ui.ttandsc.calendar.YearPickerAdapter
import com.teamayka.smsadminsuper.utils.CalendarUtils
import java.util.*

class CalendarListActivity : AppCompatActivity() {

    private lateinit var fabCreate: FloatingActionButton
    private lateinit var fabEdit: FloatingActionButton
    private lateinit var tvYear: TextView
    private lateinit var rv: RecyclerView
    var keySemId = ""

    var years = ArrayList<YearModel>()

    var currentYearPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_calendar)
        init()

        keySemId = intent.getStringExtra("sem_id").toString()

        rv.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        val pager = PagerSnapHelper()
        pager.attachToRecyclerView(rv)

        addYears()

        getDays()

        tvYear.setOnClickListener {
            val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar)

            val v = LayoutInflater.from(this).inflate(R.layout.layout_dialog_year_picker, null)
            val rvDate: RecyclerView = v.findViewById(R.id.rvDate)
            val tvOk: TextView = v.findViewById(R.id.tvOk)

            val rvDateSnapHelper = LinearSnapHelper()
            rvDate.layoutManager = LinearLayoutManager(this@CalendarListActivity)
            rvDate.adapter = YearPickerAdapter(years, rvDateSnapHelper, rvDate)
            rvDateSnapHelper.attachToRecyclerView(rvDate)

            // set snap item at first time
            rvDate.post { rvDate.smoothScrollBy(0, 1) }

//            val smoothScroller = CenterSmoothScroller(rvDate.context)
//            smoothScroller.targetPosition = currentYearPosition + 1
//            rvDate.layoutManager?.startSmoothScroll(smoothScroller)

            dialog.setContentView(v)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

            tvOk.setOnClickListener {
                dialog.cancel()

                val layoutManager = rvDate.layoutManager!!
                val snapView = rvDateSnapHelper.findSnapView(layoutManager)
                val snapPosition = layoutManager.getPosition(snapView!!)
                currentYearPosition = snapPosition - 1
                tvYear.text = years[currentYearPosition].year.toString()
                val months = getMonths()
                val adapter = CalendarListRvAdapter(
                    years[currentYearPosition].year,
                    months,
                    keySemId,
                    this@CalendarListActivity
                )
                rv.adapter = adapter
                for (i in 0 until rv.itemDecorationCount) {
                    rv.removeItemDecorationAt(i)
                }
                rv.addItemDecoration(CirclePagerIndicatorDecoration())
                rv.scrollToPosition(CalendarUtils.getCurrentMonthIndex() - 1)
            }

            dialog.show()
        }

        fabCreate.setOnClickListener {
            val i = Intent(this, CalendarCreateActivity::class.java)
            i.putExtra("sem_id", keySemId)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
        }

        fabEdit.visibility = View.GONE

        fabEdit.setOnClickListener {
            val i = Intent(this, CalendarEditActivity::class.java)
            i.putExtra("sem_id", keySemId)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.putExtra("KEY_YEAR_POSITION", currentYearPosition)

            val layoutManager = rv.layoutManager!!
            val snapView = pager.findSnapView(layoutManager)
            val snapPosition = layoutManager.getPosition(snapView!!)
            i.putExtra("KEY_MONTH_POSITION", snapPosition)

            startActivity(i)
        }
    }

    private fun getDays() {
        val year = getCurrentYear()
        currentYearPosition = year.position
        tvYear.text = year.year.toString()
        val months = getMonths()
        val adapter = CalendarListRvAdapter(year.year, months, keySemId,this@CalendarListActivity)
        rv.adapter = adapter
        rv.scrollToPosition(CalendarUtils.getCurrentMonthIndex() - 1)
    }

    private fun addYears() {
        for (i in 1970 until 2100) {
            years.add(YearModel(years.size, i))
        }
    }

    private fun getCurrentYear(): YearModel {
        for (i in 0 until years.size) {
            if (years[i].year == CalendarUtils.getCurrentYear()) {
                return years[i]
            }
        }
        return YearModel(0, CalendarUtils.getCurrentYear())
    }

    override fun onResume(){
        super.onResume()
        getDays()
    }

    private fun getMonths(): ArrayList<Int> {
        val months = ArrayList<Int>()
        months.add(0)
        months.add(1)
        months.add(2)
        months.add(3)
        months.add(4)
        months.add(5)
        months.add(6)
        months.add(7)
        months.add(8)
        months.add(9)
        months.add(10)
        months.add(11)
        return months
    }

    private fun init() {
        fabCreate = findViewById(R.id.fabCreate)
        fabEdit = findViewById(R.id.fabEdit)
        tvYear = findViewById(R.id.tvYear)
        rv = findViewById(R.id.rv)
    }
}