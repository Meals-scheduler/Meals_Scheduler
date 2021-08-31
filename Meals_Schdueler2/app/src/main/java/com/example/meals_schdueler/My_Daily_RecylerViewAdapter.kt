package com.example.meals_schdueler

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
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
import com.allyants.notifyme.NotifyMe
import com.example.meals_schdueler.dummy.DailySchedule
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class My_Daily_RecylerViewAdapter(

    values: TreeMap<String, DailySchedule>,
    childFragmentManager: FragmentManager,
    context: Context?,
    activity: Activity
) : RecyclerView.Adapter<My_Daily_RecylerViewAdapter.ViewHolder>() {

    //var builder: java.lang.StringBuilder? = null

    // private var mValues: ArrayList<DailySchedule> = values
    private var mValues: TreeMap<String, DailySchedule> = values
    private var childFragmentManager = childFragmentManager
    private var numOfDaily = 1
    private lateinit var recipeList: ArrayList<Recipe>
    private var context = context
    private var activity = activity

    private var date = ""
    private lateinit var cal: Calendar
    private lateinit var tpd: TimePickerDialog
    private lateinit var dpd: DatePickerDialog

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
//            Log.v("Elad1", "OKKK1")
//            Log.v("Elad1", "ID" + item.dailyId.toString())
//            Log.v("Elad1", "Quantities " + mValues.get(item.dailyId.toString())!!.quantities)
//            Log.v("Elad1", "num of meals " + mValues.get(item.dailyId.toString())!!.numOfMeals)
//            Log.v("Elad1", "recipe ids " + mValues.get(item.dailyId.toString())!!.recipeIds)
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
                "Daily"
            )
            dialog.show(childFragmentManager, "DeleteDaily")

        }

        holder.date.setOnClickListener {

            cal = Calendar.getInstance()
            val currentYear = cal[Calendar.YEAR]
            val currentMonth = cal[Calendar.MONTH]
            val currentDay = cal[Calendar.DAY_OF_MONTH]

            dpd = DatePickerDialog(
                activity,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    tpd.show()


                },
                currentYear,
                currentMonth,
                currentDay
            )

            dpd.show()

            val hour = cal[Calendar.HOUR]
            val min = cal[Calendar.MINUTE]
            //val second = cal[Calendar.SECOND]

            tpd = TimePickerDialog(
                activity,
                TimePickerDialog.OnTimeSetListener { view, hour, minute ->

                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    var month = cal.get(Calendar.MONTH) + 1
                    date = cal.get(Calendar.YEAR).toString() + "-" + month
                        .toString() + "-" + cal.get(Calendar.DAY_OF_MONTH).toString()


                    val dialogClickListener =
                        DialogInterface.OnClickListener { dialog, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    val notifyMe: NotifyMe =
                                        NotifyMe.Builder(context).title("Meals-Scheudler")
                                            .content("Hey, Event is coming").color(255, 0, 0, 255)
                                            .led_color(255, 255, 255, 255).time(cal)
                                            .addAction(Intent(), "Snooze", false)
                                            .key("test").addAction(Intent(), "Dismiss", true, false)
                                            .large_icon(R.mipmap.ic_launcher_round).build()

                                    Log.v("Elad1","clicked yes")

                                }
                                DialogInterface.BUTTON_NEGATIVE -> {

                                    Log.v("Elad1","clicked no")
                                }
                            }
                        }

                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("Would you like to get notification on the specific day?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show()

                    var upcoming = UpComingScheudule(
                        -1,
                        date,
                        11
                    )


                    var s = AsynTaskNew(upcoming, childFragmentManager)
                    s.execute()


                },
                hour,
                min,
                false
            )
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



}