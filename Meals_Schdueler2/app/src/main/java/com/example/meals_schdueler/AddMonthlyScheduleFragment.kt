package com.example.meals_schdueler

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.core.view.iterator
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.example.meals_schdueler.dummy.DailySchedule
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat


class AddMonthlyScheduleFragment : Fragment(), View.OnClickListener,
    DialogInterface.OnDismissListener, GetAndPost {


    private var columnCount = 1
    private var tablePosition = 1
    private lateinit var stk: TableLayout
    private lateinit var saveBtn: Button
    private lateinit var chooseWeekBtn: Button
    private lateinit var totalCost: EditText
    private var totalCostDobule: Double = 0.0
    private var flag = true // this flag is for the duplicated week check


    //saving weekly id
    private var weeklyID: Recipe_Ingredients_List? = null
    private var listWeeklyIdChoosen: ArrayList<Int>? = null
    private var weeklyList: ArrayList<WeeklySchedule>? = null
    private var weeklyDays: String = ""
    private var weeklyIds: String = ""
    private var weeklyDayss: ArrayList<Int>? = null
    private var weeklyListChoose: ArrayList<WeeklySchedule>? = null
    private var dailyList: ArrayList<DailySchedule>? = null

    private var totalCostWeekly = 0.0
    private var savedSize = 0
    private var weeklyId = -1
    private var dailyIds = ""
    private var numOfDay = ""
    private var pos = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

        dailyList = ArrayList()
        weeklyListChoose = ArrayList()
        listWeeklyIdChoosen = ArrayList()
        weeklyID = Recipe_Ingredients_List(listWeeklyIdChoosen)
        weeklyList = ArrayList()
        weeklyDayss = ArrayList()


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
        tv3.setText(" Delete ")
        tv3.setTextColor(Color.BLACK)
        tv3.gravity = Gravity.CENTER
        tbrow0.setTag(0)
        // tv3.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.addView(tv3)


        var tv4: TextView = TextView(context)
        tv4.setText(" Info ")
        tv4.setTextColor(Color.BLACK)
        tv4.gravity = Gravity.CENTER
        // tv4.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.addView(tv4)



        tbrow0.setBackgroundResource(R.drawable.spinner_shape)

        stk.addView(tbrow0)


    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            AddMonthlyScheduleFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)

                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val x = inflater.inflate(R.layout.add_monthly_schedule, null)

        //stam = x.findViewById(R.id.elad)
        stk = x.findViewById(R.id.tableLayout)
        saveBtn = x.findViewById(R.id.saveBtn)

        chooseWeekBtn = x.findViewById(R.id.chooseBtn)
        totalCost = x.findViewById(R.id.editTextTotalCost)
        saveBtn = x.findViewById(R.id.saveBtn)

        chooseWeekBtn.setOnClickListener(this)
        saveBtn.setOnClickListener(this)


        //val view = inflater.inflate(com.example.meals_schdueler.R.layout.add_daily_schedule, container, false)

        addTable()
        return x
    }


    override fun onClick(p0: View?) {
        if (p0 == chooseWeekBtn) {
            weeklyID!!.list!!.clear()
            weeklyList!!.clear()
            savedSize = weeklyListChoose!!.size

            var d = Weekly_Schedule_Choose_Dialog(
                weeklyList!!,
                weeklyID!!
            )
            d.show(childFragmentManager, "DailySchudleChooseDialog")
        } else if (p0 == saveBtn) {

            weeklyDays = ""
            weeklyIds = ""
            flag = true
            weeklyDayss!!.clear()

            // getting these weekks id's
            for (i in weeklyListChoose!!) {
                weeklyIds += "" + i.weeklyId + " "

            }

            for (x in stk) {
                if (x.getTag() as Int == 0)
                    continue
                var y = x as TableRow
                var s: Spinner = y.getChildAt(0) as Spinner
                var value = s.selectedItem

                when (value) {
                    "Week 1" -> value = 0
                    "Week 2" -> value = 1
                    "Week 3" -> value = 2
                    "Week 4" -> value = 3


                }

                weeklyDayss!!.add(value as Int)


            }


            val arr = IntArray(4)

            for (i in weeklyDayss!!) {
                if (arr[i] != 0) {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)

                    builder.setTitle("Adding Monthly")
                    builder.setMessage("You cannot have a duplicate of the same week.")

                    builder.setPositiveButton(
                        "OK",
                        DialogInterface.OnClickListener { dialog, which -> // Do nothing but close the dialog

                            dialog.dismiss()
                        })
                    val alert: AlertDialog = builder.create()
                    alert.show()
                    flag = false


                    break
                } else {
                    arr[i] += 1
                    weeklyDays += "" + i + " "
                }

            }

            if (flag) {
                var m = MonthlySchedule(
                    1,
                    UserInterFace.userID,
                    weeklyDays,
                    weeklyIds,
                    totalCostDobule,
                    false
                )
                var s = AsynTaskNew(m, childFragmentManager)
                s.execute()

                MyMonthlyScheudleFragment.getInstance1().getRecycler().toCopy(m)
                clearTable()
            }
        }


    }

    private fun clearTable() {
        tablePosition = 1
        totalCostDobule = 0.0
        weeklyDays = ""
        weeklyIds = ""
        weeklyDayss!!.clear()
        savedSize = 0
        weeklyListChoose!!.clear()
        totalCost.setText(totalCostDobule.toString())
        var j = 1
        for (x in stk) {
            stk.removeView(stk.getChildAt(j))
        }
        stk.removeView(stk.getChildAt(j))

    }


    override fun onDismiss(p0: DialogInterface?) {

        for (i in weeklyID!!.list!!) {


            var weekly = weeklyList!!.get(i)
            weeklyListChoose!!.add(weekly!!)


        }

        var j = 0
        for (i in weeklyListChoose!!) {

            // saved size to know the size of list before we change  so we wont override all the list.
            if (j > savedSize - 1) {


                stk.setColumnShrinkable(3, false)
                stk.setColumnShrinkable(2, false)
                stk.setColumnStretchable(3, false)
                stk.setColumnStretchable(2, false)


                var tbrow: TableRow = TableRow(this.context)
                tbrow.setTag(tablePosition)

                totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
                totalCostDobule += weeklyListChoose!!.get(tablePosition - 1).totalCost
                totalCost.setText(totalCostDobule.toString())

                var t1v: Spinner = Spinner(context)
                t1v.setTag(tablePosition - 1)


                ArrayAdapter.createFromResource(
                    this.requireContext(),
                    R.array.weeks,
                    android.R.layout.simple_spinner_item
                ).also { adapter ->
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    // Apply the adapter to the spinner
                    t1v.adapter = adapter
                }

                if (stk.size > 1) {
                    var o = stk.get(stk.size - 1)
                    var y = o as TableRow
                    var s: Spinner = y.getChildAt(0) as Spinner
                    var value = s.selectedItem

                    when (value) {
                        "Week 1" -> value = 0
                        "Week 2" -> value = 1
                        "Week 3" -> value = 2
                        "Week 4" -> value = 3


                    }
                    var k = value as Int
                    t1v.setSelection(k + 1)
                } else {
                    t1v.setSelection(0)
                }

                tbrow.addView(t1v)

                var t2v: TextView = TextView(context)


                t2v.setText(weeklyListChoose!!.get(tablePosition - 1).weeklyId.toString())
                t2v.setTextColor(Color.BLACK)
                t2v.gravity = Gravity.CENTER
                //  t1v.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.addView(t2v)


                var t3v: Button = Button(context)
                t3v.setTag(tablePosition)
                t3v.setText("Delete")
                t3v.setTextColor(Color.BLACK)
                t3v.gravity = Gravity.CENTER
                t3v.setTextSize(10F)
                t3v.setOnClickListener {


                    var i = t3v.getTag() as Int

                    stk.removeView(stk.getChildAt(t3v.getTag() as Int))

                    totalCostDobule -= weeklyListChoose!!.get(t3v.getTag() as Int - 1).totalCost
                    totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
                    totalCost.setText(totalCostDobule.toString())
                    weeklyListChoose!!.removeAt(t3v.getTag() as Int - 1)




                    tablePosition--

                    for (x in stk) {
                        if (x.getTag() as Int == 0)
                            continue
                        if (x.getTag() as Int > i) {
                            x.setTag(x.getTag() as Int - 1)
                            var y = x as TableRow
                            //changing info delete tag
                            y.get(2).setTag(y.get(2).getTag() as Int - 1)
                            //changing infp button tag
                            y.get(3).setTag(y.get(3).getTag() as Int - 1)

                        }

                    }

//                stk.setColumnShrinkable(3, false)
//                stk.setColumnStretchable(3, false)
                }

                tbrow.addView(t3v)

                var t4v: Button = Button(context)
                t4v.setTag(tablePosition++)
                t4v.setText("Info")
                t4v.setTextSize(10F)
                t4v.setTextColor(Color.BLACK)
                t4v.gravity = Gravity.CENTER

                t4v.setOnClickListener {

                    dailyList = ArrayList()


                    weeklyId = weeklyListChoose!!.get(t3v.getTag() as Int - 1).weeklyId
                    dailyIds = weeklyListChoose!!.get(t3v.getTag() as Int - 1).dailyIds
                    numOfDay = weeklyListChoose!!.get(t3v.getTag() as Int - 1).numOfDay
                    totalCost
                    pos = t3v.getTag() as Int
                    totalCostWeekly =  weeklyListChoose!!.get(t3v.getTag() as Int - 1).totalCost

                    var s = AsynTaskNew(this, childFragmentManager)
                    s.execute()

                }
                tbrow.addView(t4v)


                stk.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.setBackgroundResource(R.drawable.spinner_shape)
                stk.addView(tbrow)


            }
            j++
        }
    }


    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    override fun DoNetWorkOpreation(): String {
        var string = UserInterFace.userID.toString() + " " + weeklyId


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
//            currentDailyID = -1
//
//            var dailyIdsArr = dailyIds.splitIgnoreEmpty(" ")
//
//            for (i in dailyIdsArr) {
//
//                for (j in dailyInfo.indices) {
//                    var dailyInfo2 = dailyInfo[j].splitIgnoreEmpty("*")
//                    if (currentDailyID != dailyInfo2[0].toInt() && i.toInt() == dailyInfo2[0].toInt()) {
//                        dailyList!!.add(
//                            DailySchedule(
//                                dailyInfo2[0].toInt(),
//                                dailyInfo2[1].toInt(),
//                                map.get(dailyInfo2[0])!!.get(1),
//                                map.get(dailyInfo2[0])!!.get(0),
//                                map.get(dailyInfo2[0])!!.get(2),
//                                mapTotalCost.get(dailyInfo2[0])!!,
//                                false
//
//                            )
//                        )
//                        currentDailyID = dailyInfo2[0].toInt()
//                    }
//                }
//            }

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




            Log.v("Elad1", "Num of Days " + numOfDay)
                Log.v("Elad1", "dailyIds " + dailyIds)

                var dialog = WeeklyDialogInfo(

                    dailyList!!,
                    numOfDay,
                    dailyIds,
                    totalCostWeekly,
                    pos
                )


                dialog.show(childFragmentManager, "WeeklyDialogInfo")


        }


    }
}


