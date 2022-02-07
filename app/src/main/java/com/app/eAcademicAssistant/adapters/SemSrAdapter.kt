package com.app.eAcademicAssistant.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.app.eAcademicAssistant.R
import com.app.eAcademicAssistant.model.SemCourseMenuModel
import com.app.eAcademicAssistant.model.srModel

class SemSrAdapter (
    private val mActivity: Activity,
    private val list: ArrayList<SemCourseMenuModel>
) : BaseAdapter() {
    override fun getCount(): Int {
        return list.size
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v = LayoutInflater.from(mActivity).inflate(R.layout.item_sr_default_first_item, parent, false)
        val tv = v.findViewById<TextView>(R.id.tv)
        tv.text = list[position].semName
        return v
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v = LayoutInflater.from(mActivity).inflate(R.layout.item_sr_default_list_item, parent, false)
        val tv = v.findViewById<TextView>(R.id.tv)
        if (position==0){
            tv.visibility= View.GONE
        }else{
            tv.visibility= View.VISIBLE
        }
        tv.text = list[position].semName
        return v
    }

    override fun getItem(position: Int): Any {
        return 0
    }


    override fun getItemId(position: Int): Long {
        return 0
    }


}