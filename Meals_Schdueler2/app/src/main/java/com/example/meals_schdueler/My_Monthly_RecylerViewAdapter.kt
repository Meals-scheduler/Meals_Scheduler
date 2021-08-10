package com.example.meals_schdueler

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class My_Monthly_RecylerViewAdapter(

    monthlyValues: ArrayList<MonthlySchedule>,
    weeklyValues: ArrayList<WeeklySchedule>,
    childFragmentManager: FragmentManager,
    context: Context?,

    ) : RecyclerView.Adapter<My_Monthly_RecylerViewAdapter.ViewHolder>() {

    private var monthlyValues: ArrayList<MonthlySchedule> = monthlyValues
    private var weeklyValues: ArrayList<WeeklySchedule> = weeklyValues
    private var monthlyWeekly: HashMap<String, ArrayList<WeeklySchedule>> = HashMap()

    private var childFragmentManager = childFragmentManager

    // private lateinit var recipeList: ArrayList<Recipe>
    private var numOfMonthly = 1
    private var context = context
    private var monthlyToSchedule = -1
    var date = ""

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): My_Monthly_RecylerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_monthly_schedule, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: My_Monthly_RecylerViewAdapter.ViewHolder, position: Int) {
        var item: MonthlySchedule = monthlyValues[position] // each item postion
        holder.mItem = item

        holder.numOfWeekly.setText(numOfMonthly++.toString())

        holder.edit.setOnClickListener {
//            Log.v("Elad1", "FF1" + item.weeklyId.toString())
//            // copying the list not to override it in the edit .
//            var tmpList: ArrayList<WeeklySchedule> = ArrayList()
//            for (i in monthlyWeekly.get(item.weeklyId.toString())!!) {
//                tmpList.add(i)
//            }
//
//            var dialog = EditWeeklyDialog(
//                tmpList,
//                item.numOfDay,
//                item.dailyIds,
//                position + 1,
//                item.weeklyId
//
//            )
//            dialog.show(childFragmentManager, "weeklyEdit")
        }



        holder.info.setOnClickListener {
            // Log.v("Elad1","HH" + weeklyDaily.get(item.weeklyId.toString())!!.get(1).recipeIds)
            Log.v("Elad1", "FF2" + item.monthlyId.toString())
            var dialog = MonthlyDialogInfo(
                monthlyWeekly.get(item.monthlyId.toString())!!,
                item.numOfWeek,
                item.weeklyIds,
                item.totalCost,
                (position + 1)
            )


            dialog.show(childFragmentManager, "WeeklyDialogInfo")


        }

        holder.delete.setOnClickListener {
            // deleteing this weekly from the map that holds for every weekly its daily list
//            monthlyWeekly.remove(item.weeklyId.toString())!!
//
//            var dialog =
//                DeleteAlertDialog(
//                    "",
//                    null,
//                    monthlyValues.get(position).monthlyId,
//                    false,
//                    false,
//                    false,
//                    true
//                )
//            dialog.show(childFragmentManager, "DeleteMonthly")

        }

        holder.date.setOnClickListener {
            monthlyToSchedule = weeklyValues.get(position).weeklyId

            val cal = Calendar.getInstance()
            // to open the calender with the current date of this moment.
            val currentYear = cal[Calendar.YEAR]
            val currentMonth = cal[Calendar.MONTH]
            val currentDay = cal[Calendar.DAY_OF_MONTH]
            var dialog = DatePickerDialog(
                context!!,
                android.R.style.Theme_Holo_Light,
                My_Weekly_RecylerViewAdapter.calenderListener(
                    monthlyToSchedule,
                    date,
                    childFragmentManager
                ),
                currentYear,
                currentMonth,
                currentDay
            )
            dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
    }

    fun setMonthlyValues(mValues: ArrayList<MonthlySchedule>) {
        numOfMonthly = 1
        this.monthlyValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    fun setWeeklyValues(mValues: HashMap<String, ArrayList<WeeklySchedule>>) {
        numOfMonthly = 1
        this.monthlyWeekly = mValues

        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    override fun getItemCount(): Int = monthlyValues.size


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var numOfWeekly: TextView = view.findViewById(R.id.numOfMonthlyTextView)
        var info: Button = view.findViewById(R.id.buttonInfo)
        var edit: Button = view.findViewById(R.id.buttonEdit)
        var date: Button = view.findViewById(R.id.buttonSchedule)
        var delete: Button = view.findViewById(R.id.buttonDel)
        var cart: ImageView = view.findViewById(R.id.imageViewCart)
        lateinit var mItem: MonthlySchedule


        override fun toString(): String {
            return super.toString()
        }
    }


    internal class calenderListener(
        dailyToSchedule: Int, date: String, childFragmentManager: FragmentManager,
    ) : DatePickerDialog.OnDateSetListener {
        var dailyToSchedule = dailyToSchedule
        var date = date
        var childFragmentManager = childFragmentManager
        override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {

            date += year.toString() + "-" + (month + 1).toString() + "-" + dayOfMonth.toString()


            var upcoming = UpComingScheudule(
                -1,
                date,
                dailyToSchedule
            )


            var s = AsynTaskNew(upcoming, childFragmentManager)
            s.execute()


        }
    }


}