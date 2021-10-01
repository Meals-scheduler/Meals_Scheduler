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
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat


class EditWeeklyDialog(
    dailyList: ArrayList<DailySchedule>,
    numOfDay: String,
    dailyIds: String,
    pos: Int,
    weeklyId: Int,

    ) : DialogFragment(), DialogInterface.OnDismissListener, View.OnClickListener, GetAndPost {


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
    private var recipeList: ArrayList<Recipe>? = null
    private var dailyListChoose: ArrayList<DailySchedule> = ArrayList()


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
    private var flag = true // this flag is for the duplicated days check.
    private var dailyId = -1
    private var quantitiesStr = ""
    private var numOfMeals = ""
    private var recipeIdsStr = ""
    private var pos = -1


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
        recipeList = ArrayList()
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


                var daily = dailyList!!.get(i)
                dailyListChoose.add(daily!!)


            }


            var j = 0

            for (i in dailyListChoose) {
                if (j > savedSize - 1) {

                    var t1v: Spinner = Spinner(context)
                    dailyIdsArrlist.add(i.dailyId)
                    //dailyDayss.add(0)
                    t1v.setTag(tablePosition - 1)

                    var tbrow: TableRow = TableRow(this.context)
                    //t1v.setTag(tablePosition)
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

                    /// t1v.onItemSelectedListener = SpinnerActivity(t1v.getTag() as Int)

                    /// if we have sunday - the next one will be monday..
                    if (j >= 0) {
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


                    t2v.setText(" " + (i.dailyId))
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
                        totalCostDobule -= dailyListChoose!!.get(t4v.getTag() as Int).totalCost
                        totalCostDobule =
                            (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
                        totalCost.setText(totalCostDobule.toString())
                        dailyListChoose!!.removeAt(t4v.getTag() as Int)
                        dailyIdsArrlist.removeAt(t4v.getTag() as Int)
                        //  dailyDayss.removeAt(t4v.getTag() as Int)


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

                        dailyId = i.dailyId
                        quantitiesStr = i.quantities
                        numOfMeals = i.numOfMeals
                        recipeIdsStr = i.recipeIds
                        pos = t3v.getTag() as Int + 1


                        var s = AsynTaskNew(this, childFragmentManager,requireContext())
                        s.execute()

                    }
                    tbrow.addView(t3v)



                    stk.setBackgroundResource(R.drawable.spinner_shape)
                    tbrow.setBackgroundResource(R.drawable.spinner_shape)
                    stk.addView(tbrow)
                    j++
                    totalCostDobule += i.totalCost


                }
                j++
                dailyPos++

                totalCostDobule = (DecimalFormat("##.####").format(totalCostDobule)).toDouble()
                totalCost.setText(totalCostDobule.toString())
                savedSize = dailyListChoose.size
            }

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


        for (i in dailyIdsArr) {
            dailyIdsArrlist.add(i.toInt())
        }


        for (i in dailyList) {

            if (j >= dailyIdsArr.size) {
                break
            }

            if (i.dailyId == dailyIdsArr[j].toInt()) {


                var t1v: Spinner = Spinner(context)
                t1v.setTag(tablePosition - 1)
                t1v.id = android.R.id.list

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

                // t1v.onItemSelectedListener = SpinnerActivity(t1v.getTag() as Int)

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
                    // dailyDayss.removeAt(t4v.getTag() as Int)
                    savedSize = dailyList.size

                    tablePosition--
                    Log.v("Elad1", "HEREEEEEEE")


                    for (x in stk) {
                        Log.v("Elad1", "I is " + i)
                        Log.v("Elad1", " row tage is " + x.getTag())
                        if (x.getTag() as Int == 0)
                            continue
                        if (x.getTag() as Int > i) {
                            x.setTag((x.getTag() as Int) - 1)
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

                    dailyId = i.dailyId
                    quantitiesStr = i.quantities
                    numOfMeals = i.numOfMeals
                    recipeIdsStr = i.recipeIds
                    pos = t3v.getTag() as Int + 1


                    var s = AsynTaskNew(this, childFragmentManager,requireContext())
                    s.execute()
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
            savedSize = dailyListChoose.size
            dailyList.clear()

            var dialog = Daily_Schedule_Choose_Dialog(
                dailyList!!,
                recipeList!!,
                dailyID!!
            )
            dialog.show(childFragmentManager, "DailySchudleChooseDialog")
        } else if (p0 == saveBtn) {

            if (dailyIdsArrlist.isNotEmpty()) {

                numOfDay = ""
                dailyIds = ""
                dailyDayss.clear()
                flag = true

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
                    dailyDayss.add(value as Int)
                    Log.v("Elad1", "YOSIII " + value)


                }


                val arr = IntArray(7)

                // collecting dailys id's
                for (i in dailyIdsArrlist) {
                    dailyIds += "" + i + " "
                }

                // collectiing daily days, checking no duplicates
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

                    var s = AsynTaskNew(weekly, childFragmentManager,requireContext())
                    s.execute()

                    numOfDay = ""
                    dailyIds = ""

                }

                // flag = false


            }


        } else if (p0 == exit) {
            dismiss()
        }

    }

    override fun DoNetWorkOpreation(): String {
        var string = UserInterFace.userID.toString() + " " + dailyId

        var link = "https://elad1.000webhostapp.com/getRecipeForDaily.php?ownerIDAndDaily=" + string


        val sb = StringBuilder()

        val url = URL(link)
        val urlConnection = url.openConnection() as HttpURLConnection
        try {
            val `in`: InputStream = BufferedInputStream(urlConnection.inputStream)
            val bin = BufferedReader(InputStreamReader(`in`))
            // temporary string to hold each line read from the reader.
            var inputLine: String?

            while (bin.readLine().also { inputLine = it } != null) {
                sb.append(inputLine)

            }
        } finally {
            // regardless of success or failure, we will disconnect from the URLConnection.
            urlConnection.disconnect()
        }


        //Log.v("Elad1", "Id came is" + sb.toString())
        return sb.toString()
    }

    override fun getData(str: String) {
        var start = 0
        // recipeList!!.clear()
        //recipe size 11
        // ingredient size 15
        if (!str.equals("")) {


            //  recipeList!!.clear()
            val recipesAndIngredients: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()

            // first recipe id

            var recipesAndIngredients2 = recipesAndIngredients[0].splitIgnoreEmpty("*")
            // first recipe id
            var currentID = recipesAndIngredients2[0].toInt()
            var currentIngId = -1
            // taking 4 Recipe ids to classifiy the ingredients to them.

            var recipeIds: ArrayList<Int> = ArrayList()


//
            var recipeIngredientMap: HashMap<Int, ArrayList<Ingredient>> = HashMap()


            var j = 0
            while (true) {

                var recipesAndIngredients2 = recipesAndIngredients[j++].splitIgnoreEmpty("*")
                if (recipesAndIngredients2.size != 10) {
                    break
                }
                start++
                recipeIds.add(recipesAndIngredients2[0].toInt())
            }


            //var isFirst = true

            // saving all the ingredietns and quantities of each Recipe in map.
            // first we extract the ids and quantity into map.
            // then we use another map to convert all ids to real ingredients.

//            var hashMap: HashMap<Int, Pair<ArrayList<String>, ArrayList<Int>>> =
//                HashMap()

            var map: HashMap<Int, ArrayList<Ingredient>> = HashMap()

//            var ingredientList: ArrayList<Ingredient> = ArrayList()
//
//            var quantities: ArrayList<Int> = ArrayList()

            var ingredientList: ArrayList<Ingredient> = ArrayList()

            var quantities: HashMap<Int, ArrayList<Float>> = HashMap()

            var ids: HashMap<Int, ArrayList<Int>> = HashMap()

            // first extracting all ingredients ids and make them Ingredients.
            for (i in start..recipesAndIngredients.size - 1) {

                var recipesAndIngredients2 = recipesAndIngredients[i].splitIgnoreEmpty("*")
                currentIngId = recipesAndIngredients2[15].toInt()

                //if its ingredients details
                if (recipesAndIngredients2.size == 16 && recipeIds.contains(recipesAndIngredients2[15].toInt())) {

                    var ing = Ingredient(
                        recipesAndIngredients2[0].toInt(),
                        recipesAndIngredients2[1].toInt(),
                        recipesAndIngredients2[2],
                        ImageConvert.StringToBitMap(recipesAndIngredients2[3].toString())!!,
                        recipesAndIngredients2[4],
                        recipesAndIngredients2[5],
                        recipesAndIngredients2[6],
                        recipesAndIngredients2[7].toBoolean(),
                        recipesAndIngredients2[8].toBoolean(),
                        recipesAndIngredients2[9].toFloat(),
                        recipesAndIngredients2[10].toFloat(),
                        recipesAndIngredients2[11].toFloat(),
                        recipesAndIngredients2[12],
                        recipesAndIngredients2[13],
                        false

                    )
                    ingredientList?.add(ing)

                    if (!recipeIngredientMap.containsKey(currentIngId)) {
                        var recipeIngredients: ArrayList<Ingredient> = ArrayList()
                        var quantitiy: ArrayList<Float> = ArrayList()
                        var idss: ArrayList<Int> = ArrayList()
                        recipeIngredientMap.put(currentIngId, recipeIngredients)
                        recipeIngredientMap.get(currentIngId)!!.add(ing)
                        quantities.put(currentIngId, quantitiy)
                        quantities.get(currentIngId)!!.add(recipesAndIngredients2[14].toFloat())
                        ids.put(currentIngId, idss)
                        ids.get(currentIngId)!!.add(recipesAndIngredients2[0].toInt())

                    } else {
                        recipeIngredientMap.get(currentIngId)!!.add(ing)
                        quantities.get(currentIngId)!!.add(recipesAndIngredients2[14].toFloat())
                        ids.get(currentIngId)!!.add(recipesAndIngredients2[0].toInt())
                    }


                    //quantities.add(recipesAndIngredients2[14].toInt())
                    //ids.add(recipesAndIngredients2[0])

                }
            }





            currentID = -1
            for (i in recipesAndIngredients.indices) {

                var recipe2 = recipesAndIngredients[i].splitIgnoreEmpty("*")
                if (recipe2.size == 10) {
                    var s = recipe2[0].toInt()
                    var instructions = HowToStroreValue(recipe2[9])
                    if (s != currentID)
                        recipeList?.add(
                            Recipe(
                                recipe2[0].toInt(),
                                recipe2[1].toInt(),
                                recipe2[2],
                                ImageConvert.StringToBitMap(recipe2[3].toString())!!,
                                recipe2[4],
                                recipe2[5],
                                recipe2[6].toDouble(),
                                recipe2[7].toBoolean(),
                                recipe2[8].toBoolean(),
                                recipeIngredientMap.get(recipe2[0].toInt())!!,
                                quantities.get(s)!!
                                ,instructions
                                // hashMap.get(recipe2[0].toInt())!!.second

                            )
                        )

                    currentID = recipe2[0].toInt()

                } else {
                    break
                }
            }


            var dialog = DailyDialogInfo(
                recipeList!!,
                quantitiesStr,
                numOfMeals,
                recipeIdsStr,
                pos,
                dailyId

            )
            dialog.show(childFragmentManager, "DailyDialogInfo")


        }
    }
}


//    inner class SpinnerActivity() : Activity(),
//        AdapterView.OnItemSelectedListener {
//
//
//        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
//            // An item was selected. You can retrieve the selected item using
//            // parent.getItemAtPosition(pos)\
////            Log.v("Elad1", "position in table " + positionInTable)
////            Log.v("Elad1", "deleted is " + deletedTimes)
////            Log.v("Elad1", "Daily days" + dailyDayss)
////            positionInTable -= deletedTimes
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
////            //Log.v("Elad1","Dup" + duplicateDay)
////            if ((dailyDayss!!.contains(duplicateDay)) && !flag) {
////                dailyDayss!!.remove(duplicateDay)
////
////            }
////            // if we dont add new day and just change the day
////            Log.v("Elad1", "Saved is " + savedSize)
////            if (dailyDayss.size == savedSize) {
////                Log.v("Elad1", "daily size " + dailyDayss.size)
////                Log.v("Elad1", "didnt add new day")
////                // means we change the last day
////                if (positionInTable - deletedTimes == dailyDayss.size - 1) {
////                    Log.v("Elad1", "End")
////                    dailyDayss.removeAt(dailyDayss.size - 1)
////                    dailyDayss.add(tmp)
////                }
////
////                // first day and day in the middle changes are the same
////                else if (positionInTable - deletedTimes == 0) {
////                    Log.v("Elad1", "start")
////                    dailyDayss.add(positionInTable, tmp)
////                    dailyDayss.removeAt(positionInTable + 1)
////                }
////                // middle day change
////                else {
////                    Log.v("Elad1", "mid")
////                    dailyDayss.add(positionInTable, tmp)
////                    dailyDayss.removeAt(positionInTable + 1)
////                }
////
////            } else {
////                Log.v("Elad1", "add new day")
////                dailyDayss.add(tmp)
////            }
////
////            Log.v("Elad1", "Daily days2" + dailyDayss)
//        }
//
//        override fun onNothingSelected(parent: AdapterView<*>) {
//            // Another interface callback
//
//
//        }
//    }


