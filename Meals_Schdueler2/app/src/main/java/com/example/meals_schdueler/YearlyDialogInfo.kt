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

class YearlyDialogInfo(

    monthlyList: ArrayList<MonthlySchedule>,
    numOfMonth: String,
    monthlyIds: String,
    totalCost: Double,
    pos: Int,
) : DialogFragment(), View.OnClickListener, DialogInterface.OnDismissListener {

    private lateinit var stk: TableLayout
    private lateinit var totalCost: EditText
    private lateinit var exit: ImageView
    private lateinit var title: TextView

    private var monthlyList = monthlyList

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
            when (numOfMonthArr[j++]) {
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
                //NEED TO CHECK HERE WHATS WRONG with info button!!!!!
                // getting this daily recipes
                var weeklyist: ArrayList<WeeklySchedule> = ArrayList()
                var ids = monthlyList.get(t3v.getTag() as Int).weeklyIds.splitIgnoreEmpty(" ")
                var m = 0
                //going through the list and get each recipe by its id
                for (i in ids) {
                    weeklyist.add(
                        UserPropertiesSingelton.getInstance()!!.getUserWeekly()!!.get(i)!!
                    )

                }

                var dialog = MonthlyDialogInfo(
                    weeklyist,
                    i.numOfWeek,
                    i.weeklyIds,
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

}