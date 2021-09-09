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
import java.text.DecimalFormat

class EditYearlyDialog(

    monthlyList: ArrayList<MonthlySchedule>,
    numOfMonth: String,
    monthlyIds: String,
    pos: Int,
    yearlyId: Int,
) : DialogFragment(), DialogInterface.OnDismissListener, View.OnClickListener {


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
                    var weekList: ArrayList<WeeklySchedule> = ArrayList()
                    var ids = monthlyList.get(t3v.getTag() as Int).weeklyIds.splitIgnoreEmpty(" ")


                    //going through the list and get each recipe by its id
                    for (i in ids) {
                        weekList.add(
                            UserPropertiesSingelton.getInstance()!!.getUserWeekly()!!.get(i)!!
                        )

                    }


                    var dialog = MonthlyDialogInfo(
                        weekList,
                        i.numOfWeek,
                        i.weeklyIds,
                        i.totalCost,
                        t3v.getTag() as Int + 1,

                        )


                    dialog!!.show(childFragmentManager, "DailyDialogInfo")
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


                var monthly =
                    UserPropertiesSingelton.getInstance()!!.getUserMonthly()!!.get(i.toString())
                monthlyList.add(monthly!!)
                //   numOfDayArrlist.add(0)


            }


            var j = savedSize

            for (i in j..monthlyList.size - 1) {
//
//                if (j >= numOfDayArrlist.size) {
//                    break

//                }


                var tbrow: TableRow = TableRow(this.context)
                tbrow.setTag(tablePosition++)


                var t1v: Spinner = Spinner(context)
                monthlyIdsArrlist.add(monthlyList.get(i).monthlyId)
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

                if (j > 0) {
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


                t2v.setText(" " + (monthlyList.get(i).monthlyId))
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
                    totalCostDobule -= monthlyList!!.get(t4v.getTag() as Int).totalCost
                    totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
                    totalCost.setText(totalCostDobule.toString())
                    monthlyList!!.removeAt(t4v.getTag() as Int)
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
                    //NEED TO CHECK HERE WHATS WRONG with info button!!!!!
                    // getting this daily recipes
                    var weeklyList: ArrayList<WeeklySchedule> = ArrayList()
                    var ids = monthlyList.get(t3v.getTag() as Int).weeklyIds.splitIgnoreEmpty(" ")

                    //going through the list and get each recipe by its id
                    for (i in ids) {
                        weeklyList.add(
                            UserPropertiesSingelton.getInstance()!!.getUserWeekly()!!.get(i)!!
                        )

                    }

                    var dialog = MonthlyDialogInfo(
                        weeklyList,
                        monthlyList.get(i).numOfWeek,
                        monthlyList.get(i).weeklyIds,
                        monthlyList.get(i).totalCost,
                        t3v.getTag() as Int + 1

                    )


                    dialog!!.show(childFragmentManager, "MonthlyDialogInfo")
                }
                tbrow.addView(t3v)



                stk.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.setBackgroundResource(R.drawable.spinner_shape)
                stk.addView(tbrow)
                j++
                totalCostDobule += monthlyList.get(i).totalCost


            }

            monthlyPos++

            totalCostDobule = (DecimalFormat("##.####").format(totalCostDobule)).toDouble()
            totalCost.setText(totalCostDobule.toString())
            savedSize = monthlyList.size


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
            savedSize = monthlyList.size

            var dialog = Monthly_Schedule_Choose_Dialog(
                monthlyList,
//                UserPropertiesSingelton.getInstance()!!.getUserDaily()!!,
//                UserPropertiesSingelton.getInstance()!!.getUserRecipess()!!,
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

}