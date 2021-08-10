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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.meals_schdueler.dummy.DailySchedule
import java.text.DecimalFormat


class EditWeeklyDialog(
    dailyList: ArrayList<DailySchedule>,
    numOfDay: String,
    dailyIds: String,
    pos: Int,
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
    private var duplicateDay = -1


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
        // super.onDismiss(dialog)
        if (flag) {
            dailyPos = savedSize
            val parentFragment: Fragment? = parentFragment
            if (parentFragment is DialogInterface.OnDismissListener) {
                (parentFragment as DialogInterface.OnDismissListener?)!!.onDismiss(dialog)
            }


            for (i in dailyID!!.list!!) {


                var daily = UserPropertiesSingelton.getInstance()!!.getUserDaily()!!.get(i)
                dailyList.add(daily)
                //   numOfDayArrlist.add(0)



            }



            var j = savedSize

            for (i in j..dailyList.size - 1) {
//
//                if (j >= numOfDayArrlist.size) {
//                    break

//                }


                var tbrow: TableRow = TableRow(this.context)
                tbrow.setTag(tablePosition++)


                var t1v: Spinner = Spinner(context)
                dailyIdsArrlist.add(dailyList.get(i).dailyId)
                dailyDayss.add(0)
                t1v.setTag(tablePosition-1)


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

                t1v.onItemSelectedListener = SpinnerActivity(t1v.getTag() as Int)

                t1v.setSelection(dailyDayss[j++])


                tbrow.addView(t1v)


                var t2v: TextView = TextView(context)


                t2v.setText(" " + (dailyList.get(i).dailyId))
                t2v.setTextColor(Color.BLACK)
                t2v.gravity = Gravity.CENTER

                tbrow.addView(t2v)

                var t4v: Button = Button(context)
                t4v.setTag(dailyPos)
                t4v.setText("Delete")
                t4v.setTextSize(8F)
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
                    dailyDayss.removeAt(t4v.getTag() as Int)
                    Log.v("Elad1", "Daily days after del" + dailyDayss)

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
                        dailyList.get(i).quantities,
                        dailyList.get(i).numOfMeals,
                        dailyList.get(i).recipeIds,
                        t3v.getTag() as Int + 1,
                        dailyList.get(i).dailyId
                    )


                    dialog!!.show(childFragmentManager, "DailyDialogInfo")
                }
                tbrow.addView(t3v)



                stk.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.setBackgroundResource(R.drawable.spinner_shape)
                stk.addView(tbrow)
                j++
                totalCostDobule += dailyList.get(i).totalCost


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
        dailyPos = 0

        // converting the strings into arr's
        var numOfDayArr = numOfDay.splitIgnoreEmpty(" ")
        var dailyIdsArr = dailyIds.splitIgnoreEmpty(" ")

//        for (i in numOfDayArr) {
//            dailyDayss.add(i.toInt())
//
//        }

        for (i in dailyIdsArr) {
            dailyIdsArrlist.add(i.toInt())
        }


        for (i in dailyList) {

            if (j >= dailyIdsArr.size) {
                break
            }

            if (i.dailyId == dailyIdsArr[j].toInt()) {


                var t1v: Spinner = Spinner(context)
                t1v.setTag(tablePosition-1)

                var tbrow: TableRow = TableRow(this.context)
                tbrow.setTag(tablePosition++)


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

                t1v.onItemSelectedListener = SpinnerActivity(t1v.getTag() as Int)

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
                    dailyDayss.removeAt(t4v.getTag() as Int)
                    Log.v("Elad1", "Daily days after del" + dailyDayss)
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
                totalCostDobule += i.totalCost

            }

            //dailyPos++
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
            dailyID!!.list!!.clear()
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

                val arr = IntArray(7)

                Log.v("Elad1", "Daily days" + dailyDayss)
                for (i in dailyIdsArrlist) {
                    dailyIds += "" + i + " "
                }

                for (i in dailyDayss) {
                    if (arr[i] != 0) {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

                        builder.setTitle("Edit Weekly")
                        builder.setMessage("You cannot have a duplicate of the same day.")

                        builder.setPositiveButton(
                            "OK",
                            DialogInterface.OnClickListener { dialog, which -> // Do nothing but close the dialog

                                dialog.dismiss()
                            })
                        val alert: AlertDialog = builder.create()
                        alert.show()
                        flag = false
                        if (duplicateDay != -1)
                            duplicateDay = i

                        break
                    } else {
                        arr[i] += 1
                        numOfDay += "" + i + " "
                    }

                }



                if (flag) {
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
                    duplicateDay = -1
                }

                // flag = false


            }


        } else if (p0 == exit) {
            dismiss()
        }

    }


    inner class SpinnerActivity(positionTable : Int) : Activity(), AdapterView.OnItemSelectedListener {

        var positionInTable = positionTable
        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)\
           // Log.v("Elad1","position in table " + positionInTable)
            Log.v("Elad1", "Daily days" + dailyDayss)
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
            //Log.v("Elad1","Dup" + duplicateDay)
            if ((dailyDayss!!.contains(duplicateDay)) && !flag) {
                dailyDayss!!.remove(duplicateDay)

            }
           // Log.v("Elad1", "Tmp is " + tmp)
            dailyDayss!!.add(tmp)
            flag = true

            // to delete unexpected daily days
            if (dailyDayss!!.size == tablePosition) {
                //dailyDayss!!.removeAt(0)
                dailyDayss!!.removeAt(dailyDayss!!.size-1)
                dailyDayss!!.add(positionInTable,tmp)
                if(positionInTable+1>= dailyDayss.size){
                    dailyDayss!!.removeAt(positionInTable)
                }
                else{
                    dailyDayss!!.removeAt(positionInTable+1)
                }

            }

            Log.v("Elad1", "Daily days 2" + dailyDayss)

        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            // Another interface callback


        }
    }

}

