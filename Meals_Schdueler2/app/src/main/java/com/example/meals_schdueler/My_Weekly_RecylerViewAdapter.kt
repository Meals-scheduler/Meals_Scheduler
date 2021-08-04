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
import com.example.meals_schdueler.dummy.WeeklyDialogInfo
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class My_Weekly_RecylerViewAdapter(

    weeklyValues: ArrayList<WeeklySchedule>,
    dailyValues: ArrayList<DailySchedule>,
    childFragmentManager: FragmentManager,
    context: Context?,
) : RecyclerView.Adapter<My_Weekly_RecylerViewAdapter.ViewHolder>() {

    private var weeklyValues: ArrayList<WeeklySchedule> = weeklyValues
    private var dailyyValues: ArrayList<DailySchedule> = dailyValues
    private var weeklyDaily: HashMap<String, ArrayList<DailySchedule>> = HashMap()

    private var childFragmentManager = childFragmentManager
    private lateinit var recipeList: ArrayList<Recipe>
    private var numOfWeekly = 1
    private var context = context
    private var weeklyToSchedule = -1
    var date = ""

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): My_Weekly_RecylerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_weekly_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: My_Weekly_RecylerViewAdapter.ViewHolder, position: Int) {
        var item: WeeklySchedule = weeklyValues[position] // each item postion
        holder.mItem = item

        holder.numOfWeekly.setText(numOfWeekly++.toString())

        holder.edit.setOnClickListener {

            var tempRecipeList: ArrayList<Recipe> = ArrayList()

            // copying the Recipe list so if we edit it and then quit without saving, it wouldn't change the original list
//            for (i in recipeList) {
//                tempRecipeList.add(i)
//            }
            // var dialog = EditDailyDialog(
//                tempRecipeList,
//                mValues.get(position).quantities,
//                mValues.get(position).numOfMeals,
//                mValues.get(position).recipeIds,
//                position + 1,
//                this,
//                mValues.get(position).dailyId
            //)
            //  dialog.show(childFragmentManager, "dailyEdit")
        }



        holder.info.setOnClickListener {
            var dialog = WeeklyDialogInfo(
                weeklyDaily.get(item.weeklyId.toString())!!,
                item.numOfDay,
                item.dailyIds,
                item.totalCost,
                position + 1
            )



            dialog.show(childFragmentManager, "DailyDialogInfo")


            Log.v("Elad1", "Test" + weeklyDaily.get(item.weeklyId.toString())!!.get(0).dailyId)
            Log.v("Elad1", "Test" + item.numOfDay)
            Log.v("Elad1", "Test" + weeklyDaily.get(item.weeklyId.toString())!!.get(1).dailyId)
        }

        holder.delete.setOnClickListener {

            var dialog =
                DeleteAlertDialog("", null, weeklyValues.get(position).weeklyId, false, true)
            dialog.show(childFragmentManager, "DeleteDaily")

        }

        holder.date.setOnClickListener {
            weeklyToSchedule = weeklyValues.get(position).weeklyId

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

    fun setWeeklyValues(mValues: ArrayList<WeeklySchedule>) {
        numOfWeekly = 1
        this.weeklyValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    fun setDailyValues(mValues: HashMap<String, ArrayList<DailySchedule>>) {
        numOfWeekly = 1
        this.weeklyDaily = mValues
        Log.v("Elad1", "HERE AND " + weeklyDaily.size)
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }


    fun setRecipeList(recipeList: ArrayList<Recipe>) {
        Log.v("Elad1", "Successs!!!!!")
        Log.v("Elad1", "recipe size!!!!!" + recipeList.size)
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
//            var month = month
//            val cal = Calendar.getInstance()
//            val currentYear = cal[Calendar.YEAR]
//            month = month + 1
//            Log.v("Sivan", currentYear.toString() + "current")
//            Log.v("Sivan", year.toString() + "year")
            Log.v("Sivan", "Year" + year)
            Log.v("Sivan", "month" + month + 1)
            Log.v("Sivan", "day" + dayOfMonth)
            date += year.toString() + "-" + (month + 1).toString() + "-" + dayOfMonth.toString()


            var upcoming = UpComingScheudule(
                -1,
                date,
                dailyToSchedule
            )

            Log.v("Elad1", "DATE" + date)
            var s = AsynTaskNew(upcoming, childFragmentManager)
            s.execute()


        }
    }


}