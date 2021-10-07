package com.example.meals_schdueler

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.meals_schdueler.dummy.DailySchedule
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.util.HashMap

class YearlyDialogInfo(

    monthlyList: ArrayList<MonthlySchedule>,
    numOfMonth: String,
    monthlyIds: String,
    totalCost: Double,
    pos: Int,
) : DialogFragment(), View.OnClickListener, DialogInterface.OnDismissListener, GetAndPost {

    private lateinit var stk: TableLayout
    private lateinit var totalCost: EditText
    private lateinit var exit: ImageView
    private lateinit var title: TextView

    private var monthlyList = monthlyList
    private var weeklyList: ArrayList<WeeklySchedule>? = null

    //  private var weeklyList = weeklyList
    // private var weeklyId = weeklyId
    private var totalCostt = totalCost
    private var numOfMonth = numOfMonth
    private var monthlyIds = monthlyIds

    //   private var totalCost = totalCost
    private var tablePosition = 1
    private var totalCostDobule: Double = 0.0
    private var position = pos
    private var monthlyPos = 0


    private var monthlyID = -1
    private var numOfWeek = ""
    private var weeklyIds = ""
    private var pos = -1
    private var totalCostMonthly = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var x: View = inflater.inflate(R.layout.yearly_info, container, false)
        stk = x.findViewById(R.id.tableLayout)
        totalCost = x.findViewById(R.id.editTextTotalCost)
        title = x.findViewById(R.id.yearlyInfo)
        title.setText("Year No. " + position)


        exit = x.findViewById(R.id.imageViewX)
        exit.setOnClickListener(this)

        addTable()
        initYearly()
        return x


    }

    private fun initYearly() {


        // converting the strings into arr's
        var numOfMonthArr = numOfMonth.splitIgnoreEmpty(" ")
        var monthIdsArr = monthlyIds.splitIgnoreEmpty(" ")

        totalCostDobule += totalCostt
        totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
        totalCost.setText(totalCostDobule.toString())


        var j = 0
        for (i in monthlyList) {


            var tbrow: TableRow = TableRow(this.context)
            tbrow.setTag(tablePosition++)


            var t1v: TextView = TextView(context)

            // t1v.setBackgroundResource(R.drawable.border)
            var monthChoosen = ""
            when (numOfMonthArr[j]) {
                "0" -> monthChoosen = "Month 1"
                "1" -> monthChoosen = "Month 2"
                "2" -> monthChoosen = "Month 3"
                "3" -> monthChoosen = "Month 4"


            }

            t1v.setText(" " + (monthChoosen))
            t1v.setTextColor(Color.BLACK)
            t1v.gravity = Gravity.CENTER
            //  t1v.setBackgroundResource(R.drawable.spinner_shape)
            tbrow.addView(t1v)


            var t2v: TextView = TextView(context)


            t2v.setText(" " + (i.monthlyId))
            t2v.setTextColor(Color.BLACK)
            t2v.gravity = Gravity.CENTER

            tbrow.addView(t2v)


            var t3v: Button = Button(context)
            t3v.setTag(monthlyPos++)
            t3v.setText("Info")
            t3v.setTextSize(10F)
            t3v.setTextColor(Color.BLACK)
            t3v.gravity = Gravity.CENTER

            //t5v.setBackgroundResource(R.drawable.spinner_shape)
            t3v.setOnClickListener {
                weeklyList = ArrayList()

                monthlyID = i.monthlyId

                numOfWeek = i.numOfWeek
                weeklyIds = i.weeklyIds
                pos = t3v.getTag() as Int + 1
                totalCostMonthly = i.totalCost


                var s = AsynTaskNew(this, childFragmentManager,requireContext())
                s.execute()
            }
            tbrow.addView(t3v)



            stk.setBackgroundResource(R.drawable.spinner_shape)
            tbrow.setBackgroundResource(R.drawable.spinner_shape)
            stk.addView(tbrow)
            j++


        }

    }

    private fun addTable() {

        var tbrow0: TableRow = TableRow(context)

        var tv0: TextView = TextView(context)
        tv0.setText(" Month ")
        tv0.setTextColor(Color.BLACK)
        tv0.gravity = Gravity.CENTER
        //  tv0.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.addView(tv0)

        var tv1: TextView = TextView(context)
        tv1.setText(" MonthlyId ")
        tv1.setTextColor(Color.BLACK)
        tv1.gravity = Gravity.CENTER
        // tv1.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.addView(tv1)

        var tv3: TextView = TextView(context)
        tv3.setText(" Info ")
        tv3.setTextColor(Color.BLACK)
        tv3.gravity = Gravity.CENTER
        // tv3.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.addView(tv3)


        tbrow0.setBackgroundResource(R.drawable.spinner_shape)

        stk.addView(tbrow0)


    }

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }


    override fun onClick(p0: View?) {
        if (p0 == exit) {
            dismiss()
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



        return sb.toString()
    }

    override fun getData(str: String) {
        weeklyList!!.clear()
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
            // making WeeklyScheudle objects
            var weeklyIdsArr = weeklyIds.splitIgnoreEmpty(" ")
            //  currentWeeklyID = -1
            var weekly = -1
            for (i in weeklyIdsArr) {
                weekly= -1
                for (j in weeklyInfo.indices) {
                    var weeklyInfo2 = weeklyInfo[j].splitIgnoreEmpty("*")
                    if (i.toInt() == weeklyInfo2[0].toInt() && weeklyList!!.size < weeklyIdsArr.size && weekly != weeklyInfo2[0].toInt()) {
                        weeklyList!!.add(
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


            var dialog = MonthlyDialogInfo(

                weeklyList!!,
                numOfWeek,
                weeklyIds,
                totalCostDobule,
                pos
            )


            dialog.show(childFragmentManager, "WeeklyDialogInfo")

        }


    }

}

