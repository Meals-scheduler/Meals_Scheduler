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
import java.util.*

class My_Yearly_RecylerViewAdapter(
    yearlyValues: TreeMap<String, YearlySchedule>,
    childFragmentManager: FragmentManager,
    context: Context?,
    activity : Activity

    ) : RecyclerView.Adapter<My_Yearly_RecylerViewAdapter.ViewHolder>() {


    private var yearlyValues: TreeMap<String, YearlySchedule> = yearlyValues

    // private var weeklyValues: HashMap<String, WeeklySchedule> = weeklyValues
    private var yearlyMonthly: HashMap<String, ArrayList<MonthlySchedule>> = HashMap()

    private var childFragmentManager = childFragmentManager

    // private lateinit var recipeList: ArrayList<Recipe>
    private var numOfYearly = 1
    private var context = context
    private var activity = activity

    private var date = ""
    private lateinit var cal: Calendar
    private lateinit var tpd: TimePickerDialog
    private lateinit var dpd: DatePickerDialog
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
            // deleteing this yearly from the map that holds for every yearly its monthly list
            yearlyMonthly.remove(item.yearlyId.toString())!!

            var dialog =
                DeleteAlertDialog(
                    "",
                    null,
                    yearlyValues.get(item.yearlyId.toString())!!.yearlyId,
                    "Yearly"
                )
            dialog.show(childFragmentManager, "DeleteMonthly")

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




}