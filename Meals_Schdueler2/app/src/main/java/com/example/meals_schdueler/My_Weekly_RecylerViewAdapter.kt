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
import com.example.meals_schdueler.dummy.DailySchedule
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class My_Weekly_RecylerViewAdapter(

    weeklyValues: TreeMap<String, WeeklySchedule>,
   // dailyValues: HashMap<String, DailySchedule>,
    childFragmentManager: FragmentManager,
    context: Context?,
) : RecyclerView.Adapter<My_Weekly_RecylerViewAdapter.ViewHolder>() {

    private var weeklyValues: TreeMap<String, WeeklySchedule> = weeklyValues
    //private var dailyValues: HashMap<String, DailySchedule> = dailyValues
    private var weeklyDaily: HashMap<String, ArrayList<DailySchedule>> = HashMap()

    private var childFragmentManager = childFragmentManager
    private lateinit var recipeList: ArrayList<Recipe>
    private var numOfWeekly = 1
    private var context = context
    private var weeklyToSchedule = -1
    var date = ""
    private var weeklyList: ArrayList<WeeklySchedule> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): My_Weekly_RecylerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_weekly_schedule, parent, false)


        for (i in weeklyValues) {
            weeklyList.add(i.value)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: My_Weekly_RecylerViewAdapter.ViewHolder, position: Int) {
        var item: WeeklySchedule = weeklyList[position]!! // each item postion
        holder.mItem = item

        holder.numOfWeekly.setText(numOfWeekly++.toString())

        holder.edit.setOnClickListener {
            Log.v("Elad1", "FF1" + item.weeklyId.toString())
            // copying the list not to override it in the edit .
            var tmpList: ArrayList<DailySchedule> = ArrayList()
            for (i in weeklyDaily.get(item.weeklyId.toString())!!) {
                tmpList.add(i)
            }

            var dialog = EditWeeklyDialog(
                tmpList,
                item.numOfDay,
                item.dailyIds,
                position + 1,
                item.weeklyId

            )
            dialog.show(childFragmentManager, "weeklyEdit")
        }



        holder.info.setOnClickListener {
            // Log.v("Elad1","HH" + weeklyDaily.get(item.weeklyId.toString())!!.get(1).recipeIds)
            Log.v("Elad1", "FF2" + item.weeklyId.toString())
            var dialog = WeeklyDialogInfo(
                weeklyDaily.get(item.weeklyId.toString())!!,
                item.numOfDay,
                item.dailyIds,
                item.totalCost,
                (position + 1)
            )


            dialog.show(childFragmentManager, "WeeklyDialogInfo")


        }

        holder.delete.setOnClickListener {
            // deleteing this weekly from the map that holds for every weekly its daily list
            weeklyDaily.remove(item.weeklyId.toString())!!

            var dialog =
                DeleteAlertDialog(
                    "",
                    null,
                    weeklyValues.get(item.weeklyId.toString())!!.weeklyId,
                    false,
                    false,
                    true
                )
            dialog.show(childFragmentManager, "DeleteWeekly")

        }

        holder.date.setOnClickListener {
            weeklyToSchedule = weeklyValues.get(item.weeklyId.toString())!!.weeklyId

            val cal = Calendar.getInstance()
            // to open the calender with the current date of this moment.
            val currentYear = cal[Calendar.YEAR]
            val currentMonth = cal[Calendar.MONTH]
            val currentDay = cal[Calendar.DAY_OF_MONTH]
            var dialog = DatePickerDialog(
                context!!,
                android.R.style.Theme_Holo_Light,
                My_Weekly_RecylerViewAdapter.calenderListener(
                    weeklyToSchedule,
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

    fun setWeeklyValues(mValues: TreeMap<String, WeeklySchedule>) {
        numOfWeekly = 1
        this.weeklyValues = mValues
        weeklyList.clear()
        for (i in mValues) {
            weeklyList.add(i.value)
        }
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    fun setDailyValues(mValues: HashMap<String, ArrayList<DailySchedule>>) {
        numOfWeekly = 1
        this.weeklyDaily = mValues

        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }


    fun setRecipeList(recipeList: ArrayList<Recipe>) {

        this.recipeList = recipeList
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    override fun getItemCount(): Int = weeklyValues.size


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var numOfWeekly: TextView = view.findViewById(R.id.numOfWeeklyTextView)
        var info: Button = view.findViewById(R.id.buttonInfo)
        var edit: Button = view.findViewById(R.id.buttonEdit)
        var date: Button = view.findViewById(R.id.buttonSchedule)
        var delete: Button = view.findViewById(R.id.buttonDel)
        var cart: ImageView = view.findViewById(R.id.imageViewCart)
        lateinit var mItem: WeeklySchedule


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