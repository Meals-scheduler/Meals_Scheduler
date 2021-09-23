package com.example.meals_schdueler

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
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
import java.util.HashMap

class AddYearlyScheduleFragment : Fragment(), View.OnClickListener,
    DialogInterface.OnDismissListener, GetAndPost {


    private var columnCount = 1
    private var tablePosition = 1
    private lateinit var stk: TableLayout
    private lateinit var saveBtn: Button
    private lateinit var chooseMonthBtn: Button
    private lateinit var totalCost: EditText
    private var totalCostDobule: Double = 0.0
    private var flag = true // this flag is for the duplicated week check


    //saving weekly id
    private var monthlyyID: Recipe_Ingredients_List? = null
    private var listMonthlyIdChoosen: ArrayList<Int>? = null
    private var monthlyList: ArrayList<MonthlySchedule>? = null
    private var monthlyDays: String = ""
    private var monthlyIds: String = ""
    private var monthlyDayss: ArrayList<Int>? = null
    private var monthlyListChoose: ArrayList<MonthlySchedule>? = null
    private var weeklyList: ArrayList<WeeklySchedule>? = null


    private var totalCostMonthly = 0.0
    private var savedSize = 0
    private var monthlyId = -1
    private var weeklyIds = ""
    private var numOfWeek = ""
    private var pos = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

        monthlyListChoose = ArrayList()
        weeklyList = ArrayList()
        listMonthlyIdChoosen = ArrayList()
        monthlyyID = Recipe_Ingredients_List(listMonthlyIdChoosen)
        monthlyList = ArrayList()
        monthlyDayss = ArrayList()


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
        tv1.setText(" MonthID ")
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
            AddYearlyScheduleFragment().apply {
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
        val x = inflater.inflate(R.layout.add_yearly_schedule, null)

        //stam = x.findViewById(R.id.elad)
        stk = x.findViewById(R.id.tableLayout)
        saveBtn = x.findViewById(R.id.saveBtn)

        chooseMonthBtn = x.findViewById(R.id.chooseBtn)
        totalCost = x.findViewById(R.id.editTextTotalCost)
        saveBtn = x.findViewById(R.id.saveBtn)

        chooseMonthBtn.setOnClickListener(this)
        saveBtn.setOnClickListener(this)


        //val view = inflater.inflate(com.example.meals_schdueler.R.layout.add_daily_schedule, container, false)

        addTable()
        return x
    }


    override fun onClick(p0: View?) {
        if (p0 == chooseMonthBtn) {
            monthlyyID!!.list!!.clear()
            monthlyList!!.clear()
            savedSize = monthlyListChoose!!.size

            var d = Monthly_Schedule_Choose_Dialog(
                monthlyList!!,
                monthlyyID!!
            )
            d.show(childFragmentManager, "DailySchudleChooseDialog")
        } else if (p0 == saveBtn) {

            monthlyDays = ""
            monthlyIds = ""
            flag = true
            monthlyDayss!!.clear()

            // getting these month id's
            for (i in monthlyListChoose!!) {
                monthlyIds += "" + i.monthlyId + " "

            }

            for (x in stk) {
                if (x.getTag() as Int == 0)
                    continue
                var y = x as TableRow
                var s: Spinner = y.getChildAt(0) as Spinner
                var value = s.selectedItem

                when (value) {
                    "Month 1" -> value = 0
                    "Month 2" -> value = 1
                    "Month 3" -> value = 2
                    "Month 4" -> value = 3


                }

                monthlyDayss!!.add(value as Int)


            }


            val arr = IntArray(12)

            for (i in monthlyDayss!!) {
                if (arr[i] != 0) {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)

                    builder.setTitle("Adding Yearly")
                    builder.setMessage("You cannot have a duplicate of the same Month.")

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
                    monthlyDays += "" + i + " "
                }

            }

            if (flag) {
                var m = YearlySchedule(
                    1,
                    UserInterFace.userID,
                    monthlyDays,
                    monthlyIds,
                    totalCostDobule,
                    false
                )
                var s = AsynTaskNew(m, childFragmentManager)
                s.execute()
                MyYearlyScheudleFragment.getInstance1().getRecycler().toCopy(m)
                clearTable()
            }
        }


    }

    private fun clearTable() {
        tablePosition = 1
        totalCostDobule = 0.0
        monthlyDays = ""
        monthlyIds = ""
        monthlyDayss!!.clear()
        monthlyListChoose!!.clear()
        totalCost.setText(totalCostDobule.toString())
        var j = 1
        for (x in stk) {
            stk.removeView(stk.getChildAt(j))
        }
        stk.removeView(stk.getChildAt(j))

    }


    override fun onDismiss(p0: DialogInterface?) {

        for (i in monthlyyID!!.list!!) {


            var monthly = monthlyList!!.get(i)
            monthlyListChoose!!.add(monthly!!)


        }
        var j = 0
        for (i in monthlyListChoose!!) {

            // saved size to know the size of list before we change  so we wont override all the list.
            if (j > savedSize - 1) {


                stk.setColumnShrinkable(3, false)
                stk.setColumnShrinkable(2, false)
                stk.setColumnStretchable(3, false)
                stk.setColumnStretchable(2, false)


                var tbrow: TableRow = TableRow(this.context)
                tbrow.setTag(tablePosition)

                totalCostDobule += monthlyListChoose!!.get(tablePosition - 1).totalCost
                totalCost.setText(totalCostDobule.toString())

                var t1v: Spinner = Spinner(context)
                t1v.setTag(tablePosition - 1)


                ArrayAdapter.createFromResource(
                    this.requireContext(),
                    R.array.months,
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
                        "Month 1" -> value = 0
                        "Month 2" -> value = 1
                        "Month 3" -> value = 2
                        "Month 4" -> value = 3


                    }
                    var k = value as Int
                    t1v.setSelection(k + 1)
                } else {
                    t1v.setSelection(0)
                }

                tbrow.addView(t1v)

                var t2v: TextView = TextView(context)


                t2v.setText(monthlyListChoose!!.get(tablePosition - 1).monthlyId.toString())
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

                    totalCostDobule -= monthlyListChoose!!.get(t3v.getTag() as Int - 1).totalCost
                    totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
                    totalCost.setText(totalCostDobule.toString())
                    monthlyListChoose!!.removeAt(t3v.getTag() as Int - 1)




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
                    weeklyList = ArrayList()


                    monthlyId = monthlyListChoose!!.get(t3v.getTag() as Int - 1).monthlyId
                    weeklyIds = monthlyListChoose!!.get(t3v.getTag() as Int - 1).weeklyIds
                    numOfWeek = monthlyListChoose!!.get(t3v.getTag() as Int - 1).numOfWeek
                    totalCost
                    pos = t3v.getTag() as Int
                    totalCostMonthly = monthlyListChoose!!.get(t3v.getTag() as Int - 1).totalCost

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
        var string = UserInterFace.userID.toString() + " " + monthlyId


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
//            var weeklyIdsArr = weeklyIds.splitIgnoreEmpty(" ")
//            currentWeeklyID = -1
//            for (i in weeklyIdsArr) {
//
//                for (j in weeklyInfo.indices) {
//                    var weeklyInfo2 = weeklyInfo[j].splitIgnoreEmpty("*")
//                    if (currentWeeklyID != weeklyInfo2[0].toInt() && i.toInt() == weeklyInfo2[0].toInt()) {
//                        weeklyList!!.add(
//                            WeeklySchedule(
//                                weeklyInfo2[0].toInt(),
//                                weeklyInfo2[1].toInt(),
//                                map.get(weeklyInfo2[0])!!.get(0),
//                                map.get(weeklyInfo2[0])!!.get(1),
//                                mapTotalCost.get(weeklyInfo2[0])!!,
//                                false
//
//                            )
//                        )
//                        currentWeeklyID = weeklyInfo2[0].toInt()
//                    }
//
//
//                }
//            }

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

