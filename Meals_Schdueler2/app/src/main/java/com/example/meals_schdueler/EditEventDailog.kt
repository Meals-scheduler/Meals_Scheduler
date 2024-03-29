package com.example.meals_schdueler

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
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

class EditEventDailog(
    recipeList: ArrayList<Recipe>,
    quantities: String,
    recipeIds: String,
    pos: Int,
    myEventRecylerviewadapter: My_Event_RecylerViewAdapter,
    eventId: Int,
    eventName: String,
) : DialogFragment(), View.OnClickListener,
    DialogInterface.OnDismissListener {


    private lateinit var stk: TableLayout
    private lateinit var totalCost: EditText
    private lateinit var exit: ImageView
    private lateinit var title: TextView
    private lateinit var chooseBtn: Button
    private lateinit var changeNameBtn: Button
    private lateinit var eventNameEdit: EditText
    private lateinit var saveBtn: Button
    private var listItemsChoosen: ArrayList<Int>? = null
    private var recipeQuantitiy: ArrayList<Int>? = null
    private var listItems: Recipe_Ingredients_List? = null
    private var recipesQuantities: Recipe_Ingredients_List? = null
    private lateinit var quanArrList: ArrayList<Int>
    private lateinit var recipesID: ArrayList<Int>
    var recipeListChoose: ArrayList<Recipe> = ArrayList()

    private var myEventRecylerviewadapter = myEventRecylerviewadapter
    private var eventId = eventId
    private var eventName = eventName


    var recipeIds = recipeIds
    var quantities = quantities
    var recipeList = recipeList
    private var tablePosition = 1
    private var totalCostDobule: Double = 0.0
    private var position = pos
    private var savedSize = 0
    var recipePos = 0
    private var flag = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var x: View = inflater.inflate(R.layout.edit_event, container, false)
        stk = x.findViewById(R.id.tableLayout)
        totalCost = x.findViewById(R.id.editTextTotalCost)
        title = x.findViewById(R.id.editNumberOfEventTextView)
        title.setText("Edit Event No. " + position)
        chooseBtn = x.findViewById(R.id.chooseBtn)
        saveBtn = x.findViewById(R.id.saveBtn)
        changeNameBtn = x.findViewById(R.id.buttonChangeName)
        eventNameEdit = x.findViewById(R.id.editTextName)

        eventNameEdit.setText(eventName)

        chooseBtn.setOnClickListener(this)
        saveBtn.setOnClickListener(this)
        changeNameBtn.setOnClickListener(this)

        exit = x.findViewById(R.id.imageViewX)
        exit.setOnClickListener(this)


        quanArrList = ArrayList()
        recipesID = ArrayList()

        listItemsChoosen = ArrayList()
        listItems = Recipe_Ingredients_List(listItemsChoosen)

        recipeQuantitiy = ArrayList()
        recipesQuantities = Recipe_Ingredients_List(recipeQuantitiy)



        addTable()
        initEvent()
        return x


    }


    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    private fun initEvent() {
        var j = 0


        // converting the strings into arr's
        var quantitiesArr = quantities.splitIgnoreEmpty(" ")
        var recipeIdsArr = recipeIds.splitIgnoreEmpty(" ")

        // converting arr's to Array Lists to be dynamic if we delete or add
        // and at the end we will return it to strings

        for (i in recipeIdsArr) {
            recipesID.add(i.toInt())
        }


        for (i in quantitiesArr) {
            quanArrList.add(i.toInt())
            recipesQuantities!!.list!!.add(i.toInt())
        }






        for (i in recipeList) {

            if (j >= recipeIdsArr.size) {
                break
            }
            if (i.recipeId == recipeIdsArr[j].toInt()) {


                var tbrow: TableRow = TableRow(this.context)
                tbrow.setTag(tablePosition++)
                totalCostDobule += i.totalCost * quantitiesArr[j].toInt()
                totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()


                var t1v: TextView = TextView(context)

                // t1v.setBackgroundResource(R.drawable.border)
                if(eventName.length>18){
                    var shorterName = eventName.substring(0,14)
                    shorterName+="..."
                    t1v.setText(shorterName)
                }
                else{
                    t1v.setText(" " + (eventName))
                }

                t1v.setTextColor(Color.BLACK)
                t1v.gravity = Gravity.CENTER
                //  t1v.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.addView(t1v)


                var t2v: ImageView = ImageView(context)


                t2v.setImageBitmap(i.pictureBitMap)
                val scaled = Bitmap.createScaledBitmap(
                    i.pictureBitMap,
                    80,
                    90,
                    true
                )
                //t2v.adjustViewBounds = true

                t2v.scaleType = (ImageView.ScaleType.CENTER_INSIDE)
                t2v.setImageBitmap(scaled)
                t2v.foregroundGravity = Gravity.CENTER


                tbrow.addView(t2v)

                var t3v: TextView = TextView(context)
                // t3v.setBackgroundResource(R.drawable.border)
                if(i.recipeName.length>18){
                    var shorterName = i.recipeName.substring(0,14)
                    shorterName+="..."
                    t3v.setText(shorterName)
                }
                else{
                    t3v.setText(i.recipeName)
                }

                t3v.setTextColor(Color.BLACK)
                t3v.setTextSize(10F)
                t3v.gravity = Gravity.CENTER
                //    t3v.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.addView(t3v)


                var t5v: TextView = TextView(context)
                // t4v.setBackgroundResource(R.drawable.border)
                t5v.setText(quantitiesArr[j])
                t5v.setTextColor(Color.BLACK)
                t5v.gravity = Gravity.CENTER
                //t4v.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.addView(t5v)


                var t6v: Button = Button(context)
                t6v.setTag(recipePos)
                t6v.setText("Del")
                t6v.setTextColor(Color.BLACK)
                t6v.gravity = Gravity.CENTER
                t6v.setTextSize(10F)
                tbrow.addView(t6v)



                t6v.setOnClickListener {
                    var i = tbrow.getTag() as Int - 1


                    stk.removeView(stk.getChildAt(tbrow.getTag() as Int))
                    totalCostDobule -= recipeList.get(t6v.getTag() as Int).totalCost * recipesQuantities!!.list!!.get(
                        tbrow.getTag() as Int - 1
                    ).toInt()
                    totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
                    totalCost.setText(totalCostDobule.toString())
                    recipesQuantities!!.list!!.removeAt(tbrow.getTag() as Int - 1)
                    recipeList.removeAt(t6v.getTag() as Int)
                    recipesID.removeAt(tbrow.getTag() as Int - 1)
                    tablePosition--

                    for (x in stk) {
                        if (x.getTag() as Int == 0)
                            continue
                        if (x.getTag() as Int > i) {
                            x.setTag(x.getTag() as Int - 1)
                            var y = x as TableRow
                            y.get(4).setTag(y.get(4).getTag() as Int - 1)
                            y.get(5).setTag(y.get(5).getTag() as Int - 1)

                        }

                    }
                }

                var t7v: Button = Button(context)
                t7v.setTag(recipePos)
                t7v.setText("Info")
                t7v.setTextSize(10F)
                t7v.setTextColor(Color.BLACK)
                t7v.gravity = Gravity.CENTER

                //t5v.setBackgroundResource(R.drawable.spinner_shape)
                t7v.setOnClickListener {
                    var instructions =
                        HowToStroreValue(recipeList.get(t7v.getTag() as Int).instructions.howToStore)
                    var dialog = MyRecipeIngredietns(
                        recipeList.get(t7v.getTag() as Int).listOfIngredients,
                        recipeList.get(t7v.getTag() as Int).recipeName,
                        recipeList.get(t7v.getTag() as Int).pictureBitMap,
                        recipeList.get(t7v.getTag() as Int).numOfPortions,
                        recipeList.get(t7v.getTag() as Int).quantityList,
                        recipeList.get(t7v.getTag() as Int).totalCost,
                        recipeList.get(t7v.getTag() as Int).typeOfMeal,
                        instructions
                    )
                    dialog.show(childFragmentManager, "MyRecipeIngredients")
                }
                tbrow.addView(t7v)



                stk.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.setBackgroundResource(R.drawable.spinner_shape)
                stk.addView(tbrow)
                j++

            }
            recipePos++
        }
        totalCostDobule = (DecimalFormat("##.####").format(totalCostDobule)).toDouble()
        totalCost.setText(totalCostDobule.toString())
        savedSize = recipeList.size
    }


    private fun addTable() {

        stk.setColumnShrinkable(5, true)
        stk.setColumnShrinkable(4, true)
        stk.setColumnShrinkable(2, true)
        stk.setColumnStretchable(2, true)
        stk.setColumnStretchable(5, true)
        stk.setColumnStretchable(4, true)
        val boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD)
        var tbrow0: TableRow = TableRow(context)

        var tv0: TextView = TextView(context)
        tv0.setText(" Event Name ")
        tv0.setTextColor(Color.BLACK)
        tv0.gravity = Gravity.CENTER

        // tv0.setTypeface(boldTypeface)
        //  tv0.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.addView(tv0)

        var tv1: TextView = TextView(context)
        tv1.setText(" Picture ")
        tv1.setTextColor(Color.BLACK)
        tv1.gravity = Gravity.CENTER
        // tv1.setBackgroundResource(R.drawable.spinner_shape)
        // tv1.setTypeface(boldTypeface)
        tbrow0.addView(tv1)

        var tv3: TextView = TextView(context)
        tv3.setText(" Name ")
        tv3.setTextColor(Color.BLACK)
        tv3.gravity = Gravity.CENTER
        // tv3.setBackgroundResource(R.drawable.spinner_shape)
        // tv3.setTypeface(boldTypeface)
        tbrow0.addView(tv3)


        var tv5: TextView = TextView(context)
        tv5.setText(" Quantity ")
        tv5.setTextColor(Color.BLACK)
        tv5.gravity = Gravity.CENTER
        // tv4.setBackgroundResource(R.drawable.spinner_shape)
        // tv5.setTypeface(boldTypeface)
        tbrow0.addView(tv5)

        var tv6: TextView = TextView(context)
        tv6.setText(" Delete")
        tv6.setTextColor(Color.BLACK)
        tv6.gravity = Gravity.CENTER
        // tv5.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.setTag(0)
        //tv6.setTypeface(boldTypeface)
        tbrow0.addView(tv6)


        var tv7: TextView = TextView(context)
        tv7.setText(" Info ")
        tv7.setTextColor(Color.BLACK)
        tv7.gravity = Gravity.CENTER
        // tv5.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.setBackgroundResource(R.drawable.spinner_shape)
        // tv7.setTypeface(boldTypeface)
        tbrow0.addView(tv7)




        stk.addView(tbrow0)


    }


    override fun onClick(p0: View?) {
        if (p0 == exit) {
            flag = false
            dismiss()
        } else if (p0 == chooseBtn) {


            listItems!!.list!!.clear()
            savedSize = recipeListChoose.size
            recipeList.clear()

            var dialog = Recipe_Schedule_Choose_Dialog(
                listItems!!,
                recipeList,
                recipesQuantities!!,
                ""
            )
            dialog.show(childFragmentManager, "Recipe_Schuedle_Choose")


        } else if (p0 == saveBtn) {


            eventName = eventNameEdit.text.toString()

            if (recipesID.isNotEmpty()) {
                recipeIds = ""
                quantities = ""


                for (i in recipesID) {
                    recipeIds += "" + i + " "
                }

                for (i in recipesQuantities!!.list!!) {
                    quantities += "" + i + " "
                }



                myEventRecylerviewadapter!!.setRecipeList(recipeList!!)


                var event = Event(
                    eventId,
                    UserInterFace.userID,
                    eventName,
                    quantities,
                    recipeIds,
                    totalCostDobule,
                    true
                )

                var s = AsynTaskNew(event, childFragmentManager,requireContext())
                s.execute()

                recipeIds = ""
                quantities = ""


                flag = false


            } else {
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)

                builder.setTitle("Delete Recipe")
                builder.setMessage("You can't delete all recipes!! , you can delete the Event itself!")

                builder.setPositiveButton(
                    "OK",
                    DialogInterface.OnClickListener { dialog, which -> // Do nothing but close the dialog

                        dialog.dismiss()
                    })
                val alert: AlertDialog = builder.create()
                alert.show()
            }
        } else if (p0 == changeNameBtn) {
            eventNameEdit.isEnabled = true
        }
    }


    override fun onDismiss(dialog: DialogInterface) {

        if (flag) {
            recipePos = 0
            val parentFragment: Fragment? = parentFragment
            if (parentFragment is DialogInterface.OnDismissListener) {
                (parentFragment as DialogInterface.OnDismissListener?)!!.onDismiss(dialog)
            }


            for (i in listItems!!.list!!) {


                var recipe = recipeList.get(i)
                recipeListChoose.add(recipe!!)


            }

            var j = 0
            for (i in recipeListChoose) {
                if (j > savedSize - 1) {


                    var tbrow: TableRow = TableRow(this.context)
                    tbrow.setTag(tablePosition++)

                    totalCostDobule += i.totalCost * recipesQuantities!!.list!!.get(tbrow.getTag() as Int - 1)
                    recipesID.add(i.recipeId)

                    var t1v: TextView = TextView(context)

                    // t1v.setBackgroundResource(R.drawable.border)
                    if(eventName.length>18){
                        var shorterName = eventName.substring(0,14)
                        shorterName+="..."
                        t1v.setText(shorterName)
                    }
                    else{
                        t1v.setText(" " + (eventName))
                    }


                    t1v.setTextColor(Color.BLACK)
                    t1v.gravity = Gravity.CENTER
                    //  t1v.setBackgroundResource(R.drawable.spinner_shape)
                    tbrow.addView(t1v)


                    var t2v: ImageView = ImageView(context)


                    t2v.setImageBitmap(i.pictureBitMap)
                    val scaled = Bitmap.createScaledBitmap(
                        i.pictureBitMap,
                        80,
                        80,
                        true
                    )
                    //t2v.adjustViewBounds = true

                    t2v.scaleType = (ImageView.ScaleType.CENTER_INSIDE)
                    t2v.setImageBitmap(scaled)
                    t2v.foregroundGravity = Gravity.CENTER

                    tbrow.addView(t2v)

                    var t3v: TextView = TextView(context)
                    // t3v.setBackgroundResource(R.drawable.border)
                    if(i.recipeName.length>18){
                        var shorterName = i.recipeName.substring(0,14)
                        shorterName+="..."
                        t3v.setText(shorterName)
                    }
                    else{
                        t3v.setText(i.recipeName)
                    }

                    t3v.setTextSize(10F)
                    t3v.setTextColor(Color.BLACK)
                    t3v.gravity = Gravity.CENTER
                    //    t3v.setBackgroundResource(R.drawable.spinner_shape)
                    tbrow.addView(t3v)


                    var t5v: TextView = TextView(context)
                    // t4v.setBackgroundResource(R.drawable.border)
                    t5v.setText(
                        recipesQuantities!!.list!!.get(tbrow.getTag() as Int - 1).toString()
                    )
                    t5v.setTextColor(Color.BLACK)
                    t5v.gravity = Gravity.CENTER
                    //t4v.setBackgroundResource(R.drawable.spinner_shape)
                    tbrow.addView(t5v)

                    var t6v: Button = Button(context)

                    t6v.setTag(recipePos)
                    t6v.setText("Del")
                    t6v.setTextColor(Color.BLACK)
                    t6v.gravity = Gravity.CENTER
                    t6v.setTextSize(10F)



                    t6v.setOnClickListener {
                        var i = tbrow.getTag() as Int - 1


                        stk.removeView(stk.getChildAt(tbrow.getTag() as Int))
                        totalCostDobule -= recipeListChoose.get(t6v.getTag() as Int).totalCost * recipesQuantities!!.list!!.get(
                            tbrow.getTag() as Int - 1
                        ).toInt()

                        totalCostDobule =
                            (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
                        totalCost.setText(totalCostDobule.toString())
                        recipesQuantities!!.list!!.removeAt(tbrow.getTag() as Int - 1)

                        recipeListChoose.removeAt(t6v.getTag() as Int)
                        recipesID.removeAt(tbrow.getTag() as Int - 1)
                        tablePosition--

                        for (x in stk) {
                            if (x.getTag() as Int == 0)
                                continue
                            if (x.getTag() as Int > i) {
                                x.setTag(x.getTag() as Int - 1)
                                var y = x as TableRow
                                y.get(4).setTag(y.get(4).getTag() as Int - 1)
                                y.get(5).setTag(y.get(5).getTag() as Int - 1)

                            }

                        }
                    }


                    tbrow.addView(t6v)


                    var t7v: Button = Button(context)
                    t7v.setTag(recipePos)
                    t7v.setText("Info")
                    t7v.setTextSize(10F)
                    t7v.setTextColor(Color.BLACK)
                    t7v.gravity = Gravity.CENTER

                    t7v.setOnClickListener {

                        var instructions =
                            HowToStroreValue(recipeList.get(t7v.getTag() as Int).instructions.howToStore)
                        var dialog = MyRecipeIngredietns(
                            recipeListChoose.get(t7v.getTag() as Int).listOfIngredients,
                            recipeListChoose.get(t7v.getTag() as Int).recipeName,
                            recipeListChoose.get(t7v.getTag() as Int).pictureBitMap,
                            recipeListChoose.get(t7v.getTag() as Int).numOfPortions,
                            recipeListChoose.get(t7v.getTag() as Int).quantityList,
                            recipeListChoose.get(t7v.getTag() as Int).totalCost,
                            recipeListChoose.get(t7v.getTag() as Int).typeOfMeal,
                            instructions
                        )
                        dialog.show(childFragmentManager, "MyRecipeIngredients")
                    }
                    tbrow.addView(t7v)



                    stk.setBackgroundResource(R.drawable.spinner_shape)
                    tbrow.setBackgroundResource(R.drawable.spinner_shape)
                    stk.addView(tbrow)


                }
                j++
                recipePos++
            }
            totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()
            totalCost.setText(totalCostDobule.toString())
        }
    }


}