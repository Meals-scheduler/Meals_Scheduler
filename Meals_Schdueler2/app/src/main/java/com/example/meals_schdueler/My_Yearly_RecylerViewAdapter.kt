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

class My_Yearly_RecylerViewAdapter(
    yearlyValues: TreeMap<String, YearlySchedule>,
    childFragmentManager: FragmentManager,
    context: Context?,

    ) : RecyclerView.Adapter<My_Yearly_RecylerViewAdapter.ViewHolder>() {


    private var yearlyValues: TreeMap<String, YearlySchedule> = yearlyValues

    // private var weeklyValues: HashMap<String, WeeklySchedule> = weeklyValues
    private var yearlyMonthly: HashMap<String, ArrayList<MonthlySchedule>> = HashMap()

    private var childFragmentManager = childFragmentManager

    // private lateinit var recipeList: ArrayList<Recipe>
    private var numOfYearly = 1
    private var context = context
    private var yearlyToSchedule = -1
    var date = ""

    private var yearlyList: ArrayList<YearlySchedule> = ArrayList()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): My_Yearly_RecylerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_yearly_schedule, parent, false)

        for (i in yearlyValues) {
            yearlyList.add(i.value)
        }
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: My_Yearly_RecylerViewAdapter.ViewHolder, position: Int) {
        var item: YearlySchedule = yearlyList[position]!! // each item postion
        holder.mItem = item

        holder.numOfYearly.setText(numOfYearly++.toString())

        holder.edit.setOnClickListener {
           // Log.v("Elad1", "FF1" + item.monthlyId.toString())
            // copying the list not to override it in the edit .
            var tmpList: ArrayList<MonthlySchedule> = ArrayList()
            for (i in yearlyMonthly.get(item.yearlyId.toString())!!) {
                tmpList.add(i)
            }

            var dialog = EditYearlyDialog(
                tmpList,
                item.numOfMonth,
                item.monthlyIds,
                position + 1,
                item.yearlyId

            )
            dialog.show(childFragmentManager, "yearlyEdit")
        }



        holder.info.setOnClickListener {

            var dialog = YearlyDialogInfo(
                yearlyMonthly.get(item.yearlyId.toString())!!,
                item.numOfMonth,
                item.monthlyIds,
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
            yearlyToSchedule = yearlyValues.get(item.yearlyId.toString())!!.yearlyId

            val cal = Calendar.getInstance()
            // to open the calender with the current date of this moment.
            val currentYear = cal[Calendar.YEAR]
            val currentMonth = cal[Calendar.MONTH]
            val currentDay = cal[Calendar.DAY_OF_MONTH]
            var dialog = DatePickerDialog(
                context!!,
                android.R.style.Theme_Holo_Light,
                calenderListener(
                    yearlyToSchedule,
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

    fun setYearlyValues(mValues: TreeMap<String, YearlySchedule>) {
        //numOfMonthly = 1
        this.yearlyValues = mValues
        yearlyList.clear()
        for (i in mValues) {
            yearlyList.add(i.value)
        }
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    fun setMonthlyValues(mValues: HashMap<String, ArrayList<MonthlySchedule>>) {
        //numOfMonthly = 1
        this.yearlyMonthly = mValues

        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    override fun getItemCount(): Int = yearlyValues.size


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var numOfYearly: TextView = view.findViewById(R.id.numOfYearlyTextView)
        var info: Button = view.findViewById(R.id.buttonInfo)
        var edit: Button = view.findViewById(R.id.buttonEdit)
        var date: Button = view.findViewById(R.id.buttonSchedule)
        var delete: Button = view.findViewById(R.id.buttonDel)
        var cart: ImageView = view.findViewById(R.id.imageViewCart)
        lateinit var mItem: YearlySchedule


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