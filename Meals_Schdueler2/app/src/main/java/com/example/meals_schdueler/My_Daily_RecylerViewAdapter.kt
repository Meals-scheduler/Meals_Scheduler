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


class My_Daily_RecylerViewAdapter(

    values: ArrayList<DailySchedule>,
    childFragmentManager: FragmentManager,
    context: Context?,
) : RecyclerView.Adapter<My_Daily_RecylerViewAdapter.ViewHolder>() {

    var builder: java.lang.StringBuilder? = null
    private var mValues: ArrayList<DailySchedule> = values
    private var childFragmentManager = childFragmentManager
    private var numOfDaily = 1
    private lateinit var recipeList: ArrayList<Recipe>
    private var context = context
    private var dailyToSchedule = -1
    var date = ""
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): My_Daily_RecylerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_daily_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: My_Daily_RecylerViewAdapter.ViewHolder, position: Int) {
        var item: DailySchedule = mValues[position] // each item postion
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
                mValues.get(position).quantities,
                mValues.get(position).numOfMeals,
                mValues.get(position).recipeIds,
                position + 1,
                this,
                mValues.get(position).dailyId
            )
            dialog.show(childFragmentManager, "dailyEdit")
        }

        holder.info.setOnClickListener {
            var dialog = MyRecipeIngredietns(
                recipeList.get(position).listOfIngredients,
                recipeList.get(position).recipeName,
                recipeList.get(position).pictureBitMap,
                recipeList.get(position).numOfPortions,
                recipeList.get(position).quantityList,
                recipeList.get(position).totalCost
            )
            dialog.show(childFragmentManager, "MyRecipeIngredients")

        }

        holder.delete.setOnClickListener {

            var dialog = DeleteAlertDialog("", null, mValues.get(position).dailyId, false, true)
            dialog.show(childFragmentManager, "DeleteDaily")
//            val b`uilder: AlertDialog.Builder = AlertDialog.Builder(context)
//
//            builder.setTitle("Delete Daily")
//            builder.setMessage("Are you sure?")
//
//            builder.setPositiveButton(
//                "YES",
//                DialogInterface.OnClickListener { dialog, which -> // Do nothing but close the dialog
//                    dailyToDelete = item.dailyId
//                    var s = AsynTaskNew(this,childFragmentManager)
//                    s.execute()
//                    dialog.dismiss()
//                })
//
//            builder.setNegativeButton(
//                "NO",
//                DialogInterface.OnClickListener { dialog, which -> // Do nothing
//                    dialog.dismiss()
//                })
//
//            val alert: AlertDialog = builder.create()
//            alert.show()
        }

        holder.date.setOnClickListener {
            dailyToSchedule = mValues.get(position).dailyId

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

    fun setmValues(mValues: ArrayList<DailySchedule>) {
        numOfDaily = 1
        this.mValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }


    fun setRecipeList(recipeList: ArrayList<Recipe>) {
        Log.v("Elad1", "Successs!!!!!")
        Log.v("Elad1", "recipe size!!!!!" + recipeList.size)
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
        dailyToSchedule: Int, date:String, childFragmentManager: FragmentManager,
    ) : OnDateSetListener {
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
