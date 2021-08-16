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
import java.text.DecimalFormat


class MonthlyDialogInfo(

    weeklyList: ArrayList<WeeklySchedule>,
    numOfWeek: String,
    weeklyIds: String,
    totalCost: Double,
    pos: Int,
) : DialogFragment(), View.OnClickListener, DialogInterface.OnDismissListener {

    private lateinit var stk: TableLayout
    private lateinit var totalCost: EditText
    private lateinit var exit: ImageView
    private lateinit var title: TextView

    private var weeklyList = weeklyList

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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var x: View = inflater.inflate(R.layout.monthly_info, container, false)
        stk = x.findViewById(R.id.tableLayout)
        totalCost = x.findViewById(R.id.editTextTotalCost)
        title = x.findViewById(R.id.monthlyInfo)
        title.setText("Week No. " + position)


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
            when (numOfWeekArr[j++]) {
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
                //NEED TO CHECK HERE WHATS WRONG with info button!!!!!
                // getting this daily recipes
                var dailyList: ArrayList<DailySchedule> = ArrayList()
                var ids = weeklyList.get(t3v.getTag() as Int).dailyIds.splitIgnoreEmpty(" ")
                var m =0
                //going through the list and get each recipe by its id
                for (i in ids) {
                    if (UserPropertiesSingelton.getInstance()!!.getUserDaily()!!.get(m).dailyId == i.toInt()) {
                        dailyList!!.add(UserPropertiesSingelton.getInstance()!!.getUserDaily()!!.get(m))
                        m=0
                    }
                    else {
                        m++
                    }
                }

                var dialog = WeeklyDialogInfo(
                    dailyList,
                    i.numOfDay,
                    i.dailyIds,
                    i.totalCost,
                    t3v.getTag() as Int + 1
                )



                dialog!!.show(childFragmentManager, "WeeklyDialogInfo")
            }
            tbrow.addView(t3v)



            stk.setBackgroundResource(R.drawable.spinner_shape)
            tbrow.setBackgroundResource(R.drawable.spinner_shape)
            stk.addView(tbrow)


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
}