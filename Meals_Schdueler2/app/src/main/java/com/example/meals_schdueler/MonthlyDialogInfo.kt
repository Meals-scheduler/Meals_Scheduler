package com.example.meals_schdueler

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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


class MonthlyDialogInfo(

    weeklyList: ArrayList<WeeklySchedule>,
    numOfWeek: String,
    weeklyIds: String,
    totalCost: Double,
    pos: Int,
) : DialogFragment(), View.OnClickListener, DialogInterface.OnDismissListener, GetAndPost {

    private lateinit var stk: TableLayout
    private lateinit var totalCost: EditText
    private lateinit var exit: ImageView
    private lateinit var title: TextView

    private var weeklyList = weeklyList
    private var dailyList: ArrayList<DailySchedule>? = null

    //  private var weeklyList = weeklyList
    // private var weeklyId = weeklyId
    private var totalCostt = totalCost
    private var numOfWeek = numOfWeek
    private var weeklyIds = weeklyIds

    //   private var totalCost = totalCost
    private var tablePosition = 1
    private var totalCostDobule: Double = 0.0
    private var position = pos
    private var weeklyPos = 0

    private var weeklyID = -1
    private var numOfDays = ""
    private var dailyIds = ""
    private var pos = -1
    private var totalCostWeekly = 0.0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var x: View = inflater.inflate(R.layout.monthly_info, container, false)
        stk = x.findViewById(R.id.tableLayout)
        totalCost = x.findViewById(R.id.editTextTotalCost)
        title = x.findViewById(R.id.monthlyInfo)
        title.setText("Month No. " + position)


        exit = x.findViewById(R.id.imageViewX)
        exit.setOnClickListener(this)

        addTable()
        initMonthly()
        return x


    }

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }


    private fun initMonthly() {


        // converting the strings into arr's
        var numOfWeekArr = numOfWeek.splitIgnoreEmpty(" ")
        var weekIdsArr = weeklyIds.splitIgnoreEmpty(" ")

        totalCostDobule += totalCostt
        totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
        totalCost.setText(totalCostDobule.toString())


        var j = 0
        for (i in weeklyList) {


            var tbrow: TableRow = TableRow(this.context)
            tbrow.setTag(tablePosition++)


            var t1v: TextView = TextView(context)

            // t1v.setBackgroundResource(R.drawable.border)
            var weekChoosen = ""
            when (numOfWeekArr[j]) {
                "0" -> weekChoosen = "Week 1"
                "1" -> weekChoosen = "Week 2"
                "2" -> weekChoosen = "Week 3"
                "3" -> weekChoosen = "Week 4"


            }

            t1v.setText(" " + (weekChoosen))
            t1v.setTextColor(Color.BLACK)
            t1v.gravity = Gravity.CENTER
            //  t1v.setBackgroundResource(R.drawable.spinner_shape)
            tbrow.addView(t1v)


            var t2v: TextView = TextView(context)


            t2v.setText(" " + (i.weeklyId))
            t2v.setTextColor(Color.BLACK)
            t2v.gravity = Gravity.CENTER

            tbrow.addView(t2v)


            var t3v: Button = Button(context)
            t3v.setTag(weeklyPos++)
            t3v.setText("Info")
            t3v.setTextSize(10F)
            t3v.setTextColor(Color.BLACK)
            t3v.gravity = Gravity.CENTER

            //t5v.setBackgroundResource(R.drawable.spinner_shape)
            t3v.setOnClickListener {
                dailyList = ArrayList()

                weeklyID = i.weeklyId

                numOfDays = i.numOfDay
                dailyIds = i.dailyIds
                pos = t3v.getTag() as Int + 1
                totalCostWeekly = i.totalCost


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
        tv0.setText(" Week ")
        tv0.setTextColor(Color.BLACK)
        tv0.gravity = Gravity.CENTER
        //  tv0.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.addView(tv0)

        var tv1: TextView = TextView(context)
        tv1.setText(" WeeklyId ")
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


    override fun onClick(p0: View?) {
        if (p0 == exit) {
            dismiss()
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



        return sb.toString()
    }

    override fun getData(str: String) {
        dailyList!!.clear()
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
            var daily = -1
            var dailyIdsArr = dailyIds.splitIgnoreEmpty(" ")
            //var dayid = -1
            for (i in dailyIdsArr) {
                daily=-1
                // if (dayid != i.toInt()) {

                for (j in dailyInfo.indices) {
                    var dailyInfo2 = dailyInfo[j].splitIgnoreEmpty("*")
                    if (i.toInt() == dailyInfo2[0].toInt() && dailyList!!.size < dailyIdsArr.size && daily != dailyInfo2[0].toInt()) {
                        dailyList!!.add(
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
                        daily = dailyInfo2[0].toInt()
                    }
                }
                // }
                // dayid = dailyInfo2[0].toInt()
            }


            var dialog = WeeklyDialogInfo(

                dailyList!!,
                numOfDays,
                dailyIds,
                totalCostWeekly,
                pos
            )


            dialog.show(childFragmentManager, "WeeklyDialogInfo")


        }


    }
}

