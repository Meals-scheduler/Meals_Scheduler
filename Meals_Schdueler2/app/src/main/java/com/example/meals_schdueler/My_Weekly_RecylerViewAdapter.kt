package com.example.meals_schdueler

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
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
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class My_Weekly_RecylerViewAdapter(

    weeklyValues: TreeMap<String, WeeklySchedule>,
   // dailyValues: HashMap<String, DailySchedule>,
    childFragmentManager: FragmentManager,
    context: Context?,
    activity : Activity
) : RecyclerView.Adapter<My_Weekly_RecylerViewAdapter.ViewHolder>() {

    private var weeklyValues: TreeMap<String, WeeklySchedule> = weeklyValues
    //private var dailyValues: HashMap<String, DailySchedule> = dailyValues
    private var weeklyDaily: HashMap<String, ArrayList<DailySchedule>> = HashMap()

    private var childFragmentManager = childFragmentManager
    private lateinit var recipeList: ArrayList<Recipe>
    private var numOfWeekly = 1
    private var context = context
    private var activity= activity
    private var date = ""
    private lateinit var cal: Calendar
    private lateinit var tpd: TimePickerDialog
    private lateinit var dpd: DatePickerDialog
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
                   "Weekly"
                )
            dialog.show(childFragmentManager, "DeleteWeekly")

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





}