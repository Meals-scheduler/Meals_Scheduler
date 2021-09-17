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
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class My_Yearly_RecylerViewAdapter(
    yearlyValues: ArrayList<YearlySchedule>,
    childFragmentManager: FragmentManager,
    context: Context?,
    activity: Activity

) : RecyclerView.Adapter<My_Yearly_RecylerViewAdapter.ViewHolder>(), GetAndPost {


    private var yearlyValues: ArrayList<YearlySchedule> = yearlyValues

    // private var weeklyValues: HashMap<String, WeeklySchedule> = weeklyValues
    private var monthlyValues: ArrayList<MonthlySchedule> = ArrayList()

    private var childFragmentManager = childFragmentManager

    // private lateinit var recipeList: ArrayList<Recipe>
    private var context = context
    private var activity = activity

    private var date = ""
    private lateinit var cal: Calendar
    private lateinit var tpd: TimePickerDialog
    private lateinit var dpd: DatePickerDialog

    private var queryToExcute = ""
    private var yearlyID = -1
    private var numOfMonth = ""
    private var monthlyIds = ""
    private var pos = -1
    private var totalCost = 0.0


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): My_Yearly_RecylerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_yearly_schedule, parent, false)


        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: My_Yearly_RecylerViewAdapter.ViewHolder, position: Int) {
        var item: YearlySchedule = yearlyValues[position]!! // each item postion
        holder.mItem = item

        holder.numOfYearly.setText(position.toString())

        holder.edit.setOnClickListener {
            queryToExcute = "edit"
            yearlyID = yearlyValues.get(position)!!.yearlyId
            monthlyIds = yearlyValues.get(position)!!.monthlyIds
            numOfMonth = yearlyValues.get(position)!!.numOfMonth
            pos = position + 1
            totalCost = yearlyValues.get(position).totalCost


            var s = AsynTaskNew(this, childFragmentManager)
            s.execute()
        }



        holder.info.setOnClickListener {

            queryToExcute = "info"
            yearlyID = yearlyValues.get(position)!!.yearlyId
            monthlyIds = yearlyValues.get(position)!!.monthlyIds
            numOfMonth = yearlyValues.get(position)!!.numOfMonth
            pos = position + 1
            totalCost = yearlyValues.get(position).totalCost


            var s = AsynTaskNew(this, childFragmentManager)
            s.execute()


        }

        holder.delete.setOnClickListener {
            // deleteing this yearly from the map that holds for every yearly its monthly list


            var dialog =
                DeleteAlertDialog(
                    "",
                    null,
                    yearlyValues.get(position)!!.yearlyId,
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

                                    Log.v("Elad1", "clicked yes")

                                }
                                DialogInterface.BUTTON_NEGATIVE -> {

                                    Log.v("Elad1", "clicked no")
                                }
                            }
                        }

                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("Would you like to get notification on the specific day?")
                        .setPositiveButton("Yes", dialogClickListener)
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

    fun setYearlyValues(mValues: ArrayList<YearlySchedule>) {
        //numOfMonthly = 1
        this.yearlyValues = mValues

        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    fun setMonthlyValues(mValues: ArrayList<MonthlySchedule>) {
        //numOfMonthly = 1
        this.monthlyValues = mValues

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

    override fun DoNetWorkOpreation(): String {
        var string = UserInterFace.userID.toString() + " " + yearlyID

        var link =
            "https://elad1.000webhostapp.com/getMonthlyForYearly.php?ownerIDAndYearly=" + string


        val sb = StringBuilder()

        val url = URL(link)
        val urlConnection = url.openConnection() as HttpURLConnection
        try {
            val `in`: InputStream = BufferedInputStream(urlConnection.inputStream)
            val bin = BufferedReader(InputStreamReader(`in`))
            // temporary string to hold each line read from the reader.
            var inputLine: String?

            while (bin.readLine().also { inputLine = it } != null) {
                sb.append(inputLine)

            }
        } finally {
            // regardless of success or failure, we will disconnect from the URLConnection.
            urlConnection.disconnect()
        }


        //Log.v("Elad1", "Id came is" + sb.toString())
        return sb.toString()
    }

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    override fun getData(str: String) {
        monthlyValues.clear()
        if (!str.equals("")) {


            var numOfWeek = ""
            var weeklyIds = ""
            var totalcost = 0.011
            // recipeList!!.clear()
            // monthlyList!!.clear()


            val monthlyInfo: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()


            // map to map each MonthlyID with a key as ID and contains all 2
            // array lists (e.g - numOfDay,dailyIds)
            var map: HashMap<String, ArrayList<String>> = HashMap()
            var mapTotalCost: HashMap<String, Double> = HashMap()


            var monthlyInfo2 = monthlyInfo[0].splitIgnoreEmpty("*")
            var currentMonthlyID = monthlyInfo2[0].toInt()


            for (i in monthlyInfo.indices) {

                monthlyInfo2 = monthlyInfo[i].splitIgnoreEmpty("*")

                //means we switch to the next WeeklyID
                if (monthlyInfo2[0].toInt() != currentMonthlyID) {

                    // to keep each Weekly its dailys ids and its num of day.(days in a week)
                    var totalLists: ArrayList<String> = ArrayList()
                    totalLists.add(numOfWeek)
                    totalLists.add(weeklyIds)
                    // saving this weekly daily ids and num of days
                    map.put(currentMonthlyID.toString(), totalLists)
                    // saving this weekly total cost
                    mapTotalCost.put(currentMonthlyID.toString(), totalcost)

                    //switching to the next WeeklyID
                    currentMonthlyID = monthlyInfo2[0].toInt()

                    // clearing the variables for next WeeeklyID
                    numOfWeek = ""
                    weeklyIds = ""

                }

                numOfWeek += "" + monthlyInfo2[3] + " "
                weeklyIds += "" + monthlyInfo2[4] + " "
                // saving the last total cost
                totalcost = monthlyInfo2[2].toDouble()


            }

            // not to skip on the last Weeekly

            if (!numOfWeek.equals("")) {
                var totalLists: ArrayList<String> = ArrayList()
                totalLists.add(numOfWeek)
                totalLists.add(weeklyIds)
                map.put(currentMonthlyID.toString(), totalLists)
                mapTotalCost.put(currentMonthlyID.toString(), totalcost)
            }



            // making MonthlyScheudle objects
            var monthlyIdsArr = monthlyIds.splitIgnoreEmpty(" ")
            var month = -1

            for (i in monthlyIdsArr) {
                month = -1

                for (j in monthlyInfo.indices) {
                    var monthlyInfo2 = monthlyInfo[j].splitIgnoreEmpty("*")
                    if (i.toInt() == monthlyInfo2[0].toInt() && monthlyValues.size < monthlyIdsArr.size && month != monthlyInfo2[0].toInt()) {
                        monthlyValues!!.add(
                            MonthlySchedule(
                                monthlyInfo2[0].toInt(),
                                monthlyInfo2[1].toInt(),
                                map.get(monthlyInfo2[0])!!.get(0),
                                map.get(monthlyInfo2[0])!!.get(1),
                                mapTotalCost.get(monthlyInfo2[0])!!,
                                false

                            )
                        )

                        month = monthlyInfo2[0].toInt()
                    }
                }

            }


            this.setMonthlyValues(monthlyValues)
            if (queryToExcute.equals("info")) {


                var dialog = YearlyDialogInfo(

                    monthlyValues,
                    numOfMonth,
                    monthlyIds,
                    totalCost,
                    pos
                )


                dialog.show(childFragmentManager, "WeeklyDialogInfo")

            } else {

                // copying the list not to override it in the edit .
                var tmpList: ArrayList<MonthlySchedule> = ArrayList()
                for (i in monthlyValues) {
                    tmpList.add(i)
                }

                var dialog = EditYearlyDialog(
                    tmpList,
                    numOfMonth,
                    monthlyIds,
                    pos,
                    yearlyID


                )
                dialog.show(childFragmentManager, "weeklyEdit")
            }

        }

    }


}