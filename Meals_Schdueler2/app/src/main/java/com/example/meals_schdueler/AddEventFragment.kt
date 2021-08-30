package com.example.meals_schdueler

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
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
import androidx.fragment.app.Fragment
import com.allyants.notifyme.NotifyMe
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList



class AddEventFragment : Fragment(), View.OnClickListener,
    DialogInterface.OnDismissListener {


    private var listItemsChoosen: ArrayList<Int>? = null
    private var recipeQuantitiy: ArrayList<Int>? = null
    private var listItems: Recipe_Ingredients_List? = null
    private lateinit var stk: TableLayout
    private lateinit var recipeList: ArrayList<Recipe>
    private lateinit var recipesID: ArrayList<Int>
    private var recipesQuantities: Recipe_Ingredients_List? = null


    private var columnCount = 1
    private lateinit var chooseBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var dateBtn: Button
    private var recipeIds: String = ""
    private var quantities = ""
    private var eventName = ""
    private lateinit var eventNameEditText: EditText
    private lateinit var dateEditText: TextView
    private lateinit var totalCost: EditText
    private var totalCostDobule: Double = 0.0

    private var date = ""
    private lateinit var cal: Calendar
    private lateinit var tpd: TimePickerDialog
    private lateinit var dpd: DatePickerDialog


    private var tablePosition = 1
    private var savedSize = 0
    var j = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(AddDailyScheduleFragment.ARG_COLUMN_COUNT)
        }
        listItemsChoosen = ArrayList()
        listItems = Recipe_Ingredients_List(listItemsChoosen)
        recipeList = ArrayList()
        recipesID = ArrayList()
        recipeQuantitiy = ArrayList()
        recipesQuantities = Recipe_Ingredients_List(recipeQuantitiy)
    }

    private fun addTable() {

        var tbrow0: TableRow = TableRow(context)

        var tv1: TextView = TextView(context)
        tv1.setText(" Picture ")
        tv1.setTextColor(Color.BLACK)
        tv1.gravity = Gravity.CENTER
        // tv1.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.addView(tv1)

        var tv3: TextView = TextView(context)
        tv3.setText(" Name ")
        tv3.setTextColor(Color.BLACK)
        tv3.gravity = Gravity.CENTER
        // tv3.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.addView(tv3)


        var tv5: TextView = TextView(context)
        tv5.setText(" Quantity ")
        tv5.setTextColor(Color.BLACK)
        tv5.gravity = Gravity.CENTER
        // tv4.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.addView(tv5)

        var tv6: TextView = TextView(context)
        tv6.setText(" Delete ")
        tv6.setTextColor(Color.BLACK)
        tv6.gravity = Gravity.CENTER
        // tv5.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.setTag(0)
        tbrow0.addView(tv6)


        var tv7: TextView = TextView(context)
        tv7.setText(" Info ")
        tv7.setTextColor(Color.BLACK)
        tv7.gravity = Gravity.CENTER
        // tv5.setBackgroundResource(R.drawable.spinner_shape)

        tbrow0.addView(tv7)


        tbrow0.setBackgroundResource(R.drawable.spinner_shape)

        stk.addView(tbrow0)


    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            AddEventFragment().apply {
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
        val x = inflater.inflate(R.layout.add_event, null)

        //stam = x.findViewById(R.id.elad)
        stk = x.findViewById(R.id.tableLayout)
        chooseBtn = x.findViewById(R.id.chooseBtn)
        totalCost = x.findViewById(R.id.editTextTotalCost)
        eventNameEditText = x.findViewById(R.id.editTextEventName)
        saveBtn = x.findViewById(R.id.saveBtn)
        dateBtn = x.findViewById(R.id.dateBtn)
        dateEditText = x.findViewById(R.id.editTextDate)
        dateBtn.setOnClickListener(this)
        saveBtn.setOnClickListener(this)
        chooseBtn.setOnClickListener(this)


        //val view = inflater.inflate(com.example.meals_schdueler.R.layout.add_daily_schedule, container, false)

        addTable()
        return x
    }


    override fun onClick(p0: View?) {
        if (p0 == chooseBtn) {
            //    Log.v("Elad1","RecipeQuantity Size breakfast: " + recipesQuantities!!.list!!.size)
            listItems!!.list!!.clear()
            savedSize = recipeList.size
            //   recipeList.clear()

            var dialog = Recipe_Schedule_Choose_Dialog(
                listItems!!,
                UserPropertiesSingelton.getInstance()!!.getUserRecipess()!!,
                recipesQuantities!!,
                "Event"

            )
            dialog.show(childFragmentManager, "Recipe_Schuedle_Choose")
        } else if (p0 == saveBtn) {

            if (date != "") {
                val dialogClickListener =
                    DialogInterface.OnClickListener { dialog, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                val notifyMe: NotifyMe =
                                    NotifyMe.Builder(context).title("Meals-Scheudler")
                                        .content("Hey, Event is coming").color(255, 0, 0, 255)
                                        .led_color(255, 255, 255, 255).time(cal)
                                        .addAction(Intent(), "Snooze", false)
                                        .key("test").addAction(Intent(), "Dismiss", true, false)
                                        .large_icon(R.mipmap.ic_launcher_round).build()

                                Log.v("Elad1","clicked yes")

                            }
                            DialogInterface.BUTTON_NEGATIVE -> {

                                Log.v("Elad1","clicked no")
                            }
                        }
                    }

                val builder = AlertDialog.Builder(context)
                builder.setMessage("Would you like to get notification on the specific day?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show()


                recipeIds = ""
                quantities = ""

                eventName = eventNameEditText.text.toString()

                for (i in recipesID) {
                    recipeIds += "" + i + " "
                }

                for (i in recipesQuantities!!.list!!) {
                    quantities += "" + i + " "
                }


                recipesID.clear()
                recipesQuantities!!.list!!.clear()

                var upcoming = UpComingScheudule(
                    -1,
                    date,
                    11
                )


                var s = AsynTaskNew(upcoming, childFragmentManager)
                s.execute()

                Log.v("Elad1", "event name " + eventName)
                Log.v("Elad1", "quantities " + quantities)
                Log.v("Elad1", "date " + date)
                Log.v("Elad1", "ids  " + recipeIds)
                Log.v("Elad1", "cost  " + totalCostDobule)

                var event = Event(
                    1,
                    UserInterFace.userID,
                    eventName,
                    quantities,
                    date,
                    recipeIds,
                    totalCostDobule,
                    false
                )

                s = AsynTaskNew(event, childFragmentManager)
                s.execute()

                recipeIds = ""
                quantities = ""
                clearTable()
            } else {
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)

                builder.setTitle("Adding Event")
                builder.setMessage("You must choose date!")

                builder.setPositiveButton(
                    "OK",
                    DialogInterface.OnClickListener { dialog, which -> // Do nothing but close the dialog

                        dialog.dismiss()
                    })
                val alert: AlertDialog = builder.create()
                alert.show()
            }

        } else if (p0 == dateBtn) {
            cal = Calendar.getInstance()
            val currentYear = cal[Calendar.YEAR]
            val currentMonth = cal[Calendar.MONTH]
            val currentDay = cal[Calendar.DAY_OF_MONTH]

            dpd = DatePickerDialog(
                requireActivity(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    tpd.show()


//                    Log.v("Elad1", year.toString())
//                    Log.v("Elad1", monthOfYear.toString())
//                    Log.v("Elad1", dayOfMonth.toString())

                },
                currentYear,
                currentMonth,
                currentDay
            )

            dpd.show()

            val hour = cal[Calendar.HOUR]
            val min = cal[Calendar.MINUTE]
            //val second = cal[Calendar.SECOND]

            tpd = TimePickerDialog(
                requireActivity(),
                TimePickerDialog.OnTimeSetListener { view, hour, minute ->

                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    var month = cal.get(Calendar.MONTH) + 1
                    date = cal.get(Calendar.YEAR).toString() + "-" + month
                        .toString() + "-" + cal.get(Calendar.DAY_OF_MONTH).toString()

                    dateEditText.setText(date)
//
//                    Log.v("Elad1", hour.toString())
//                    Log.v("Elad1", minute.toString())


                },
                hour,
                min,
                false
            )

        }
    }


    private fun clearTable() {
        tablePosition = 1
        savedSize = 0
        totalCostDobule = 0.0
        totalCost.setText(totalCostDobule.toString())
        stk.setColumnShrinkable(4, false)
        stk.setColumnShrinkable(5, false)
        stk.setColumnStretchable(5, false)
        stk.setColumnStretchable(4, false)
        var j = 1
        for (x in stk) {
            stk.removeView(stk.getChildAt(j))
        }
        stk.removeView(stk.getChildAt(j))

    }

    override fun onDismiss(p0: DialogInterface?) {
        // on dissmiss event , when we dissmiss the ingrdeitns selcection dialog we want to update the list with
        // the chosen ingredients.
//        if (!dateClass!!.date.equals(""))
//            dateEditText.setText(dateClass!!.date)

        for (i in listItems!!.list!!) {


            var recipe =
                UserPropertiesSingelton.getInstance()!!.getUserRecipess()!!.get(i.toString())
            recipeList.add(recipe!!)


        }


        // for buttons to shirnk them

        stk.setColumnShrinkable(5, true)
        stk.setColumnShrinkable(4, true)
        stk.setColumnStretchable(5, true)
        stk.setColumnStretchable(4, true)

        var j = 0
        for (i in recipeList) {

            // saved size to know the size of list before we change  so we wont override all the list.
            if (j > savedSize - 1) {


                var tbrow: TableRow = TableRow(this.context)

                tbrow.setTag(tablePosition)
                totalCostDobule += i.totalCost * recipesQuantities!!.list!!.get(tbrow.getTag() as Int - 1)
                recipesID.add(i.recipeId)


                var t2v: ImageView = ImageView(context)


                t2v.setImageBitmap(i.pictureBitMap)
                val scaled = Bitmap.createScaledBitmap(
                    i.pictureBitMap,
                    80,
                    80,
                    true
                )
                //  ((i.pictureBitMap.height * (100.0 / i.pictureBitMap.width)).toInt())
                //t2v.adjustViewBounds = true

                t2v.scaleType = (ImageView.ScaleType.CENTER)
                t2v.setImageBitmap(scaled)

                tbrow.addView(t2v)

                var t3v: TextView = TextView(context)
                // t3v.setBackgroundResource(R.drawable.border)
                t3v.setText(i.recipeName)
                t3v.setTextColor(Color.BLACK)
                t3v.gravity = Gravity.CENTER
                //    t3v.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.addView(t3v)


                var t5v: TextView = TextView(context)
                // t4v.setBackgroundResource(R.drawable.border)
                t5v.setText(recipesQuantities!!.list!!.get(tbrow.getTag() as Int - 1).toString())
                t5v.setTextColor(Color.BLACK)
                t5v.gravity = Gravity.CENTER
                //t4v.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.addView(t5v)

                var t6v: Button = Button(context)
                t6v.setTag(tablePosition)
                t6v.setText("Del")
                t6v.setTextColor(Color.BLACK)
                t6v.gravity = Gravity.CENTER
                t6v.setTextSize(10F)
                //t5v.setBackgroundResource(R.drawable.spinner_shape)
                t6v.setOnClickListener {
                    //stk.removeViewAt(t5v.getTag() as Int)
                    var i = t6v.getTag() as Int

                    stk.removeView(stk.getChildAt(t6v.getTag() as Int))
                    totalCostDobule -= recipeList.get(t6v.getTag() as Int - 1).totalCost * recipesQuantities!!.list!!.get(
                        t6v.getTag() as Int - 1
                    )
                    totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
                    totalCost.setText(totalCostDobule.toString())


                    recipesID.removeAt(t6v.getTag() as Int - 1)
                    recipesQuantities!!.list!!.removeAt(t6v.getTag() as Int - 1)
                    recipeList.removeAt(t6v.getTag() as Int - 1)
                    tablePosition--


                    for (x in stk) {
                        if (x.getTag() as Int == 0)
                            continue
                        if (x.getTag() as Int > i) {
                            x.setTag(x.getTag() as Int - 1)
                            var y = x as TableRow
                            //changing info delete tag
                            y.get(3).setTag(y.get(3).getTag() as Int - 1)
                            //changing infp button tag
                            y.get(4).setTag(y.get(4).getTag() as Int - 1)

                        }

                    }

                    if (tablePosition == 1) {
                        stk.setColumnShrinkable(4, false)
                        stk.setColumnShrinkable(5, false)
                        stk.setColumnStretchable(5, false)
                        stk.setColumnStretchable(4, false)
                    }


                }
                tbrow.addView(t6v)


                var t7v: Button = Button(context)
                t7v.setTag(tablePosition++)
                t7v.setText("Info")
                t7v.setTextSize(10F)
                t7v.setTextColor(Color.BLACK)
                t7v.gravity = Gravity.CENTER

                //t5v.setBackgroundResource(R.drawable.spinner_shape)
                t7v.setOnClickListener {
                    var dialog = MyRecipeIngredietns(
                        recipeList.get(t7v.getTag() as Int - 1).listOfIngredients,
                        recipeList.get(t7v.getTag() as Int - 1).recipeName,
                        recipeList.get(t7v.getTag() as Int - 1).pictureBitMap,
                        recipeList.get(t7v.getTag() as Int - 1).numOfPortions,
                        recipeList.get(t7v.getTag() as Int - 1).quantityList,
                        recipeList.get(t7v.getTag() as Int - 1).totalCost
                    )
                    dialog.show(childFragmentManager, "MyRecipeIngredients")
                }
                tbrow.addView(t7v)



                stk.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.setBackgroundResource(R.drawable.spinner_shape)
                stk.addView(tbrow)

            }
            j++
        }

        totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
        totalCost.setText(totalCostDobule.toString())
    }


}