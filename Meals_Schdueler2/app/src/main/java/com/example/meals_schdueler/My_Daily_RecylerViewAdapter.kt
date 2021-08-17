package com.example.meals_schdueler

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
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
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class My_Daily_RecylerViewAdapter(

    // values: ArrayList<DailySchedule>,
    values: TreeMap<String, DailySchedule>,
    childFragmentManager: FragmentManager,
    context: Context?,
) : RecyclerView.Adapter<My_Daily_RecylerViewAdapter.ViewHolder>() {

    var builder: java.lang.StringBuilder? = null

    // private var mValues: ArrayList<DailySchedule> = values
    private var mValues: TreeMap<String, DailySchedule> = values
    private var childFragmentManager = childFragmentManager
    private var numOfDaily = 1
    private lateinit var recipeList: ArrayList<Recipe>
    private var context = context
    private var dailyToSchedule = -1
    var date = ""
    private var dailyList: ArrayList<DailySchedule> = ArrayList()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): My_Daily_RecylerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_daily_schedule, parent, false)


        for (i in mValues) {
            dailyList.add(i.value)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: My_Daily_RecylerViewAdapter.ViewHolder, position: Int) {
        var item: DailySchedule = dailyList[position]!! // each item postion
        holder.mItem = item

        holder.numOfDaily.setText(numOfDaily++.toString())

        holder.edit.setOnClickListener {

            var tempRecipeList: ArrayList<Recipe> = ArrayList()

            // copying the Recipe list so if we edit it and then quit without saving, it wouldn't change the original list
            for (i in recipeList) {
                tempRecipeList.add(i)
            }
            var dialog = EditDailyDialog(
                tempRecipeList,
                mValues.get(item.dailyId.toString())!!.quantities,
                mValues.get(item.dailyId.toString())!!.numOfMeals,
                mValues.get(item.dailyId.toString())!!.recipeIds,
                position + 1,
                this,
                mValues.get(item.dailyId.toString())!!.dailyId
            )
            dialog.show(childFragmentManager, "dailyEdit")
        }



        holder.info.setOnClickListener {
            Log.v("Elad1", "OKKK1")
            Log.v("Elad1", "ID" + item.dailyId.toString())
            Log.v("Elad1", "Quantities " + mValues.get(item.dailyId.toString())!!.quantities)
            Log.v("Elad1", "num of meals " + mValues.get(item.dailyId.toString())!!.numOfMeals)
            Log.v("Elad1", "recipe ids " + mValues.get(item.dailyId.toString())!!.recipeIds)
            var dialog = DailyDialogInfo(
                recipeList!!,
                mValues.get(item.dailyId.toString())!!.quantities,
                mValues.get(item.dailyId.toString())!!.numOfMeals,
                mValues.get(item.dailyId.toString())!!.recipeIds,
                position + 1,
                mValues.get(item.dailyId.toString())!!.dailyId

            )
            dialog.show(childFragmentManager, "DailyDialogInfo")

        }

        holder.delete.setOnClickListener {

            var dialog = DeleteAlertDialog(
                "",
                null,
                mValues.get(item.dailyId.toString())!!.dailyId,
                false,
                true,
                false
            )
            dialog.show(childFragmentManager, "DeleteDaily")

        }

        holder.date.setOnClickListener {
            dailyToSchedule = mValues.get(item.dailyId.toString())!!.dailyId

            val cal = Calendar.getInstance()
            // to open the calender with the current date of this moment.
            val currentYear = cal[Calendar.YEAR]
            val currentMonth = cal[Calendar.MONTH]
            val currentDay = cal[Calendar.DAY_OF_MONTH]
            var dialog = DatePickerDialog(
                context!!,
                android.R.style.Theme_Holo_Light,
                calenderListener(dailyToSchedule, date, childFragmentManager),
                currentYear,
                currentMonth,
                currentDay
            )
            dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
    }

    fun setmValues(mValues: TreeMap<String, DailySchedule>) {
        numOfDaily = 1
        this.mValues = mValues
        dailyList.clear()
        for (i in mValues) {
            dailyList.add(i.value)
        }
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }


    fun setRecipeList(recipeList: ArrayList<Recipe>) {

        this.recipeList = recipeList

        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    override fun getItemCount(): Int = mValues.size


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var numOfDaily: TextView = view.findViewById(R.id.numOfDailyTextView)
        var info: Button = view.findViewById(R.id.buttonInfo)
        var edit: Button = view.findViewById(R.id.buttonEdit)
        var date: Button = view.findViewById(R.id.buttonSchedule)
        var delete: Button = view.findViewById(R.id.buttonDel)
        var cart: ImageView = view.findViewById(R.id.imageViewCart)
        lateinit var mItem: DailySchedule


        override fun toString(): String {
            return super.toString()
        }
    }


    internal class calenderListener(
        dailyToSchedule: Int, date: String, childFragmentManager: FragmentManager,
    ) : OnDateSetListener {
        var dailyToSchedule = dailyToSchedule
        var date = date
        var childFragmentManager = childFragmentManager
        override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
//            var month = month
//            val cal = Calendar.getInstance()
//            val currentYear = cal[Calendar.YEAR]
//            month = month + 1

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
