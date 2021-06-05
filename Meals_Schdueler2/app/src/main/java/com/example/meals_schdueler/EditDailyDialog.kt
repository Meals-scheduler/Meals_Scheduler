package com.example.meals_schdueler

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
import androidx.core.view.size
import androidx.fragment.app.DialogFragment
import java.text.DecimalFormat

class EditDailyDialog(
    recipeList: ArrayList<Recipe>,
    quantities: String,
    numOfMeals: String,
    recipeIds: String,
    pos: Int,
) : DialogFragment(), View.OnClickListener {


    private lateinit var stk: TableLayout
    private lateinit var totalCost: EditText
    private lateinit var exit: ImageView
    private lateinit var title: TextView
    var recipeIds = recipeIds
    var numOfMeals = numOfMeals
    var quantities = quantities
    var recipeList = recipeList
    var mealChoosen = ""
    private var tablePosition = 1
    private var totalCostDobule: Double = 0.0
    private var position = pos


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var x: View = inflater.inflate(R.layout.edit_daily, container, false)
        stk = x.findViewById(R.id.tableLayout)
        totalCost = x.findViewById(R.id.editTextTotalCost)
        title = x.findViewById(R.id.editNumberOfDailyTextView)
        title.setText("Edit Daily No. " + position)
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

        var numOfMealsArr = numOfMeals.splitIgnoreEmpty(" ")
        var quantitiesArr = quantities.splitIgnoreEmpty(" ")
        var recipeIdsArr = recipeIds.splitIgnoreEmpty(" ")

        var quanArrList = ArrayList<String>()

        for(i in quantitiesArr){
            quanArrList.add(i)
        }


        for (i in recipeList) {
            if (j >= recipeIdsArr.size) {
                break
            }
            if (i.recipeId == recipeIdsArr[j].toInt()) {


                when (numOfMealsArr[j]) {
                    "0" -> mealChoosen = "Breakfast"
                    "1" -> mealChoosen = "Lunch"
                    "2" -> mealChoosen = "Dinner"
                }

                var tbrow: TableRow = TableRow(this.context)
                tbrow.setTag(tablePosition)
                totalCostDobule += i.totalCost * quantitiesArr[j].toInt()


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
                    ((i.pictureBitMap.height * (100.0 / i.pictureBitMap.width)).toInt()),
                    true
                )
                //t2v.adjustViewBounds = true

                t2v.scaleType = (ImageView.ScaleType.CENTER_INSIDE)
                t2v.setImageBitmap(scaled)
                t2v.foregroundGravity = Gravity.CENTER

                tbrow.addView(t2v)

                var t3v: TextView = TextView(context)
                // t3v.setBackgroundResource(R.drawable.border)
                t3v.setText(i.recipeName)
                t3v.setTextColor(Color.BLACK)
                t3v.gravity = Gravity.CENTER
                //    t3v.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.addView(t3v)

                var t4v: TextView = TextView(context)
                // t4v.setBackgroundResource(R.drawable.border)
                t4v.setText(i.totalCost.toString())
                t4v.setTextColor(Color.BLACK)
                t4v.gravity = Gravity.CENTER
                //t4v.setBackgroundResource(R.drawable.spinner_shape)
                tbrow.addView(t4v)


                var t5v: TextView = TextView(context)
                // t4v.setBackgroundResource(R.drawable.border)
                t5v.setText(quantitiesArr[j])
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
                tbrow.addView(t6v)

                t6v.setOnClickListener {
                    var i = t6v.getTag() as Int

                    Log.v("Elad1","Quantitiy: " +quanArrList.toString())
                    Log.v("Elad1","pos: " + (t6v.getTag() as Int-1))
                    Log.v("Elad1", recipeList.get(t6v.getTag() as Int - 1).totalCost.toString())
                    stk.removeView(stk.getChildAt(t6v.getTag() as Int))
                    totalCostDobule -= recipeList.get(t6v.getTag() as Int - 1).totalCost * quanArrList.get(t6v.getTag() as Int -1).toInt()
                    totalCostDobule = (DecimalFormat("##.####").format(totalCostDobule)).toDouble()
                    totalCost.setText(totalCostDobule.toString())
                    quanArrList.removeAt(t6v.getTag() as Int -1)
                    recipeList.removeAt(t6v.getTag() as Int - 1)
                    tablePosition--

                    for (x in stk) {
                        if (x.getTag() as Int == 0)
                            continue
                        if (x.getTag() as Int > i) {
                            x.setTag(x.getTag() as Int - 1)
                            var y = x as TableRow
                            //changing info delete tag
                            y.get(5).setTag(y.get(5).getTag() as Int - 1)
                            //changing infp button tag
                            y.get(6).setTag(y.get(6).getTag() as Int - 1)

                        }

                    }
                }

                var t7v: Button = Button(context)
                t7v.setTag(tablePosition++)
                t7v.setText("Info")
                t7v.setTextSize(10F)
                t7v.setTextColor(Color.BLACK)
                t7v.gravity = Gravity.CENTER

                //t5v.setBackgroundResource(R.drawable.spinner_shape)
                t7v.setOnClickListener {
                    Log.v("Elad1", "List size" + recipeList.size)
                    Log.v("Elad1", "table size" + stk.size)
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
                j++
            }
        }
        totalCostDobule = (DecimalFormat("##.####").format(totalCostDobule)).toDouble()
        totalCost.setText(totalCostDobule.toString())
    }


    private fun addTable() {

        stk.setColumnShrinkable(5, true)
        stk.setColumnShrinkable(6, true)
        stk.setColumnStretchable(5, true)
        stk.setColumnStretchable(6, true)
        val boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD)
        var tbrow0: TableRow = TableRow(context)

        var tv0: TextView = TextView(context)
        tv0.setText(" Category ")
        tv0.setTextColor(Color.BLACK)
        tv0.gravity = Gravity.CENTER

        tv0.setTypeface(boldTypeface)
        //  tv0.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.addView(tv0)

        var tv1: TextView = TextView(context)
        tv1.setText(" Picture ")
        tv1.setTextColor(Color.BLACK)
        tv1.gravity = Gravity.CENTER
        // tv1.setBackgroundResource(R.drawable.spinner_shape)
        tv1.setTypeface(boldTypeface)
        tbrow0.addView(tv1)

        var tv3: TextView = TextView(context)
        tv3.setText(" Name ")
        tv3.setTextColor(Color.BLACK)
        tv3.gravity = Gravity.CENTER
        // tv3.setBackgroundResource(R.drawable.spinner_shape)
        tv3.setTypeface(boldTypeface)
        tbrow0.addView(tv3)

        var tv4: TextView = TextView(context)
        tv4.setText(" Price ")
        tv4.setTextColor(Color.BLACK)
        tv4.gravity = Gravity.CENTER
        // tv4.setBackgroundResource(R.drawable.spinner_shape)
       // tv4.setTypeface(boldTypeface)
        tbrow0.addView(tv4)

        var tv5: TextView = TextView(context)
        tv5.setText(" Quantity ")
        tv5.setTextColor(Color.BLACK)
        tv5.gravity = Gravity.CENTER
        // tv4.setBackgroundResource(R.drawable.spinner_shape)
       // tv5.setTypeface(boldTypeface)
        tbrow0.addView(tv5)

        var tv6: TextView = TextView(context)
        tv6.setText(" Delete ")
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
            dismiss()
        }
    }

}