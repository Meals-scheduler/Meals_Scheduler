package com.example.meals_schdueler


import android.app.Activity
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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.meals_schdueler.dummy.DailySchedule
import java.text.DecimalFormat


class EditWeeklyDialog(
    dailyList: ArrayList<DailySchedule>,
    numOfDay: String,
    dailyIds: String,
    pos: Int,
    myWeeklyRecylerviewadapter: My_Weekly_RecylerViewAdapter,
    weeklyId: Int,

    ) : DialogFragment(), DialogInterface.OnDismissListener, View.OnClickListener {


    private lateinit var stk: TableLayout
    private lateinit var totalCost: EditText
    private lateinit var exit: ImageView
    private lateinit var title: TextView
    private lateinit var chooseBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var numOfDayArrlist: ArrayList<Int>
    private lateinit var dailyIdsArrlist: ArrayList<Int>
    private lateinit var dailyDayss: ArrayList<Int>
    private var dailyID: Recipe_Ingredients_List? = null
    private var listDailyIdChoosen: ArrayList<Int>? = null

    // private var myWeeklyRecylerviewadapter = myWeeklyRecylerviewadapter
    private var weeklyId = weeklyId
    private var numOfDay = numOfDay
    private var dailyIds = dailyIds
    private var dailyList = dailyList
    private var tablePosition = 1
    private var totalCostDobule: Double = 0.0
    private var position = pos
    private var dailyPos = 0
    private var savedSize = 0
    private var flag = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var x: View = inflater.inflate(R.layout.edit_weekly, container, false)
        stk = x.findViewById(R.id.tableLayout)
        totalCost = x.findViewById(R.id.editTextTotalCost)
        title = x.findViewById(R.id.editNumberOfWeeklyTextView)
        title.setText("Edit Weekly No. " + position)
        dailyIdsArrlist = ArrayList()
        numOfDayArrlist = ArrayList()
        dailyDayss = ArrayList()
        listDailyIdChoosen = ArrayList()
        dailyID = Recipe_Ingredients_List(listDailyIdChoosen)
        chooseBtn = x.findViewById(R.id.chooseBtn)
        saveBtn = x.findViewById(R.id.saveBtn)


        chooseBtn.setOnClickListener(this)
        saveBtn.setOnClickListener(this)

        exit = x.findViewById(R.id.imageViewX)
        exit.setOnClickListener(this)

        addTable()
        initWeekly()
        return x


    }

    override fun onDismiss(dialog: DialogInterface) {
        Log.v("Elad1", "DDDDD")
        // super.onDismiss(dialog)
        if (flag) {
            dailyPos = 0
            val parentFragment: Fragment? = parentFragment
            if (parentFragment is DialogInterface.OnDismissListener) {
                (parentFragment as DialogInterface.OnDismissListener?)!!.onDismiss(dialog)
            }

            for (i in dailyID!!.list!!) {


                var daily = UserPropertiesSingelton.getInstance()!!.getUserDaily()!!.get(i)
                dailyList.add(daily)


            }

            var j = 0

            for (i in dailyList) {

                if (j >= dailyIdsArrlist.size) {
                    break
                }


                var tbrow: TableRow = TableRow(this.context)
                tbrow.setTag(tablePosition++)


                var t1v: Spinner = Spinner(context)
                dailyIdsArrlist.add(i.dailyId)
                dailyDayss.add(0)


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

                t1v.setSelection(numOfDayArrlist[j++].toInt())

                tbrow.addView(t1v)


                var t2v: TextView = TextView(context)


                t2v.setText(" " + (i.dailyId))
                t2v.setTextColor(Color.BLACK)
                t2v.gravity = Gravity.CENTER

                tbrow.addView(t2v)

                var t4v: Button = Button(context)
                t4v.setTag(dailyPos)
                t4v.setText("Delete")
                t4v.setTextSize(10F)
                t4v.setTextColor(Color.BLACK)
                t4v.gravity = Gravity.CENTER
                tbrow.addView(t4v)

                t4v.setOnClickListener {

                    var i = t4v.getTag() as Int

//                    stk.removeView(stk.getChildAt(t4v.getTag() as Int))
                    stk.removeView(stk.getChildAt(tbrow.getTag() as Int))
                    totalCostDobule -= dailyList!!.get(t4v.getTag() as Int).totalCost
                    totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
                    totalCost.setText(totalCostDobule.toString())
                    dailyList!!.removeAt(t4v.getTag() as Int)
                    dailyIdsArrlist.removeAt(t4v.getTag() as Int)
                    numOfDayArrlist.removeAt(t4v.getTag() as Int)
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
                t3v.setTag(dailyPos++)
                t3v.setText("Info")
                t3v.setTextSize(10F)
                t3v.setTextColor(Color.BLACK)
                t3v.gravity = Gravity.CENTER

                //t5v.setBackgroundResource(R.drawable.spinner_shape)
                t3v.setOnClickListener {
                    //NEED TO CHECK HERE WHATS WRONG with info button!!!!!
                    // getting this daily recipes
                    var recipeList: ArrayList<Recipe> = ArrayList()
                    var ids = dailyList.get(t3v.getTag() as Int).recipeIds.splitIgnoreEmpty(" ")
                    var ind = 0
                    for (k in UserPropertiesSingelton.getInstance()!!.getUserRecipess()!!) {
                        if (ind == ids.size) {
                            break
                        }
                        if (ids.contains(k.recipeId.toString())) {
                            recipeList.add(k)
                            ind++
                        }

                    }
                    var dialog = DailyDialogInfo(
                        recipeList,
                        i.quantities,
                        i.numOfMeals,
                        i.recipeIds,
                        t3v.getTag() as Int + 1,
                        i.dailyId
                    )


                    dialog!!.show(childFragmentManager, "DailyDialogInfo")
                }
                tbrow.addView(t3v)



                stk.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.setBackgroundResource(R.drawable.spinner_shape)
                stk.addView(tbrow)
                j++
                totalCostDobule+=i.totalCost


            }

            dailyPos++

            totalCostDobule = (DecimalFormat("##.####").format(totalCostDobule)).toDouble()
            totalCost.setText(totalCostDobule.toString())
            savedSize = dailyList.size


        }
    }


    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    private fun initWeekly() {
        var j = 0
        var k = 0

        // converting the strings into arr's
        var numOfDayArr = numOfDay.splitIgnoreEmpty(" ")
        var dailyIdsArr = dailyIds.splitIgnoreEmpty(" ")

        for (i in numOfDayArr) {
            numOfDayArrlist.add(i.toInt())
        }

        for (i in dailyIdsArr) {
            dailyIdsArrlist.add(i.toInt())
        }



        for (i in dailyList) {

            if (j >= dailyIdsArr.size) {
                break
            }

            if (i.dailyId == dailyIdsArr[j].toInt()) {


                var tbrow: TableRow = TableRow(this.context)
                tbrow.setTag(tablePosition++)


                var t1v: Spinner = Spinner(context)


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

                t1v.setSelection(numOfDayArr[k++].toInt())

                tbrow.addView(t1v)


                var t2v: TextView = TextView(context)


                t2v.setText(" " + (i.dailyId))
                t2v.setTextColor(Color.BLACK)
                t2v.gravity = Gravity.CENTER

                tbrow.addView(t2v)

                var t4v: Button = Button(context)
                t4v.setTag(dailyPos)
                t4v.setText("Delete")
                t4v.setTextSize(10F)
                t4v.setTextColor(Color.BLACK)
                t4v.gravity = Gravity.CENTER
                tbrow.addView(t4v)

                t4v.setOnClickListener {

                    var i = t4v.getTag() as Int

//                    stk.removeView(stk.getChildAt(t4v.getTag() as Int))
                    stk.removeView(stk.getChildAt(tbrow.getTag() as Int))
                    totalCostDobule -= dailyList!!.get(t4v.getTag() as Int).totalCost
                    totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
                    totalCost.setText(totalCostDobule.toString())
                    dailyList!!.removeAt(t4v.getTag() as Int)
                    dailyIdsArrlist.removeAt(t4v.getTag() as Int)
                    numOfDayArrlist.removeAt(t4v.getTag() as Int)
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
                t3v.setTag(dailyPos++)
                t3v.setText("Info")
                t3v.setTextSize(10F)
                t3v.setTextColor(Color.BLACK)
                t3v.gravity = Gravity.CENTER

                //t5v.setBackgroundResource(R.drawable.spinner_shape)
                t3v.setOnClickListener {
                    //NEED TO CHECK HERE WHATS WRONG with info button!!!!!
                    // getting this daily recipes
                    var recipeList: ArrayList<Recipe> = ArrayList()
                    var ids = dailyList.get(t3v.getTag() as Int).recipeIds.splitIgnoreEmpty(" ")
                    var ind = 0
                    for (k in UserPropertiesSingelton.getInstance()!!.getUserRecipess()!!) {
                        if (ind == ids.size) {
                            break
                        }
                        if (ids.contains(k.recipeId.toString())) {
                            recipeList.add(k)
                            ind++
                        }

                    }
                    var dialog = DailyDialogInfo(
                        recipeList,
                        i.quantities,
                        i.numOfMeals,
                        i.recipeIds,
                        t3v.getTag() as Int + 1,
                        i.dailyId
                    )


                    dialog!!.show(childFragmentManager, "DailyDialogInfo")
                }
                tbrow.addView(t3v)



                stk.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.setBackgroundResource(R.drawable.spinner_shape)
                stk.addView(tbrow)
                j++
                totalCostDobule +=i.totalCost

            }

            dailyPos++
        }

        totalCostDobule = (DecimalFormat("##.####").format(totalCostDobule)).toDouble()
        totalCost.setText(totalCostDobule.toString())
        savedSize = dailyList.size

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

    override fun onClick(p0: View?) {

        if (p0 == chooseBtn) {
            savedSize = dailyList.size

            var dialog = Daily_Schedule_Choose_Dialog(
                UserPropertiesSingelton.getInstance()!!.getUserDaily()!!,
                UserPropertiesSingelton.getInstance()!!.getUserRecipess()!!,
                dailyID!!
            )
            dialog.show(childFragmentManager, "DailySchudleChooseDialog")
        } else if (p0 == saveBtn) {
            if (dailyIdsArrlist.isNotEmpty()) {
                numOfDay = ""
                dailyIds = ""



                for (i in dailyIdsArrlist) {
                    dailyIds += "" + i + " "
                }

                for (i in dailyDayss) {
                    numOfDay += "" + i + " "
                }

                Log.v("Elad1", weeklyId.toString())
                Log.v("Elad1", numOfDay.toString())
                Log.v("Elad1", dailyIds.toString())

                var weekly = WeeklySchedule(
                    weeklyId,
                    UserInterFace.userID,
                    numOfDay,
                    dailyIds,
                    totalCostDobule,
                    true
                )

                var s = AsynTaskNew(weekly, childFragmentManager)
                s.execute()

                numOfDay = ""
                dailyIds = ""


                flag = false


            }


        }
        else if(p0 == exit){
            dismiss()
        }

    }



        inner class SpinnerActivity() : Activity(), AdapterView.OnItemSelectedListener {


            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)

                var tmp = 0
                when (parent.getItemAtPosition(pos)) {
                    "Sunday" -> tmp = 0
                    "Monday" -> tmp = 1
                    "Tuesday" -> tmp = 2
                    "Wednesday" -> tmp = 3
                    "Thursday" -> tmp = 4
                    "Friday" -> tmp = 5
                    "Saturday" -> tmp = 6
                }
                // dailyDays!!.add(parent.getItemAtPosition(pos).toString())
                if (!(dailyDayss!!.contains(tmp))) {
                    dailyDayss!!.add(pos)


                }
                // to delete unexpected daily days
                if (dailyDayss!!.size == tablePosition) {
                    dailyDayss!!.removeAt(0)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback


            }
        }

    }

