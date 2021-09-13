package com.example.meals_schdueler

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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.meals_schdueler.dummy.DailySchedule
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat

class EditYearlyDialog(

    monthlyList: ArrayList<MonthlySchedule>,
    numOfMonth: String,
    monthlyIds: String,
    pos: Int,
    yearlyId: Int,
) : DialogFragment(), DialogInterface.OnDismissListener, View.OnClickListener, GetAndPost {


    private lateinit var stk: TableLayout
    private lateinit var totalCost: EditText
    private lateinit var exit: ImageView
    private lateinit var title: TextView
    private lateinit var chooseBtn: Button
    private lateinit var saveBtn: Button


    private lateinit var numOfMonthArrlist: ArrayList<Int>
    private lateinit var monthlyIdsArrlist: ArrayList<Int>
    private lateinit var monthlyDayss: ArrayList<Int>
    private var monthlyID: Recipe_Ingredients_List? = null
    private var listMonthlyIdChoosen: ArrayList<Int>? = null

    private var monthlyListChoose: ArrayList<MonthlySchedule>? = null
    private var weeklyList: ArrayList<WeeklySchedule>? = null

    private var yearlyId = yearlyId
    private var numOfMonth = numOfMonth
    private var monthlyIds = monthlyIds
    private var monthlyList = monthlyList
    private var tablePosition = 1
    private var totalCostDobule: Double = 0.0
    private var position = pos
    private var monthlyPos = 0
    private var savedSize = 0
    private var flag = true // this flag is for the duplicated months check

    private var monthlyId = -1
    private var numOfWeek = ""
    private var weeklyIds = ""
    private var pos = -1
    private var totalCostMonthly = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var x: View = inflater.inflate(R.layout.edit_yearly, container, false)
        stk = x.findViewById(R.id.tableLayout)
        totalCost = x.findViewById(R.id.editTextTotalCost)
        title = x.findViewById(R.id.editNumberOfYearlyTextView)
        title.setText("Edit Yearly No. " + position)
        monthlyIdsArrlist = ArrayList()
        numOfMonthArrlist = ArrayList()
        monthlyDayss = ArrayList()
        listMonthlyIdChoosen = ArrayList()
        monthlyListChoose = ArrayList()

        monthlyID = Recipe_Ingredients_List(listMonthlyIdChoosen)
        chooseBtn = x.findViewById(R.id.chooseBtn)
        saveBtn = x.findViewById(R.id.saveBtn)


        chooseBtn.setOnClickListener(this)
        saveBtn.setOnClickListener(this)

        exit = x.findViewById(R.id.imageViewX)
        exit.setOnClickListener(this)

        addTable()
        initYearly()
        return x


    }

    private fun initYearly() {
        var j = 0
        var k = 0
        monthlyPos = 0

        // converting the strings into arr's
        var numOfMonthArr = numOfMonth.splitIgnoreEmpty(" ")
        var monthlydsArr = monthlyIds.splitIgnoreEmpty(" ")

//        for (i in numOfDayArr) {
//            dailyDayss.add(i.toInt())
//
//        }

        for (i in monthlydsArr) {
            monthlyIdsArrlist.add(i.toInt())
        }


        for (i in monthlyList) {

            if (j >= monthlydsArr.size) {
                break
            }

            if (i.monthlyId == monthlydsArr[j].toInt()) {


                var t1v: Spinner = Spinner(context)
                t1v.setTag(tablePosition - 1)

                var tbrow: TableRow = TableRow(this.context)
                tbrow.setTag(tablePosition++)


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



                t1v.setSelection(numOfMonthArr[k++].toInt())


                tbrow.addView(t1v)


                var t2v: TextView = TextView(context)


                t2v.setText(" " + (i.monthlyId))
                t2v.setTextColor(Color.BLACK)
                t2v.gravity = Gravity.CENTER

                tbrow.addView(t2v)

                var t4v: Button = Button(context)
                t4v.setTag(monthlyPos)
                t4v.setText("Delete")
                t4v.setTextSize(10F)
                t4v.setTextColor(Color.BLACK)
                t4v.gravity = Gravity.CENTER
                tbrow.addView(t4v)

                t4v.setOnClickListener {

                    var i = t4v.getTag() as Int

//                    stk.removeView(stk.getChildAt(t4v.getTag() as Int))
                    stk.removeView(stk.getChildAt(tbrow.getTag() as Int))
                    totalCostDobule -= monthlyList!!.get(t4v.getTag() as Int).totalCost
                    totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
                    totalCost.setText(totalCostDobule.toString())
                    monthlyList!!.removeAt(t4v.getTag() as Int)
                    monthlyIdsArrlist.removeAt(t4v.getTag() as Int)
                    // Log.v("Elad1", "Daily days after del" + monthlyDayss)
                    // CONTINUE HERE
                    //  dailyDayss.remove()
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

                    stk.setColumnShrinkable(3, false)
                    stk.setColumnStretchable(3, false)

                }


                var t3v: Button = Button(context)
                t3v.setTag(monthlyPos++)
                t3v.setText("Info")
                t3v.setTextSize(10F)
                t3v.setTextColor(Color.BLACK)
                t3v.gravity = Gravity.CENTER

                //t5v.setBackgroundResource(R.drawable.spinner_shape)
                t3v.setOnClickListener {
                    //NEED TO CHECK HERE WHATS WRONG with info button!!!!!
                    // getting this weekly recipes
                    weeklyList = ArrayList()


                    monthlyId = i.monthlyId

                    numOfWeek = i.numOfWeek
                    weeklyIds = i.weeklyIds
                    pos = t3v.getTag() as Int + 1
                    totalCostMonthly = i.totalCost


                    var s = AsynTaskNew(this, childFragmentManager)
                    s.execute()
                }
                tbrow.addView(t3v)



                stk.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.setBackgroundResource(R.drawable.spinner_shape)
                stk.addView(tbrow)
                j++
                totalCostDobule += i.totalCost

            }

            //dailyPos++
        }

        totalCostDobule = (DecimalFormat("##.####").format(totalCostDobule)).toDouble()
        totalCost.setText(totalCostDobule.toString())
        savedSize = monthlyList.size


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

    override fun onDismiss(dialog: DialogInterface) {
        // super.onDismiss(dialog)
        if (flag) {
            monthlyPos = savedSize
            val parentFragment: Fragment? = parentFragment
            if (parentFragment is DialogInterface.OnDismissListener) {
                (parentFragment as DialogInterface.OnDismissListener?)!!.onDismiss(dialog)
            }


            for (i in monthlyID!!.list!!) {


                var monthly = monthlyList!!.get(i)
                monthlyListChoose!!.add(monthly!!)


            }


            var j = 0

            for (i in monthlyListChoose!!) {
                if (j > savedSize - 1) {


                    var tbrow: TableRow = TableRow(this.context)
                    tbrow.setTag(tablePosition++)


                    var t1v: Spinner = Spinner(context)
                    monthlyIdsArrlist.add(i.monthlyId)
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

                    if (j >= 0) {
                        var o = stk.get(stk.size - 1)
                        var y = o as TableRow
                        var s: Spinner = y.getChildAt(0) as Spinner
                        var value = s.selectedItem

                        when (value) {
                            "Month 1" -> value = 0
                            "Month 2" -> value = 1
                            "Month 3" -> value = 2
                            "Month 4" -> value = 3
                            "Month 5" -> value = 4
                            "Month 6" -> value = 5
                            "Month 7" -> value = 6
                            "Month 8" -> value = 7
                            "Month 9" -> value = 8
                            "Month 10" -> value = 9
                            "Month 11" -> value = 10
                            "Month 12" -> value = 11

                        }
                        var k = value as Int
                        t1v.setSelection(k + 1)
                    } else {
                        t1v.setSelection(0)
                    }


                    tbrow.addView(t1v)


                    var t2v: TextView = TextView(context)


                    t2v.setText(" " + i.monthlyId)
                    t2v.setTextColor(Color.BLACK)
                    t2v.gravity = Gravity.CENTER

                    tbrow.addView(t2v)

                    var t4v: Button = Button(context)
                    t4v.setTag(monthlyPos)
                    t4v.setText("Delete")
                    t4v.setTextSize(8F)
                    t4v.setTextColor(Color.BLACK)
                    t4v.gravity = Gravity.CENTER
                    tbrow.addView(t4v)

                    t4v.setOnClickListener {

                        var i = t4v.getTag() as Int


                        stk.removeView(stk.getChildAt(tbrow.getTag() as Int))
                        totalCostDobule -= monthlyListChoose!!.get(t4v.getTag() as Int).totalCost
                        totalCostDobule =
                            (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
                        totalCost.setText(totalCostDobule.toString())
                        monthlyListChoose!!.removeAt(t4v.getTag() as Int)
                        monthlyIdsArrlist.removeAt(t4v.getTag() as Int)

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

                        stk.setColumnShrinkable(3, false)
                        stk.setColumnStretchable(3, false)
                    }


                    var t3v: Button = Button(context)
                    t3v.setTag(monthlyPos++)
                    t3v.setText("Info")
                    t3v.setTextSize(10F)
                    t3v.setTextColor(Color.BLACK)
                    t3v.gravity = Gravity.CENTER

                    //t5v.setBackgroundResource(R.drawable.spinner_shape)
                    t3v.setOnClickListener {
                        weeklyList = ArrayList()

                        monthlyId = i.monthlyId

                        numOfWeek = i.numOfWeek
                        weeklyIds = i.weeklyIds
                        pos = t3v.getTag() as Int + 1
                        totalCostMonthly = i.totalCost


                        var s = AsynTaskNew(this, childFragmentManager)
                        s.execute()
                    }
                    tbrow.addView(t3v)



                    stk.setBackgroundResource(R.drawable.spinner_shape)
                    tbrow.setBackgroundResource(R.drawable.spinner_shape)
                    stk.addView(tbrow)
                    j++
                    totalCostDobule += i.totalCost


                }
            }

            monthlyPos++
            j++
            totalCostDobule = (DecimalFormat("##.####").format(totalCostDobule)).toDouble()
            totalCost.setText(totalCostDobule.toString())
            savedSize = monthlyListChoose!!.size


        }
    }

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    override fun onClick(p0: View?) {
        if (p0 == chooseBtn) {
            flag = true
            monthlyID!!.list!!.clear()
            savedSize = monthlyListChoose!!.size
            monthlyList.clear()

            var dialog = Monthly_Schedule_Choose_Dialog(
                monthlyList,

                monthlyID!!
            )
            dialog.show(childFragmentManager, "DailySchudleChooseDialog")
        } else if (p0 == saveBtn) {
            if (monthlyIdsArrlist.isNotEmpty()) {

                monthlyDayss.clear()
                flag = true
                numOfMonth = ""
                monthlyIds = ""

                val arr = IntArray(7)

                // getting these weeks id's
                for (i in monthlyIdsArrlist) {
                    monthlyIds += "" + i + " "
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
                        "Month 5" -> value = 4
                        "Month 6" -> value = 5
                        "Month 7" -> value = 6
                        "Month 8" -> value = 7
                        "Month 9" -> value = 8
                        "Month 10" -> value = 9
                        "Month 11" -> value = 10
                        "Month 12" -> value = 11

                    }
                    monthlyDayss.add(value as Int)
                    Log.v("Elad1", "YOSIII " + value)


                }


                // getting number of weeks , checking duplicate
                for (i in monthlyDayss) {
                    if (arr[i] != 0) {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

                        builder.setTitle("Edit Weekly")
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
                        numOfMonth += "" + i + " "
                    }

                }



                if (flag) {
                    var weekly = YearlySchedule(
                        yearlyId,
                        UserInterFace.userID,
                        numOfMonth,
                        monthlyIds,
                        totalCostDobule,
                        true
                    )

                    var s = AsynTaskNew(weekly, childFragmentManager)
                    s.execute()

                    numOfMonth = ""
                    monthlyIds = ""

                }

                // flag = false


            }


        } else if (p0 == exit) {
            dismiss()
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
            var weeklyIdsArr = weeklyIds.splitIgnoreEmpty(" ")
            currentWeeklyID = -1
            for (i in weeklyIdsArr) {

                for (j in weeklyInfo.indices) {
                    var weeklyInfo2 = weeklyInfo[j].splitIgnoreEmpty("*")
                    if (currentWeeklyID != weeklyInfo2[0].toInt() && i.toInt() == weeklyInfo2[0].toInt()) {
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
                        currentWeeklyID = weeklyInfo2[0].toInt()
                    }


                }
            }


            var dialog = MonthlyDialogInfo(

                weeklyList!!,
                numOfWeek,
                weeklyIds,
                totalCostMonthly,
                pos
            )


            dialog.show(childFragmentManager, "WeeklyDialogInfo")


        }

    }
}

