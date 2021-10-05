package com.example.meals_schdueler

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import kotlin.collections.HashMap

class My_Monthly_RecylerViewAdapter(

    monthlyValues: ArrayList<MonthlySchedule>,
    // weeklyValues: HashMap<String, WeeklySchedule>,
    childFragmentManager: FragmentManager,
    context: Context?,
    acticity: Activity

) : RecyclerView.Adapter<My_Monthly_RecylerViewAdapter.ViewHolder>(), GetAndPost,
    deleteInterface, toCopyRecipe {

    private var monthlyValues: ArrayList<MonthlySchedule> = monthlyValues

    // private var weeklyValues: HashMap<String, WeeklySchedule> = weeklyValues
    private var weeklyValues: ArrayList<WeeklySchedule> = ArrayList()

    private var childFragmentManager = childFragmentManager

    private var monthlyToDelete: Int? = null

    // private lateinit var recipeList: ArrayList<Recipe>
    private var context = context
    private var activity = acticity

    private var date = ""
    private lateinit var cal: Calendar
    private lateinit var tpd: TimePickerDialog
    private lateinit var dpd: DatePickerDialog

    private var queryToExcute = ""
    private var monthlyID = -1
    private var numOfWeek = ""
    private var weeklyIds = ""
    private var pos = -1
    private var totalCost = 0.0


    private var monthlyList: ArrayList<MonthlySchedule> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): My_Monthly_RecylerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_monthly_schedule, parent, false)


        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: My_Monthly_RecylerViewAdapter.ViewHolder, position: Int) {
        var item: MonthlySchedule = monthlyValues[position]!! // each item postion
        holder.mItem = item

        holder.numOfWeekly.setText(position.toString())

        holder.edit.setOnClickListener {
            queryToExcute = "edit"
            monthlyID = monthlyValues.get(position)!!.monthlyId
            weeklyIds = monthlyValues.get(position)!!.weeklyIds
            numOfWeek = monthlyValues.get(position)!!.numOfWeek
            pos = position + 1
            totalCost = monthlyValues.get(position).totalCost


            var s = AsynTaskNew(this, childFragmentManager,context!!)
            s.execute()

        }



        holder.info.setOnClickListener {
            queryToExcute = "info"
            monthlyID = monthlyValues.get(position)!!.monthlyId
            weeklyIds = monthlyValues.get(position)!!.weeklyIds
            numOfWeek = monthlyValues.get(position)!!.numOfWeek
            pos = position + 1
            totalCost = monthlyValues.get(position).totalCost


            var s = AsynTaskNew(this, childFragmentManager,context!!)
            s.execute()


        }

        holder.delete.setOnClickListener {
            // deleteing this monthly from the map that holds for every monthly its weekly list

            monthlyToDelete = position
            var dialog =
                DeleteAlertDialog(
                    "",
                    null,
                    monthlyValues.get(position)!!.monthlyId,
                    "Monthly",
                    this

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



                                    val intent = Intent(context, MainActivity::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    }

                                    val notifyMe: NotifyMe =
                                        NotifyMe.Builder(context).title("Meals-Scheudler")
                                            .content("Hey, Event is coming").color(255, 0, 0, 255)
                                            .led_color(255, 255, 255, 255).time(cal)
                                            .addAction(intent, "Check it out!", true)
                                            .key("test").addAction(Intent(), "Dismiss", true, false)
                                            .large_icon(R.mipmap.ic_launcher_round).build()
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


                    var s = AsynTaskNew(upcoming, childFragmentManager,context!!)
                    s.execute()


                },
                hour,
                min,
                false
            )
        }
    }

    fun setMonthlyValues(mValues: ArrayList<MonthlySchedule>) {

        this.monthlyValues = mValues

        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    fun setWeeklyValues(mValues: ArrayList<WeeklySchedule>) {

        this.weeklyValues = mValues

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

    override fun DoNetWorkOpreation(): String {
        var string = UserInterFace.userID.toString() + " " + monthlyID

        var link =
            "https://elad1.000webhostapp.com/getWeeklyForMonthly.php?ownerIDAndMonthly=" + string


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

        weeklyValues.clear()
        if (!str.equals("")) {


            var totalcost = 0.011
            var numOfDay = ""
            var dailyIds = ""
            // recipeList!!.clear()

            // weeklyList!!.clear()

            val weeklyInfo: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()


            // map to map each WeeklyID with a key as ID and contains all 2
            // array lists (e.g - numOfDay,dailyIds)
            var map: HashMap<String, ArrayList<String>> = HashMap()
            var mapTotalCost: HashMap<String, Double> = HashMap()

            var weeklyInfo2 = weeklyInfo[0].splitIgnoreEmpty("*")
            var currentWeeklyID = weeklyInfo2[0].toInt()

            for (i in weeklyInfo.indices) {

                weeklyInfo2 = weeklyInfo[i].splitIgnoreEmpty("*")

                //means we switch to the next WeeklyID
                if (weeklyInfo2[0].toInt() != currentWeeklyID) {

                    // to keep each Weekly its dailys ids and its num of day.(days in a week)
                    var totalLists: ArrayList<String> = ArrayList()
                    totalLists.add(numOfDay)
                    totalLists.add(dailyIds)
                    // saving this weekly daily ids and num of days
                    map.put(currentWeeklyID.toString(), totalLists)
                    // saving this weekly total cost
                    mapTotalCost.put(currentWeeklyID.toString(), totalcost)

                    //switching to the next WeeklyID
                    currentWeeklyID = weeklyInfo2[0].toInt()

                    // clearing the variables for next WeeeklyID
                    numOfDay = ""
                    dailyIds = ""

                }

                numOfDay += "" + weeklyInfo2[3] + " "
                dailyIds += "" + weeklyInfo2[4] + " "
                // saving the last total cost
                totalcost = weeklyInfo2[2].toDouble()


            }

            // not to skip on the last Weeekly

            if (!numOfDay.equals("")) {
                var totalLists: ArrayList<String> = ArrayList()
                totalLists.add(numOfDay)
                totalLists.add(dailyIds)
                map.put(currentWeeklyID.toString(), totalLists)
                mapTotalCost.put(currentWeeklyID.toString(), totalcost)
            }


            // making WeeklyScheudle objects
            var weeklyIdsArr = weeklyIds.splitIgnoreEmpty(" ")
            //  currentWeeklyID = -1
            var weekly = -1
            for (i in weeklyIdsArr) {
                weekly = -1
                for (j in weeklyInfo.indices) {
                    var weeklyInfo2 = weeklyInfo[j].splitIgnoreEmpty("*")
                    if (i.toInt() == weeklyInfo2[0].toInt() && weeklyValues.size < weeklyIdsArr.size && weekly != weeklyInfo2[0].toInt()) {
                        weeklyValues!!.add(
                            WeeklySchedule(
                                weeklyInfo2[0].toInt(),
                                weeklyInfo2[1].toInt(),
                                map.get(weeklyInfo2[0])!!.get(0),
                                map.get(weeklyInfo2[0])!!.get(1),
                                mapTotalCost.get(weeklyInfo2[0])!!,
                                false

                            )
                        )
                        weekly = weeklyInfo2[0].toInt()
                    }


                }

            }

            this.setWeeklyValues(weeklyValues)
            if (queryToExcute.equals("info")) {


                var dialog = MonthlyDialogInfo(

                    weeklyValues,
                    numOfWeek,
                    weeklyIds,
                    totalCost,
                    pos
                )


                dialog.show(childFragmentManager, "WeeklyDialogInfo")

            } else {

                // copying the list not to override it in the edit .
                var tmpList: ArrayList<WeeklySchedule> = ArrayList()
                for (i in weeklyValues) {
                    tmpList.add(i)
                }

                var dialog = EditMonthlyDialog(
                    tmpList,
                    numOfWeek,
                    weeklyIds,
                    pos,
                    monthlyID


                )
                dialog.show(childFragmentManager, "weeklyEdit")
            }


        }

    }


    override fun toDelete(isDelete: Boolean) {
        if (isDelete) {
            monthlyValues.removeAt(monthlyToDelete!!)
            notifyDataSetChanged()
        }
    }

    override fun <T> toCopy(toCopy: T) {
        monthlyValues.add(toCopy!! as MonthlySchedule)
        notifyDataSetChanged()
    }
}