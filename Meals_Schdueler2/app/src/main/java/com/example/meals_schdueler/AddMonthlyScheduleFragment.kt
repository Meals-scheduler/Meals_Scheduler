package com.example.meals_schdueler

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
        val x = inflater.inflate(R.layout.add_weekly_schedule, null)

        //stam = x.findViewById(R.id.elad)
        stk = x.findViewById(R.id.tableLayout)
        totalCost = x.findViewById(R.id.editTextTotalCost)
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


            for (i in weeklyList!!) {
                weeklyIds += "" + i.weeklyId + " "

            }
            for (i in weeklyDayss!!) {
                weeklyDays += "" + i + " "
            }

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

    private fun clearTable() {
        tablePosition = 1
        totalCostDobule = 0.0
        totalCost.setText(totalCostDobule.toString())
        stk.setColumnShrinkable(4, false)
        stk.setColumnShrinkable(3, false)
        stk.setColumnStretchable(3, false)
        stk.setColumnStretchable(4, false)
        var j = 1
        for (x in stk) {
            stk.removeView(stk.getChildAt(j))
        }

    }


    override fun onDismiss(p0: DialogInterface?) {


        if (!weeklyID!!.list!!.isEmpty()) {
            /// need to clear dailyID.list before coming here (in choose button event)
            var weekly =
                UserPropertiesSingelton.getInstance()!!.getUserWeekly()!!
                    .get(weeklyID!!.list!!.get(0))
            weeklyList!!.add(weekly)



            stk.setColumnShrinkable(3, true)
            stk.setColumnShrinkable(4, true)
            stk.setColumnStretchable(3, true)
            stk.setColumnStretchable(4, true)


            var tbrow: TableRow = TableRow(this.context)
            tbrow.setTag(tablePosition)

            totalCostDobule += weeklyList!!.get(tablePosition - 1).totalCost
            totalCost.setText(totalCostDobule.toString())

            var t1v: Spinner= Spinner(context)


            ArrayAdapter.createFromResource(
                this.requireContext(),
                R.array.days,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                t1v.adapter = adapter
            }

            t1v.onItemSelectedListener = SpinnerActivity()
            // t1v.setBackgroundResource(R.drawable.border)
//        t1v.setText(" " + (mealChoosen))
//        t1v.setTextColor(Color.BLACK)
//        t1v.gravity = Gravity.CENTER
            //  t1v.setBackgroundResource(R.drawable.spinner_shape)

            tbrow.addView(t1v)

            var t2v: TextView = TextView(context)

            // t1v.setBackgroundResource(R.drawable.border)
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

            tbrow.addView(t3v)

            var t4v: Button = Button(context)
            t4v.setTag(tablePosition++)
            t4v.setText("Info")
            t4v.setTextSize(10F)
            t4v.setTextColor(Color.BLACK)
            t4v.gravity = Gravity.CENTER

            t4v.setOnClickListener {
                // making the Recipe list to transfer to the info dialog
                recipeList = ArrayList()

                // taking the Recipe Id's string from this current daily object
                var recipeIDS = dailyList!!.get(t3v.getTag() as Int - 1).recipeIds
                // split it so i can make a list from it
                var recipeIDs = recipeIDS.splitIgnoreEmpty(" ")

                //going through the list and get each recipe by its id
                for (i in UserPropertiesSingelton.getInstance()!!.getUserRecipess()!!) {
                    if (recipeIDs.contains(i.recipeId.toString())) {
                        recipeList!!.add(i)
                    }
                }



                var dialog = DailyDialogInfo(
                    recipeList!!,
                    dailyList!!.get(t3v.getTag() as Int - 1).quantities,
                    dailyList!!.get(t3v.getTag() as Int - 1).numOfMeals,
                    dailyList!!.get(t3v.getTag() as Int - 1).recipeIds,
                    (t4v.getTag() as Int - 1) + 1,
                    dailyList!!.get(t3v.getTag() as Int - 1).dailyId

                )
                dialog.show(childFragmentManager, "DailyDialogInfo")
            }
            tbrow.addView(t4v)


            stk.setBackgroundResource(R.drawable.spinner_shape)
            tbrow.setBackgroundResource(R.drawable.spinner_shape)
            stk.addView(tbrow)


        }

    }
}