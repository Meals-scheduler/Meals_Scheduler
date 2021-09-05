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
import kotlin.collections.HashMap

class My_Weekly_RecylerViewAdapter(

    weeklyValues: ArrayList<WeeklySchedule>,
    // dailyValues: HashMap<String, DailySchedule>,
    childFragmentManager: FragmentManager,
    context: Context?,
    activity: Activity
) : RecyclerView.Adapter<My_Weekly_RecylerViewAdapter.ViewHolder>(), GetAndPost {

    private var weeklyValues: ArrayList<WeeklySchedule> = weeklyValues

    //private var dailyValues: HashMap<String, DailySchedule> = dailyValues
    private var dailyValues: ArrayList<DailySchedule> = ArrayList()

    private var childFragmentManager = childFragmentManager
  //  private lateinit var recipeList: ArrayList<Recipe>

    private var context = context
    private var activity = activity

    private var date = ""
    private lateinit var cal: Calendar
    private lateinit var tpd: TimePickerDialog
    private lateinit var dpd: DatePickerDialog

    private var queryToExcute = ""
    private var weeklyID = -1
    private var numOfDay = ""
    private var dailyIds = ""
    private var pos = -1
    private var totalCost = 0.0


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): My_Weekly_RecylerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_weekly_schedule, parent, false)



        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: My_Weekly_RecylerViewAdapter.ViewHolder, position: Int) {
        var item: WeeklySchedule = weeklyValues[position]!! // each item postion
        holder.mItem = item

        holder.numOfWeekly.setText(position.toString())

        holder.edit.setOnClickListener {


            queryToExcute = "edit"
            weeklyID = weeklyValues.get(position)!!.weeklyId
            numOfDay = weeklyValues.get(position)!!.numOfDay
            dailyIds = weeklyValues.get(position)!!.dailyIds
            pos = position + 1


            var s = AsynTaskNew(this, childFragmentManager)
            s.execute()

        }



        holder.info.setOnClickListener {

            queryToExcute = "info"
            weeklyID = weeklyValues.get(position)!!.weeklyId
            numOfDay = weeklyValues.get(position)!!.numOfDay
            dailyIds = weeklyValues.get(position)!!.dailyIds
            pos = position + 1
            totalCost = weeklyValues.get(position)!!.totalCost.toDouble()





            var s = AsynTaskNew(this, childFragmentManager)
            s.execute()

            // Log.v("Elad1","HH" + weeklyDaily.get(item.weeklyId.toString())!!.get(1).recipeIds)
//            Log.v("Elad1", "FF2" + item.weeklyId.toString())
//            var dialog = WeeklyDialogInfo(
//               // weeklyDaily.get(item.weeklyId.toString())!!,
//                item.numOfDay,
//                item.dailyIds,
//                item.totalCost,
//                (position + 1)
//            )
//
//
//            dialog.show(childFragmentManager, "WeeklyDialogInfo")


        }

        holder.delete.setOnClickListener {
            // deleteing this weekly from the map that holds for every weekly its daily list
            //  weeklyDaily.remove(item.weeklyId.toString())!!

            var dialog =
                DeleteAlertDialog(
                    "",
                    null,
                    weeklyValues.get(position)!!.weeklyId,
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

    fun setWeeklyValues(mValues: ArrayList<WeeklySchedule>) {

        this.weeklyValues = mValues

        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    fun setDailyValues(mValues: ArrayList<DailySchedule>) {

        this.dailyValues = mValues

        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }


//    fun setRecipeList(recipeList: ArrayList<Recipe>) {
//
//        this.recipeList = recipeList
//        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
//    }

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

    override fun DoNetWorkOpreation(): String {
        var string = UserInterFace.userID.toString() + " " + weeklyID

        var link =
            "https://elad1.000webhostapp.com/getDailyForWeekly.php?ownerIDAndWeekly=" + string


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

        dailyValues!!.clear()
        if (!str.equals("")) {
            var quantities = ""
            var numOfMeal = ""
            var recipeIds = ""
            var totalcost = 0.011


            val dailyInfo: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()

            // map to map each DailyID with a key as ID and contains all 3
            // array lists (e.g - quantities,recipeIds,numOfMeals)
            var map: HashMap<String, ArrayList<String>> = HashMap()
            var mapTotalCost: HashMap<String, Double> = HashMap()
            // first attach each meal to its dailyID.
            var dailyInfo2 = dailyInfo[0].splitIgnoreEmpty("*")
            var currentDailyID = dailyInfo2[0].toInt()
            for (i in dailyInfo.indices) {
                dailyInfo2 = dailyInfo[i].splitIgnoreEmpty("*")
                //means we switch to the next DailyID
                if (dailyInfo2[0].toInt() != currentDailyID) {
                    var totalLists: ArrayList<String> = ArrayList()
                    totalLists.add(quantities)
                    totalLists.add(numOfMeal)
                    totalLists.add(recipeIds)

                    // gathering all quantities , numOfMeal and recipeIds under
                    // the key of that DailyID
                    map.put(currentDailyID.toString(), totalLists)
                    mapTotalCost.put(currentDailyID.toString(), totalcost)
                    //switching to the next DailyID
                    currentDailyID = dailyInfo2[0].toInt()

                    // clearing the variables for next DailyID
                    quantities = ""
                    numOfMeal = ""
                    recipeIds = ""
                }
                quantities += "" + dailyInfo2[5] + " "
                numOfMeal += "" + dailyInfo2[3] + " "
                recipeIds += "" + dailyInfo2[4] + " "
                // saving the last total cost
                totalcost = dailyInfo2[2].toDouble()
            }
            if (!quantities.equals("")) {
                var totalLists: ArrayList<String> = ArrayList()
                totalLists.add(quantities)
                totalLists.add(numOfMeal)
                totalLists.add(recipeIds)
                map.put(currentDailyID.toString(), totalLists)
                mapTotalCost.put(currentDailyID.toString(), totalcost)
            }

            //  recipeNumbers += "" + i + " "
            // making DailyScheudle objects
            currentDailyID = -1

            var dailyIdsArr = dailyIds.splitIgnoreEmpty(" ")

            for (i in dailyIdsArr) {

                for (j in dailyInfo.indices) {
                    var dailyInfo2 = dailyInfo[j].splitIgnoreEmpty("*")
                    if (currentDailyID != dailyInfo2[0].toInt() && i.toInt() == dailyInfo2[0].toInt()) {
                        dailyValues!!.add(
                            DailySchedule(
                                dailyInfo2[0].toInt(),
                                dailyInfo2[1].toInt(),
                                map.get(dailyInfo2[0])!!.get(1),
                                map.get(dailyInfo2[0])!!.get(0),
                                map.get(dailyInfo2[0])!!.get(2),
                                mapTotalCost.get(dailyInfo2[0])!!,
                                false

                            )
                        )
                        currentDailyID = dailyInfo2[0].toInt()
                    }
                }
            }

            this.setDailyValues(dailyValues)
            if (queryToExcute.equals("info")) {
                Log.v("Elad1","Num of Days " + numOfDay)
                Log.v("Elad1","dailyIds " + dailyIds)

                var dialog = WeeklyDialogInfo(

                    dailyValues,
                    numOfDay,
                    dailyIds,
                    totalCost,
                    pos
                )


                dialog.show(childFragmentManager, "WeeklyDialogInfo")

            } else {

                // copying the list not to override it in the edit .
                var tmpList: ArrayList<DailySchedule> = ArrayList()
                for (i in dailyValues) {
                    tmpList.add(i)
                }

                var dialog = EditWeeklyDialog(
                    tmpList,
                    numOfDay,
                    dailyIds,
                    pos,
                    weeklyID

                )
                dialog.show(childFragmentManager, "weeklyEdit")
            }
        }


    }
}