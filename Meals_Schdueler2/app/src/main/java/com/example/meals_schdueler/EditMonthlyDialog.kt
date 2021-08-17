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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.meals_schdueler.dummy.DailySchedule
import java.text.DecimalFormat


class EditMonthlyDialog(
    weeklyList: ArrayList<WeeklySchedule>,
    numOfWeek: String,
    weeklyIds: String,
    pos: Int,
    monthlyId: Int,
) : DialogFragment(), DialogInterface.OnDismissListener, View.OnClickListener {


    private lateinit var stk: TableLayout
    private lateinit var totalCost: EditText
    private lateinit var exit: ImageView
    private lateinit var title: TextView
    private lateinit var chooseBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var numOfWeekArrlist: ArrayList<Int>
    private lateinit var weeklyIdsArrlist: ArrayList<Int>
    private lateinit var weeklyDayss: ArrayList<Int>
    private var weeklyID: Recipe_Ingredients_List? = null
    private var listWeeklyIdChoosen: ArrayList<Int>? = null


    private var monthlyId = monthlyId
    private var numOfWeek = numOfWeek
    private var weeklyIds = weeklyIds
    private var weeklyList = weeklyList
    private var tablePosition = 1
    private var totalCostDobule: Double = 0.0
    private var position = pos
    private var weeklyPos = 0
    private var savedSize = 0
    private var flag = true // this flag is for the duplicated weeks check



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var x: View = inflater.inflate(R.layout.edit_monthly, container, false)
        stk = x.findViewById(R.id.tableLayout)
        totalCost = x.findViewById(R.id.editTextTotalCost)
        title = x.findViewById(R.id.editNumberOfMonthlyTextView)
        title.setText("Edit Monthly No. " + position)
        weeklyIdsArrlist = ArrayList()
        numOfWeekArrlist = ArrayList()
        weeklyDayss = ArrayList()
        listWeeklyIdChoosen = ArrayList()
        weeklyID = Recipe_Ingredients_List(listWeeklyIdChoosen)
        chooseBtn = x.findViewById(R.id.chooseBtn)
        saveBtn = x.findViewById(R.id.saveBtn)


        chooseBtn.setOnClickListener(this)
        saveBtn.setOnClickListener(this)

        exit = x.findViewById(R.id.imageViewX)
        exit.setOnClickListener(this)

        addTable()
        initMonthly()
        return x


    }

    override fun onDismiss(dialog: DialogInterface) {
        // super.onDismiss(dialog)
        if (flag) {
            weeklyPos = savedSize
            val parentFragment: Fragment? = parentFragment
            if (parentFragment is DialogInterface.OnDismissListener) {
                (parentFragment as DialogInterface.OnDismissListener?)!!.onDismiss(dialog)
            }


            for (i in weeklyID!!.list!!) {


                var weekly = UserPropertiesSingelton.getInstance()!!.getUserWeekly()!!.get(i.toString())
                weeklyList.add(weekly!!)
                //   numOfDayArrlist.add(0)


            }


            var j = savedSize

            for (i in j..weeklyList.size - 1) {
//
//                if (j >= numOfDayArrlist.size) {
//                    break

//                }


                var tbrow: TableRow = TableRow(this.context)
                tbrow.setTag(tablePosition++)


                var t1v: Spinner = Spinner(context)
                weeklyIdsArrlist.add(weeklyList.get(i).weeklyId)
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

                if (j > 0) {
                    var o = stk.get(stk.size-1)
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


                t2v.setText(" " + (weeklyList.get(i).weeklyId))
                t2v.setTextColor(Color.BLACK)
                t2v.gravity = Gravity.CENTER

                tbrow.addView(t2v)

                var t4v: Button = Button(context)
                t4v.setTag(weeklyPos)
                t4v.setText("Delete")
                t4v.setTextSize(8F)
                t4v.setTextColor(Color.BLACK)
                t4v.gravity = Gravity.CENTER
                tbrow.addView(t4v)

                t4v.setOnClickListener {

                    var i = t4v.getTag() as Int


                    stk.removeView(stk.getChildAt(tbrow.getTag() as Int))
                    totalCostDobule -= weeklyList!!.get(t4v.getTag() as Int).totalCost
                    totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
                    totalCost.setText(totalCostDobule.toString())
                    weeklyList!!.removeAt(t4v.getTag() as Int)
                    weeklyIdsArrlist.removeAt(t4v.getTag() as Int)

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
                t3v.setTag(weeklyPos++)
                t3v.setText("Info")
                t3v.setTextSize(10F)
                t3v.setTextColor(Color.BLACK)
                t3v.gravity = Gravity.CENTER

                //t5v.setBackgroundResource(R.drawable.spinner_shape)
                t3v.setOnClickListener {
                    //NEED TO CHECK HERE WHATS WRONG with info button!!!!!
                    // getting this daily recipes
                    var dailyList: ArrayList<DailySchedule> = ArrayList()
                    var ids = weeklyList.get(t3v.getTag() as Int).dailyIds.splitIgnoreEmpty(" ")

                    //going through the list and get each recipe by its id
                    for (i in ids) {
                        dailyList.add(UserPropertiesSingelton.getInstance()!!.getUserDaily()!!.get(i)!!)

                    }

                    var dialog = WeeklyDialogInfo(
                        dailyList,
                        weeklyList.get(i).numOfDay,
                        weeklyList.get(i).dailyIds,
                        weeklyList.get(i).totalCost,
                        t3v.getTag() as Int + 1

                    )


                    dialog!!.show(childFragmentManager, "DailyDialogInfo")
                }
                tbrow.addView(t3v)



                stk.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.setBackgroundResource(R.drawable.spinner_shape)
                stk.addView(tbrow)
                j++
                totalCostDobule += weeklyList.get(i).totalCost


            }

            weeklyPos++

            totalCostDobule = (DecimalFormat("##.####").format(totalCostDobule)).toDouble()
            totalCost.setText(totalCostDobule.toString())
            savedSize = weeklyList.size


        }
    }

    private fun initMonthly() {
        var j = 0
        var k = 0
        weeklyPos = 0

        // converting the strings into arr's
        var numOfWeekArr = numOfWeek.splitIgnoreEmpty(" ")
        var weeklyIdsArr = weeklyIds.splitIgnoreEmpty(" ")

//        for (i in numOfDayArr) {
//            dailyDayss.add(i.toInt())
//
//        }

        for (i in weeklyIdsArr) {
            weeklyIdsArrlist.add(i.toInt())
        }


        for (i in weeklyList) {

            if (j >= weeklyIdsArr.size) {
                break
            }

            if (i.weeklyId == weeklyIdsArr[j].toInt()) {


                var t1v: Spinner = Spinner(context)
                t1v.setTag(tablePosition - 1)

                var tbrow: TableRow = TableRow(this.context)
                tbrow.setTag(tablePosition++)


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



                t1v.setSelection(numOfWeekArr[k++].toInt())


                tbrow.addView(t1v)


                var t2v: TextView = TextView(context)


                t2v.setText(" " + (i.weeklyId))
                t2v.setTextColor(Color.BLACK)
                t2v.gravity = Gravity.CENTER

                tbrow.addView(t2v)

                var t4v: Button = Button(context)
                t4v.setTag(weeklyPos)
                t4v.setText("Delete")
                t4v.setTextSize(10F)
                t4v.setTextColor(Color.BLACK)
                t4v.gravity = Gravity.CENTER
                tbrow.addView(t4v)

                t4v.setOnClickListener {

                    var i = t4v.getTag() as Int

//                    stk.removeView(stk.getChildAt(t4v.getTag() as Int))
                    stk.removeView(stk.getChildAt(tbrow.getTag() as Int))
                    totalCostDobule -= weeklyList!!.get(t4v.getTag() as Int).totalCost
                    totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
                    totalCost.setText(totalCostDobule.toString())
                    weeklyList!!.removeAt(t4v.getTag() as Int)
                    weeklyIdsArrlist.removeAt(t4v.getTag() as Int)
                    Log.v("Elad1", "Daily days after del" + weeklyDayss)
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
                t3v.setTag(weeklyPos++)
                t3v.setText("Info")
                t3v.setTextSize(10F)
                t3v.setTextColor(Color.BLACK)
                t3v.gravity = Gravity.CENTER

                //t5v.setBackgroundResource(R.drawable.spinner_shape)
                t3v.setOnClickListener {
                    //NEED TO CHECK HERE WHATS WRONG with info button!!!!!
                    // getting this weekly recipes
                    var dailyList: ArrayList<DailySchedule> = ArrayList()
                    var ids = weeklyList.get(t3v.getTag() as Int).dailyIds.splitIgnoreEmpty(" ")

                    var m =0
                    //going through the list and get each recipe by its id
                    for (i in ids) {
                        dailyList.add(UserPropertiesSingelton.getInstance()!!.getUserDaily()!!.get(i)!!)

                    }



                    var dialog = WeeklyDialogInfo(
                        dailyList,
                        i.numOfDay,
                        i.dailyIds,
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
        savedSize = weeklyList.size

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

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }




    override fun onClick(p0: View?) {
        if (p0 == chooseBtn) {
            flag = true
            weeklyID!!.list!!.clear()
            savedSize = weeklyList.size

            var dialog = Weekly_Schedule_Choose_Dialog(
                UserPropertiesSingelton.getInstance()!!.getUserWeekly()!!,
                UserPropertiesSingelton.getInstance()!!.getUserDaily()!!,
                UserPropertiesSingelton.getInstance()!!.getUserRecipess()!!,
                weeklyID!!
            )
            dialog.show(childFragmentManager, "DailySchudleChooseDialog")
        } else if (p0 == saveBtn) {
            if (weeklyIdsArrlist.isNotEmpty()) {

                weeklyDayss.clear()
                flag = true
                numOfWeek = ""
                weeklyIds = ""

                val arr = IntArray(7)

                // getting these weeks id's
                for (i in weeklyIdsArrlist) {
                    weeklyIds += "" + i + " "
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
                    weeklyDayss.add(value as Int)
                    Log.v("Elad1", "YOSIII " + value)


                }


                // getting number of weeks , checking duplicate
                for (i in weeklyDayss) {
                    if (arr[i] != 0) {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

                        builder.setTitle("Edit Weekly")
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
                        numOfWeek += "" + i + " "
                    }

                }



                if (flag) {
                    var weekly = MonthlySchedule(
                        monthlyId,
                        UserInterFace.userID,
                        numOfWeek,
                        weeklyIds,
                        totalCostDobule,
                        true
                    )

                    var s = AsynTaskNew(weekly, childFragmentManager)
                    s.execute()

                    numOfWeek = ""
                    weeklyIds = ""

                }

                // flag = false


            }


        } else if (p0 == exit) {
            dismiss()
        }
    }
}