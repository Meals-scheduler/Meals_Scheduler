package com.example.meals_schdueler

import android.content.DialogInterface
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
import androidx.core.view.size
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import java.text.DecimalFormat

class DailyDialogInfo(
    recipeList: ArrayList<Recipe>,
    quantities: String,
    numOfMeals: String,
    recipeIds: String,
    pos: Int,
    dailyId: Int,
) : DialogFragment(), View.OnClickListener,
    DialogInterface.OnDismissListener {


    private lateinit var stk: TableLayout
    private lateinit var totalCost: EditText
    private lateinit var exit: ImageView
    private lateinit var title: TextView

    // private var recipesQuantities: Recipe_Ingredients_List? = null
    // private var recipeQuantitiy: ArrayList<Int>? = null
    private lateinit var recipeChoosenNumOfMeal: ArrayList<Int>
    private lateinit var quanArrList: ArrayList<Int>
    private lateinit var recipesID: ArrayList<Int>

    private var dailyId = dailyId

    private var recipeIds = recipeIds
    private var numOfMeals = numOfMeals
    private var quantities = quantities
    private var recipeList = recipeList
    private var tablePosition = 1
    private var totalCostDobule: Double = 0.0
    private var position = pos
    private var mealChoosen = ""
    private var recipePos = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var x: View = inflater.inflate(R.layout.daily_info, container, false)
        stk = x.findViewById(R.id.tableLayout)
        totalCost = x.findViewById(R.id.editTextTotalCost)
        title = x.findViewById(R.id.dailyInfo)
        title.setText("Daily No. " + position)
        recipeChoosenNumOfMeal = ArrayList()
        quanArrList = ArrayList()
        recipesID = ArrayList()
        //  recipeQuantitiy = ArrayList()
        // recipesQuantities = Recipe_Ingredients_List(recipeQuantitiy)


        exit = x.findViewById(R.id.imageViewX)
        exit.setOnClickListener(this)

        addTable()
        initDaily()
        return x


    }

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    private fun initDaily() {
        var j = 0

       // stk.setColumnShrinkable(2, true)
        //stk.setColumnStretchable(2, true)

        // 1converting the strings into arr's
        var numOfMealsArr = numOfMeals.splitIgnoreEmpty(" ")
        var quantitiesArr = quantities.splitIgnoreEmpty(" ")
        var recipeIdsArr = recipeIds.splitIgnoreEmpty(" ")

        // converting arr's to Array Lists to be dynamic if we delete or add
        // and at the end we will return it to strings

        for (i in recipeIdsArr) {
            recipesID.add(i.toInt())
        }

        for (i in numOfMealsArr) {
            recipeChoosenNumOfMeal.add(i.toInt())
        }

        for (i in quantitiesArr) {
            quanArrList.add(i.toInt())
            //recipesQuantities!!.list!!.add(i.toInt())
        }




        for (i in recipeList) {

            if (j >= recipeIdsArr.size) {
                break
            }
            if (recipesID.contains(i.recipeId)) {


                when (numOfMealsArr[j]) {
                    "0" -> mealChoosen = "Breakfast"
                    "1" -> mealChoosen = "Lunch"
                    "2" -> mealChoosen = "Dinner"
                }

                var tbrow: TableRow = TableRow(this.context)
                tbrow.setTag(tablePosition++)
                totalCostDobule += i.totalCost * quantitiesArr[j].toInt()
                totalCostDobule = (DecimalFormat("##.##").format(totalCostDobule)).toDouble()


                var t1v: TextView = TextView(context)

                // t1v.setBackgroundResource(R.drawable.border)
                t1v.setText(" " + (mealChoosen))
                t1v.setTextColor(Color.BLACK)
                t1v.gravity = Gravity.CENTER
                //  t1v.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.addView(t1v)


                var t2v: ImageView = ImageView(context)


                t2v.setImageBitmap(i.pictureBitMap)
                val scaled = Bitmap.createScaledBitmap(
                    i.pictureBitMap,
                    80,
                    110,
                    true
                )
                //t2v.adjustViewBounds = true

                t2v.scaleType = (ImageView.ScaleType.FIT_CENTER)
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
//
//                var t4v: TextView = TextView(context)
//                // t4v.setBackgroundResource(R.drawable.border)
//                t4v.setText(i.totalCost.toString())
//                t4v.setTextColor(Color.BLACK)
//                t4v.gravity = Gravity.CENTER
//                //t4v.setBackgroundResource(R.drawable.spinner_shape)
//                tbrow.addView(t4v)


                var t5v: TextView = TextView(context)
                // t4v.setBackgroundResource(R.drawable.border)
                t5v.setText(quantitiesArr[j])
                t5v.setTextColor(Color.BLACK)
                t5v.gravity = Gravity.CENTER
                //t4v.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.addView(t5v)


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

    }

    private fun addTable() {

        var tbrow0: TableRow = TableRow(context)

        var tv0: TextView = TextView(context)
        tv0.setText(" Category ")
        tv0.setTextColor(Color.BLACK)
        tv0.gravity = Gravity.CENTER
        //  tv0.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.addView(tv0)

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

//        var tv4: TextView = TextView(context)
//        tv4.setText(" Price ")
//        tv4.setTextColor(Color.BLACK)
//        tv4.gravity = Gravity.CENTER
//        // tv4.setBackgroundResource(R.drawable.spinner_shape)
//        tbrow0.addView(tv4)

        var tv5: TextView = TextView(context)
        tv5.setText(" Quantity ")
        tv5.setTextColor(Color.BLACK)
        tv5.gravity = Gravity.CENTER
        // tv4.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.addView(tv5)


        var tv7: TextView = TextView(context)
        tv7.setText(" Info ")
        tv7.setTextColor(Color.BLACK)
        tv7.gravity = Gravity.CENTER
        // tv5.setBackgroundResource(R.drawable.spinner_shape)

        tbrow0.addView(tv7)


        tbrow0.setBackgroundResource(R.drawable.spinner_shape)

        stk.addView(tbrow0)


    }
//
//    override fun onDismiss(dialog: DialogInterface) {
//        super.onDismiss(dialog)
//
//        val parentFragment: Fragment? = parentFragment
//        if (parentFragment is DialogInterface.OnDismissListener) {
//            (parentFragment as DialogInterface.OnDismissListener?)!!.onDismiss(dialog)
//        }
//
//
//
//    }

    override fun onClick(p0: View?) {
        if (p0 == exit) {
            dismiss()
        }
    }


}