package com.example.meals_schdueler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meals_schdueler.dummy.DailySchedule
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class Weekly_Schedule_Choose_RecyclerViewAdapter(

    var weeklyValues: TreeMap<String, WeeklySchedule>,
    var dailyValues: TreeMap<String, DailySchedule>,
    var recipes:HashMap<String, Recipe>,
    weeklyId: ArrayList<Int>?,
    childFragmentManager: FragmentManager
) : RecyclerView.Adapter<Weekly_Schedule_Choose_RecyclerViewAdapter.ViewHolder>() {

    private var mValues: TreeMap<String, WeeklySchedule> = weeklyValues
    private var mValuesDaily: TreeMap<String, DailySchedule> = dailyValues

    private var weeklyList: ArrayList<WeeklySchedule> = ArrayList()

    //    private var recipe = recipes
    private var childFragmentManager = childFragmentManager
    private var numOfWeekly = 1
    private var weeklyId = weeklyId


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Weekly_Schedule_Choose_RecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.weekly_choose, parent, false)

        for(i in weeklyValues){
            weeklyList.add(i.value)
        }
        return ViewHolder(view)
    }

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item: WeeklySchedule = weeklyList[position]!! // each item postion
        holder.mItem = item
        if (numOfWeekly < mValues.size) {
            holder.numOfDaily.setText(numOfWeekly++.toString())
        }
        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)


        holder.weeklyInfo.setOnClickListener {

            var dailyList: ArrayList<DailySchedule> = ArrayList()
            var dailyArr = item.dailyIds.splitIgnoreEmpty(" ")
            var j = 0
            for (i in dailyArr) {

                dailyList.add(UserPropertiesSingelton.getInstance()!!.getUserDaily()!!.get(i)!!)
            }

            var dialog = WeeklyDialogInfo(
                dailyList,
                item.numOfDay,
                item.dailyIds,
                item.totalCost,
                position + 1
            )

            dialog.show(childFragmentManager, "WeeklyDialgInfo")

        }


        holder.choose.setChecked(holder.arr[position]);
        holder.choose.setOnClickListener() {

//
            if (holder.choose.isChecked == true && (weeklyId!!.isEmpty())) {
                weeklyId!!.add(item.weeklyId)
                holder.arr[position] = true

            } else if (holder.choose.isChecked == false) {
                if (weeklyId!!.contains(position)) {
                    holder.arr[position] = false
                    weeklyId!!.remove(position)

                }

            }


        }


    }


    fun setmValues(mValues: TreeMap<String, WeeklySchedule>) {
        this.mValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val idView: TextView = view.findViewById(R.id.item_number)
        //val contentView: TextView = view.findViewById(R.id.content)

        var weeklyInfo: Button = view.findViewById(R.id.buttonInfo)
        var cart: ImageView = view.findViewById(R.id.imageViewCart)
        var numOfDaily: TextView = view.findViewById(R.id.numOfWeeklyTextView)
        var choose: CheckBox = view.findViewById(R.id.WeeklyCheckBox)
        val arr = Array(mValues.size, { i -> false })
        lateinit var mItem: WeeklySchedule


        override fun toString(): String {
            return super.toString()
        }


    }

    override fun getItemCount(): Int = weeklyValues.size

}