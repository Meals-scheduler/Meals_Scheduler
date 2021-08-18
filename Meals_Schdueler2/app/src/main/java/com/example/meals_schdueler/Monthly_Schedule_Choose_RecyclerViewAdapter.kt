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

class Monthly_Schedule_Choose_RecyclerViewAdapter(

    var monthlyValues: TreeMap<String, MonthlySchedule>,
    monthlyId: ArrayList<Int>?,
    childFragmentManager: FragmentManager

) : RecyclerView.Adapter<Monthly_Schedule_Choose_RecyclerViewAdapter.ViewHolder>()  {


    private var mValues: TreeMap<String, MonthlySchedule> = monthlyValues
    // private var mValuesDaily: TreeMap<String, DailySchedule> = dailyValues

    private var monthylList: ArrayList<MonthlySchedule> = ArrayList()

    //    private var recipe = recipes
    private var childFragmentManager = childFragmentManager
    private var numOfMonthly = 1
    private var monthlyId = monthlyId


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Monthly_Schedule_Choose_RecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.monthly_choose, parent, false)

        for(i in monthlyValues){
            monthylList.add(i.value)
        }
        return ViewHolder(view)
    }

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }



    override fun onBindViewHolder(holder: Monthly_Schedule_Choose_RecyclerViewAdapter.ViewHolder, position: Int) {
        var item: MonthlySchedule = monthylList[position]!! // each item postion
        holder.mItem = item
        if (numOfMonthly < mValues.size) {
            holder.numOfMonthly.setText(numOfMonthly++.toString())
        }
        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)


        holder.monthlyInfo.setOnClickListener {

            var weeklyList: ArrayList<WeeklySchedule> = ArrayList()
            var dailyArr = item.weeklyIds.splitIgnoreEmpty(" ")
            var j = 0
            for (i in dailyArr) {

                weeklyList.add(UserPropertiesSingelton.getInstance()!!.getUserWeekly()!!.get(i)!!)
            }

            var dialog = MonthlyDialogInfo(
                weeklyList,
                item.numOfWeek,
                item.weeklyIds,
                item.totalCost,
                position + 1
            )

            dialog.show(childFragmentManager, "WeeklyDialgInfo")

        }


        holder.choose.setChecked(holder.arr[position]);
        holder.choose.setOnClickListener() {

//
            if (holder.choose.isChecked == true && (monthlyId!!.isEmpty())) {
                monthlyId!!.add(item.monthlyId)
                holder.arr[position] = true

            } else if (holder.choose.isChecked == false) {
                if (monthlyId!!.contains(position)) {
                    holder.arr[position] = false
                    monthlyId!!.remove(position)

                }

            }


        }


    }


    fun setmValues(mValues: TreeMap<String, MonthlySchedule>) {
        this.mValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val idView: TextView = view.findViewById(R.id.item_number)
        //val contentView: TextView = view.findViewById(R.id.content)

        var monthlyInfo: Button = view.findViewById(R.id.buttonInfo)
        var cart: ImageView = view.findViewById(R.id.imageViewCart)
        var numOfMonthly: TextView = view.findViewById(R.id.numOfMonthlyTextView)
        var choose: CheckBox = view.findViewById(R.id.WeeklyCheckBox)
        val arr = Array(mValues.size, { i -> false })
        lateinit var mItem: MonthlySchedule


        override fun toString(): String {
            return super.toString()
        }


    }

    override fun getItemCount(): Int = monthlyValues.size


}