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
import java.text.DecimalFormat


class AddMonthlyScheduleFragment : Fragment(), View.OnClickListener,
    DialogInterface.OnDismissListener {


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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }


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

            var d = Weekly_Schedule_Choose_Dialog(
                UserPropertiesSingelton.getInstance()!!.getUserWeekly()!!,
                UserPropertiesSingelton.getInstance()!!.getUserDaily()!!,
                UserPropertiesSingelton.getInstance()!!.getUserRecipess()!!,
                weeklyID!!
            )
            d.show(childFragmentManager, "DailySchudleChooseDialog")
        } else if (p0 == saveBtn) {

            weeklyDays = ""
            weeklyIds=""
            flag = true
            weeklyDayss!!.clear()

            // getting these weekks id's
            for (i in weeklyList!!) {
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


            val arr = IntArray(7)

            for (i in weeklyDayss!!) {
                if (arr[i] != 0) {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)

                    builder.setTitle("Adding Weekly")
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

                clearTable()
            }
        }


    }

    private fun clearTable() {
        tablePosition = 1
        totalCostDobule = 0.0
        weeklyDays = ""
        weeklyIds=""
        weeklyDayss!!.clear()
        weeklyList!!.clear()
        totalCost.setText(totalCostDobule.toString())
        var j = 1
        for (x in stk) {
            stk.removeView(stk.getChildAt(j))
        }
        stk.removeView(stk.getChildAt(j))

    }


    override fun onDismiss(p0: DialogInterface?) {


        if (!weeklyID!!.list!!.isEmpty()) {
            /// need to clear dailyID.list before coming here (in choose button event)
            var weekly =
                UserPropertiesSingelton.getInstance()!!.getUserWeekly()!!
                    .get(weeklyID!!.list!!.get(0).toString())
            weeklyList!!.add(weekly!!)



            stk.setColumnShrinkable(3, false)
            stk.setColumnShrinkable(2, false)
            stk.setColumnStretchable(3, false)
            stk.setColumnStretchable(2, false)


            var tbrow: TableRow = TableRow(this.context)
            tbrow.setTag(tablePosition)

            totalCostDobule += weeklyList!!.get(tablePosition - 1).totalCost
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


            t2v.setText(weeklyList!!.get(tablePosition - 1).weeklyId.toString())
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

                totalCostDobule -= weeklyList!!.get(t3v.getTag() as Int - 1).totalCost
                totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
                totalCost.setText(totalCostDobule.toString())
                weeklyList!!.removeAt(t3v.getTag() as Int - 1)




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

                var dailyList: ArrayList<DailySchedule> = ArrayList()
                var dailyArr =
                    weeklyList!!.get(t3v.getTag() as Int - 1).dailyIds.splitIgnoreEmpty(" ")
                for (i in dailyArr) {
                    dailyList.add(UserPropertiesSingelton.getInstance()!!.getUserDaily()!!.get(i)!!)

                }



                var dialog = WeeklyDialogInfo(
                    dailyList,
                    weeklyList!!.get(t3v.getTag() as Int - 1).numOfDay,
                    weeklyList!!.get(t3v.getTag() as Int - 1).dailyIds,
                    weeklyList!!.get(t3v.getTag() as Int - 1).totalCost,
                    t3v.getTag() as Int
                )
                dialog.show(childFragmentManager, "DailyDialogInfo")
            }
            tbrow.addView(t4v)


            stk.setBackgroundResource(R.drawable.spinner_shape)
            tbrow.setBackgroundResource(R.drawable.spinner_shape)
            stk.addView(tbrow)


        }

    }




    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }


}