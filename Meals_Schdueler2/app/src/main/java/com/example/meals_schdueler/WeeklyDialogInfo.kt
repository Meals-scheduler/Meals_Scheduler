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

class WeeklyDialogInfo(


    dailyList: ArrayList<DailySchedule>,
    numOfDay: String,
    dailyIds: String,
    totalCost: Double,
    pos: Int,
    // weeklyId: Int,

) : DialogFragment(), View.OnClickListener,
    DialogInterface.OnDismissListener {


    private lateinit var stk: TableLayout
    private lateinit var totalCost: EditText
    private lateinit var exit: ImageView
    private lateinit var title: TextView

    private var dailyList = dailyList

    //  private var weeklyList = weeklyList
    // private var weeklyId = weeklyId
    private var totalCostt = totalCost
    private var numOfDay = numOfDay
    private var dailyIds = dailyIds

    //   private var totalCost = totalCost
    private var tablePosition = 1
    private var totalCostDobule: Double = 0.0
    private var position = pos
    private var dailyPos = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var x: View = inflater.inflate(R.layout.weekly_info, container, false)
        stk = x.findViewById(R.id.tableLayout)
        totalCost = x.findViewById(R.id.editTextTotalCost)
        title = x.findViewById(R.id.weeklyInfo)
        title.setText("Week No. " + position)


        exit = x.findViewById(R.id.imageViewX)
        exit.setOnClickListener(this)

        addTable()
        initWeekly()
        return x


    }

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    private fun initWeekly() {


        // converting the strings into arr's
        var numOfDayArr = numOfDay.splitIgnoreEmpty(" ")
        var dailyIdsArr = dailyIds.splitIgnoreEmpty(" ")

        totalCostDobule += totalCostt
        totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
        totalCost.setText(totalCostDobule.toString())


        var j = 0
        for (i in dailyList) {


            var tbrow: TableRow = TableRow(this.context)
            tbrow.setTag(tablePosition++)


            var t1v: TextView = TextView(context)

            // t1v.setBackgroundResource(R.drawable.border)
            var dayChoosen = ""
            when (numOfDayArr[j++]) {
                "0" -> dayChoosen = "Sunday"
                "1" -> dayChoosen = "Monday"
                "2" -> dayChoosen = "Tuesday"
                "3" -> dayChoosen = "Wednesday"
                "4" -> dayChoosen = "Thursday"
                "5" -> dayChoosen = "Friday"
                "6" -> dayChoosen = "Saturday"

            }

            t1v.setText(" " + (dayChoosen))
            t1v.setTextColor(Color.BLACK)
            t1v.gravity = Gravity.CENTER
            //  t1v.setBackgroundResource(R.drawable.spinner_shape)
            tbrow.addView(t1v)


            var t2v: TextView = TextView(context)


            t2v.setText(" " + (i.dailyId))
            t2v.setTextColor(Color.BLACK)
            t2v.gravity = Gravity.CENTER

            tbrow.addView(t2v)


            var t3v: Button = Button(context)
            t3v.setTag(dailyPos++)
            t3v.setText("Info")
            t3v.setTextSize(10F)
            t3v.setTextColor(Color.BLACK)
            t3v.gravity = Gravity.CENTER

            //t5v.setBackgroundResource(R.drawable.spinner_shape)
            t3v.setOnClickListener {
              //NEED TO CHECK HERE WHATS WRONG with info button!!!!!
                // getting this daily recipes
                var k =0
                var recipeList: ArrayList<Recipe> = ArrayList()
                var ids = dailyList.get(t3v.getTag() as Int).recipeIds.splitIgnoreEmpty(" ")
                for (i in ids) {
                    if (UserPropertiesSingelton.getInstance()!!.getUserRecipess()!!.get(k).recipeId == i.toInt()) {
                        recipeList!!.add(UserPropertiesSingelton.getInstance()!!.getUserRecipess()!!.get(k))
                        k=0
                    }
                    else {
                        k++
                    }
                }
                var dialog = DailyDialogInfo(
                    recipeList,
                    i.quantities,
                    i.numOfMeals,
                    i.recipeIds,
                    t3v.getTag() as Int +1,
                    i.dailyId
                )


                dialog!!.show(childFragmentManager, "DailyDialogInfo")
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
        tv0.setText(" Day ")
        tv0.setTextColor(Color.BLACK)
        tv0.gravity = Gravity.CENTER
        //  tv0.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.addView(tv0)

        var tv1: TextView = TextView(context)
        tv1.setText(" DailyId ")
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