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
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import kotlin.collections.ArrayList

// NEED TO CHECK  - > WHEN EDITING A DAILY - IF IT CHANGES ITS TOTAL COST OR NOT !!!
class AddWeeklyScheduleFragment : Fragment(), View.OnClickListener,
    DialogInterface.OnDismissListener, GetAndPost {

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
    private var dailyListChoose: ArrayList<DailySchedule> = ArrayList()
    private var flag = true // this flag is for the duplicated days check.

    private var tablePosition = 1
    private var savedSize = 0
    private var dailyId = -1
    private var recipeIDs = ""
    private var quantitiesStr = ""
    private var numOfMeals = ""
    private var pos = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }


        listDailyIdChoosen = ArrayList()
        recipeList = ArrayList()
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
            dailyList!!.clear()
            savedSize = dailyListChoose.size


            var d = Daily_Schedule_Choose_Dialog(
                dailyList!!,
                recipeList!!,
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
                for (i in dailyListChoose!!) {
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
                    var s = AsynTaskNew(w, childFragmentManager,requireContext())
                    s.execute()
                    if (MyWeeklyScheduleFragment.instance!!.noWeeklyTextView.visibility == View.VISIBLE) {
                        MyWeeklyScheduleFragment.instance!!.noWeeklyTextView.visibility = View.INVISIBLE
                    }

                    MyWeeklyScheduleFragment.getInstance1().getRecycler().toCopy(w)
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
        dailyListChoose!!.clear()
        savedSize = 0
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

        for (i in dailyID!!.list!!) {


            var daily = dailyList!!.get(i)
            dailyListChoose.add(daily!!)


        }



        stk.setColumnShrinkable(3, false)
        stk.setColumnStretchable(3, false)


        var j = 0
        for (i in dailyListChoose) {

            // saved size to know the size of list before we change  so we wont override all the list.
            if (j > savedSize - 1) {


                var tbrow: TableRow = TableRow(this.context)
                tbrow.setTag(tablePosition)

                totalCostDobule += dailyListChoose!!.get(tablePosition - 1).totalCost
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
                t2v.setText(dailyListChoose!!.get(tablePosition - 1).dailyId.toString())
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

                    totalCostDobule -= dailyListChoose!!.get(t3v.getTag() as Int - 1).totalCost
                    totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
                    totalCost.setText(totalCostDobule.toString())
                    dailyListChoose!!.removeAt(t3v.getTag() as Int - 1)

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

                    dailyId = dailyListChoose!!.get(t3v.getTag() as Int - 1).dailyId
                    quantitiesStr = dailyListChoose!!.get(t3v.getTag() as Int - 1).quantities
                    recipeIDs = dailyListChoose!!.get(t3v.getTag() as Int - 1).recipeIds
                    numOfMeals = dailyListChoose!!.get(t3v.getTag() as Int - 1).numOfMeals
                    pos = t3v.getTag() as Int


                    var s = AsynTaskNew(this, childFragmentManager,requireContext())
                    s.execute()
                }
                tbrow.addView(t4v)


                stk.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.setBackgroundResource(R.drawable.spinner_shape)
                stk.addView(tbrow)


            }
            j++
        }
    }


    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
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



        return sb.toString()
    }

    override fun getData(str: String) {
        var start = 0
        recipeList!!.clear()
        //recipe size 11
        // ingredient size 15

        if (!str.equals("")) {


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


            // currentID = -1
            var recipeIdsArr = recipeIDs.splitIgnoreEmpty(" ")
            for (i in recipeIdsArr) {

                for (j in recipesAndIngredients.indices) {


                    var recipe2 = recipesAndIngredients[j].splitIgnoreEmpty("*")
                    if (recipe2.size == 10 && i.toInt() == recipe2[0].toInt()) {
                        //var s = recipe2[0].toInt()
                        //  if (s != currentID)
                        var instructions = HowToStroreValue(recipe2[9])
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
                                quantities.get(recipe2[0].toInt())!!,
                                instructions
                                // hashMap.get(recipe2[0].toInt())!!.second

                            )
                        )

                        //currentID = recipe2[0].toInt()

                    }
                }

            }
            var dialog = DailyDialogInfo(
                recipeList!!,
                quantitiesStr,
                numOfMeals,
                recipeIDs,
                pos,
                dailyId

            )
            dialog.show(childFragmentManager, "DailyDialogInfo")


        }
    }
}




