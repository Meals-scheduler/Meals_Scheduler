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
import androidx.core.view.isNotEmpty
import androidx.core.view.iterator
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.example.meals_schdueler.dummy.DailySchedule
import java.text.DecimalFormat
import kotlin.collections.ArrayList

// NEED TO CHECK  - > WHEN EDITING A DAILY - IF IT CHANGES ITS TOTAL COST OR NOT !!!
class AddWeeklyScheduleFragment : Fragment(), View.OnClickListener,
    DialogInterface.OnDismissListener {

    private var columnCount = 1
    private lateinit var stk: TableLayout
    private lateinit var saveBtn: Button
    private lateinit var chooseDayBtn: Button
    private lateinit var totalCost: EditText
    private var totalCostDobule: Double = 0.0

    // saving DailyID
    private var dailyID: Recipe_Ingredients_List? = null
    private var listDailyIdChoosen: ArrayList<Int>? = null
    private var dailyList: ArrayList<DailySchedule>? = null
    private var dailyDays: String = ""
    private var dailyIds: String = ""
    private var dailyDayss: ArrayList<Int>? = null
    private var recipeList: ArrayList<Recipe>? = null
    private var flag = true // this flag is for the duplicated days check.

    private var tablePosition = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }


        listDailyIdChoosen = ArrayList()
        dailyID = Recipe_Ingredients_List(listDailyIdChoosen)
        dailyList = ArrayList()
        dailyDayss = ArrayList()

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
            AddWeeklyScheduleFragment().apply {
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

        chooseDayBtn = x.findViewById(R.id.chooseBtn)
        totalCost = x.findViewById(R.id.editTextTotalCost)
        saveBtn = x.findViewById(R.id.saveBtn)

        chooseDayBtn.setOnClickListener(this)
        saveBtn.setOnClickListener(this)


        //val view = inflater.inflate(com.example.meals_schdueler.R.layout.add_daily_schedule, container, false)

        addTable()
        return x
    }

    override fun onClick(p0: View?) {

        if (p0 == chooseDayBtn) {
            dailyID!!.list!!.clear()

            var d = Daily_Schedule_Choose_Dialog(
                UserPropertiesSingelton.getInstance()!!.getUserDaily()!!,
                UserPropertiesSingelton.getInstance()!!.getUserRecipess()!!,
                dailyID!!
            )

            d.show(childFragmentManager, "DailySchudleChooseDialog")


        } else if (p0 == saveBtn) {

            if (stk.isNotEmpty()) {

                dailyDays = ""
                dailyIds = ""
                dailyDayss!!.clear()
                flag = true

                val arr = IntArray(7)


                // collecting dailys id's
                for (i in dailyList!!) {
                    dailyIds += "" + i.dailyId + " "

                }

                for (x in stk) {
                    if (x.getTag() as Int == 0)
                        continue
                    var y = x as TableRow
                    var s: Spinner = y.getChildAt(0) as Spinner
                    var value = s.selectedItem

                    when (value) {
                        "Sunday" -> value = 0
                        "Monday" -> value = 1
                        "Tuesday" -> value = 2
                        "Wednesday" -> value = 3
                        "Thursday" -> value = 4
                        "Friday" -> value = 5
                        "Saturday" -> value = 6

                    }

                    dailyDayss!!.add(value as Int)
                    Log.v("Elad1", "YOSIII " + value)


                }

                // collectiing daily days, checking no duplicates
                for (i in dailyDayss!!) {
                    if (arr[i] != 0) {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

                        builder.setTitle("Adding Weekly")
                        builder.setMessage("You cannot have a duplicate of the same day.")

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
                        dailyDays += "" + i + " "
                    }

                }
                if (flag) {
                    var w = WeeklySchedule(
                        1,
                        UserInterFace.userID,
                        dailyDays,
                        dailyIds,
                        totalCostDobule,
                        false
                    )
                    var s = AsynTaskNew(w, childFragmentManager)
                    s.execute()

                    clearTable()
                }
            }
        }

    }

    private fun clearTable() {
        tablePosition = 1
        totalCostDobule = 0.0
        dailyDays = ""
        dailyIds = ""
        dailyList!!.clear()
        totalCost.setText(totalCostDobule.toString())
        stk.setColumnShrinkable(4, false)
        stk.setColumnShrinkable(3, false)
        stk.setColumnStretchable(3, false)
        stk.setColumnStretchable(4, false)
        var j = 1
        for (x in stk) {
            stk.removeView(stk.getChildAt(j))
        }
        stk.removeView(stk.getChildAt(j))

    }

    override fun onDismiss(p0: DialogInterface?) {


        if (!dailyID!!.list!!.isEmpty()) {
            /// need to clear dailyID.list before coming here (in choose button event)
            var daily =
                UserPropertiesSingelton.getInstance()!!.getUserDaily()!!
                    .get(dailyID!!.list!!.get(0).toString())
            dailyList!!.add(daily!!)



            stk.setColumnShrinkable(3, true)
            stk.setColumnShrinkable(4, true)
            stk.setColumnStretchable(3, true)
            stk.setColumnStretchable(4, true)


            var tbrow: TableRow = TableRow(this.context)
            tbrow.setTag(tablePosition)

            totalCostDobule += dailyList!!.get(tablePosition - 1).totalCost
            totalCost.setText(totalCostDobule.toString())

            var t1v: Spinner = Spinner(context)
            t1v.setTag(tablePosition - 1)


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

            // t1v.onItemSelectedListener = SpinnerActivity(t1v.getTag() as Int)
            // t1v.setBackgroundResource(R.drawable.border)
//        t1v.setText(" " + (mealChoosen))
//        t1v.setTextColor(Color.BLACK)
//        t1v.gravity = Gravity.CENTER
            //  t1v.setBackgroundResource(R.drawable.spinner_shape)

            if (stk.size > 1) {
                var o = stk.get(stk.size - 1)
                var y = o as TableRow
                var s: Spinner = y.getChildAt(0) as Spinner
                var value = s.selectedItem

                when (value) {
                    "Sunday" -> value = 0
                    "Monday" -> value = 1
                    "Tuesday" -> value = 2
                    "Wednesday" -> value = 3
                    "Thursday" -> value = 4
                    "Friday" -> value = 5
                    "Saturday" -> value = 6

                }
                var k = value as Int
                t1v.setSelection(k + 1)
            } else {
                t1v.setSelection(0)
            }

            tbrow.addView(t1v)

            var t2v: TextView = TextView(context)

            // t1v.setBackgroundResource(R.drawable.border)
            t2v.setText(dailyList!!.get(tablePosition - 1).dailyId.toString())
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

                totalCostDobule -= dailyList!!.get(t3v.getTag() as Int - 1).totalCost
                totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
                totalCost.setText(totalCostDobule.toString())
                dailyList!!.removeAt(t3v.getTag() as Int - 1)
                // CONTINUE HERE
             //   dailyDayss!!.removeAt(t3v.getTag() as Int - 1)
                Log.v("Elad1", "Daily days after delete" + dailyDayss)
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
                for (i in recipeIDS) {
                    recipeList!!.add(UserPropertiesSingelton.getInstance()!!.getUserRecipess()!!.get(i)!!)

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


//    inner class SpinnerActivity(positionInTable: Int) : Activity(),
//        AdapterView.OnItemSelectedListener {
//        var positionInTable = positionInTable
//
//
//        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
//            Log.v("Elad1", "position in table " + positionInTable)
//            // An item was selected. You can retrieve the selected item using
//            // parent.getItemAtPosition(pos)
//            Log.v("Elad1", "Daily days " + dailyDayss)
//            var tmp = 0
//            when (parent.getItemAtPosition(pos)) {
//                "Sunday" -> tmp = 0
//                "Monday" -> tmp = 1
//                "Tuesday" -> tmp = 2
//                "Wednesday" -> tmp = 3
//                "Thursday" -> tmp = 4
//                "Friday" -> tmp = 5
//                "Saturday" -> tmp = 6
//            }
//            // dailyDays!!.add(parent.getItemAtPosition(pos).toString())
////            if (!(dailyDayss!!.contains(tmp))) {
////                dailyDayss!!.add(pos)
////
////
////            }
//            Log.v("Elad1", "Dup" + duplicateDay)
//            if ((dailyDayss!!.contains(duplicateDay)) && !flag) {
//                dailyDayss!!.remove(duplicateDay)
//
//            }
//            Log.v("Elad1", "Tmp is " + tmp)
//            dailyDayss!!.add(tmp)
//            flag = true
//
//            // to delete unexpected daily days
//            if (dailyDayss!!.size == tablePosition) {
//                dailyDayss!!.removeAt(dailyDayss!!.size - 1)
//                dailyDayss!!.add(positionInTable, tmp)
//                dailyDayss!!.removeAt(positionInTable + 1)
//            }
//
//            Log.v("Elad1", "Daily days 2" + dailyDayss)
//
//        }
//
//        override fun onNothingSelected(parent: AdapterView<*>) {
//            // Another interface callback
//
//
//        }
//    }

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }


}

