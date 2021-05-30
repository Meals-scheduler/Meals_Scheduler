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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import com.example.meals_schdueler.R.layout
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


/// need to do :
// fix the image scale


class AddDailyScheduleFragment : Fragment(), View.OnClickListener,
    DialogInterface.OnDismissListener, GetAndPost {
    var builder: java.lang.StringBuilder? = null
    private var listItemsChoosen: ArrayList<Int>? = null
    private var recipeQuantitiy: ArrayList<Int>? = null
    private var listItems: Recipe_Ingredients_List? = null
    private lateinit var stk: TableLayout
    private lateinit var recipeList: ArrayList<Recipe>
    private lateinit var recipeChoosenNumOfMeal: ArrayList<Int>
    private lateinit var recipesID: ArrayList<Int>
    private var recipesQuantities: Recipe_Ingredients_List? = null

    //lateinit var stam : TextView
    private var columnCount = 1
    private lateinit var breakfastBtn: Button
    private lateinit var lunchBtn: Button
    private lateinit var dinnerBtn: Button
    private lateinit var saveBtn: Button
    private var mealChoosen: String = ""
    private var recipeNumbers: String = ""
    private var recipeIds: String = ""
    private lateinit var totalCost: EditText
    private var totalCostDobule: Double = 0.0
    private var dailyId = 0
    private var tablePosition = 1
    var j = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        listItemsChoosen = ArrayList()
        listItems = Recipe_Ingredients_List(listItemsChoosen)
        recipeList = ArrayList()
        recipeChoosenNumOfMeal = ArrayList()
        recipesID = ArrayList()
        recipeQuantitiy = ArrayList()
        recipesQuantities = Recipe_Ingredients_List(recipeQuantitiy)
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

        var tv4: TextView = TextView(context)
        tv4.setText(" Price ")
        tv4.setTextColor(Color.BLACK)
        tv4.gravity = Gravity.CENTER
        // tv4.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.addView(tv4)

        var tv5: TextView = TextView(context)
        tv5.setText(" Delete ")
        tv5.setTextColor(Color.BLACK)
        tv5.gravity = Gravity.CENTER
        // tv5.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.setTag(0)
        tbrow0.addView(tv5)


        var tv6: TextView = TextView(context)
        tv6.setText(" Info ")
        tv6.setTextColor(Color.BLACK)
        tv6.gravity = Gravity.CENTER
        // tv5.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.setBackgroundResource(R.drawable.spinner_shape)
        tbrow0.setTag(0)
        tbrow0.addView(tv6)




        stk.addView(tbrow0)


    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            AddDailyScheduleFragment().apply {
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
        val x = inflater.inflate(layout.add_daily_schedule, null)

        //stam = x.findViewById(R.id.elad)
        stk = x.findViewById(R.id.tableLayout)
        breakfastBtn = x.findViewById(R.id.breakfastBtn)
        lunchBtn = x.findViewById(R.id.lunchBtn)
        dinnerBtn = x.findViewById(R.id.dinnerBtn)
        totalCost = x.findViewById(R.id.editTextTotalCost)
        saveBtn = x.findViewById(R.id.saveBtn)

        saveBtn.setOnClickListener(this)
        breakfastBtn.setOnClickListener(this)
        lunchBtn.setOnClickListener(this)
        dinnerBtn.setOnClickListener(this)

        //val view = inflater.inflate(com.example.meals_schdueler.R.layout.add_daily_schedule, container, false)

        addTable()
        return x
    }

    override fun onClick(p0: View?) {
        if (p0 == breakfastBtn) {
            listItems!!.list!!.clear()
            recipeList.clear()
            mealChoosen = "Breakfast"
            recipeChoosenNumOfMeal.add(0)
            var dialog = Recipe_Schedule_Choose_Dialog(
                listItems!!,
                UserPropertiesSingelton.getInstance()!!.getUserRecipess()!!,
                recipesQuantities!!
            )
            dialog.show(childFragmentManager, "Recipe_Schuedle_Choose")

        } else if (p0 == lunchBtn) {
            listItems!!.list!!.clear()
            recipeList.clear()
            mealChoosen = "Lunch"
            recipeChoosenNumOfMeal.add(1)
            var dialog = Recipe_Schedule_Choose_Dialog(
                listItems!!,
                UserPropertiesSingelton.getInstance()!!.getUserRecipess()!!,
                recipesQuantities!!
            )
            dialog.show(childFragmentManager, "Recipe_Schuedle_Choose")

        } else if (p0 == dinnerBtn) {
            listItems!!.list!!.clear()
            recipeList.clear()
            mealChoosen = "Dinner"
            recipeChoosenNumOfMeal.add(2)
            var dialog = Recipe_Schedule_Choose_Dialog(
                listItems!!,
                UserPropertiesSingelton.getInstance()!!.getUserRecipess()!!,
                recipesQuantities!!
            )
            dialog.show(childFragmentManager, "Recipe_Schuedle_Choose")

        } else if (p0 == saveBtn) {


            for (i in recipesID) {
                recipeIds += "" + i + " "
            }
            Log.v("Elad1", "IDS : " + recipeIds)
            var s = AsynTaskNew(this, childFragmentManager)
            s.execute()
        }

    }


    override fun onDismiss(p0: DialogInterface?) {
        // on dissmiss event , when we dissmiss the ingrdeitns selcection dialog we want to update the list with
        // the chosen ingredients.
        for (i in listItems!!.list!!) {
            var recipe = UserPropertiesSingelton.getInstance()!!.getUserRecipess()!!.get(i)
            recipeList.add(recipe)

        }
        Log.v("Elad1", "Quantities : " + recipesQuantities!!.list.toString())
        Log.v("Elad1", "QuantitiesSize : " + recipesQuantities!!.list!!.size)



        for (i in recipeList) {

            totalCostDobule += i.totalCost
            recipesID.add(i.recipeId)


            var tbrow: TableRow = TableRow(this.context)
            tbrow.setTag(tablePosition)

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
                i.pictureBitMap, 80,
                ((i.pictureBitMap.getHeight() * (100.0 / i.pictureBitMap.getWidth())).toInt()), true
            )
            //t2v.adjustViewBounds = true

            t2v.scaleType = (ImageView.ScaleType.CENTER_INSIDE)
            t2v.setImageBitmap(scaled)


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

            var t5v: Button = Button(context)
            t5v.setTag(tablePosition)
            t5v.setText("Delete")
            t5v.setTextColor(Color.BLACK)
            t5v.gravity = Gravity.CENTER
            //t5v.setBackgroundResource(R.drawable.spinner_shape)
            t5v.setOnClickListener {
                //stk.removeViewAt(t5v.getTag() as Int)
                var i = t5v.getTag() as Int

                stk.removeView(stk.getChildAt(t5v.getTag() as Int))
                totalCostDobule -= recipeList.get(t5v.getTag() as Int - 1).totalCost
                totalCost.setText(totalCostDobule.toString())
                recipesID.remove(stk.getChildAt(t5v.getTag() as Int))
                recipesQuantities!!.list!!.removeAt(t5v.getTag() as Int -1)
                tablePosition--

                for (x in stk) {
                    if (x.getTag() as Int == 0)
                        continue
                    if (x.getTag() as Int > i) {
                        x.setTag(x.getTag() as Int - 1)
                        var y = x as TableRow
                        y.get(4).setTag(y.get(4).getTag() as Int - 1)

                    }

                }


            }
            tbrow.addView(t5v)


            var t6v: Button = Button(context)
            t6v.setTag(tablePosition++)
            t6v.setText("Info")
            t6v.setTextColor(Color.BLACK)
            t6v.gravity = Gravity.CENTER
            //t5v.setBackgroundResource(R.drawable.spinner_shape)
            t6v.setOnClickListener {
                var dialog = MyRecipeIngredietns(
                    recipeList.get(t6v.getTag() as Int - 1).listOfIngredients,
                    recipeList.get(t6v.getTag() as Int - 1).recipeName,
                    recipeList.get(t6v.getTag() as Int - 1).pictureBitMap,
                    recipeList.get(t6v.getTag() as Int - 1).numOfPortions,
                    recipeList.get(t6v.getTag() as Int - 1).quantityList,
                    recipeList.get(t6v.getTag() as Int - 1).totalCost
                )
                dialog.show(childFragmentManager, "MyRecipeIngredients")
            }
            tbrow.addView(t6v)



            stk.setBackgroundResource(R.drawable.spinner_shape)
            tbrow.setBackgroundResource(R.drawable.spinner_shape)
            stk.addView(tbrow)


        }

        totalCost.setText(totalCostDobule.toString())
    }

    override fun DoNetWorkOpreation(): String {
        // making arr of ingreditns ID to send
        for (i in recipeChoosenNumOfMeal) {
            recipeNumbers += "" + i + " "

        }
        recipeChoosenNumOfMeal.clear()


        var input = ""

        // if we insert a new ingredient and not updating
        //if (!isUpdate) {
        //   dailyId = getDailyID().toInt() + 1 // getting current RecipeID first
        Log.v("Elad1", "Daily is " + dailyId)
        // }

        // ingredientID = 1
        //   Log.v("Elad1", "current ID " + ingredientID)
        if (dailyId != -1)
            input = postData() // now we upload the current ingredient details.

        return input
    }

    private fun postData(): String {

        return try {

            // values go to - Ingredient Table
            var link =
                "https://elad1.000webhostapp.com/postDailySchedule.php?ownerID=" + UserInterFace.userID;
//            if (isUpdate){
//                link = "https://elad1.000webhostapp.com/updateIngredient.php?ingredientID="+ingredientID
//
//            }

            // print here ingredient elemtnes
            Log.v("Elad1", recipeNumbers)
            Log.v("Elad1", recipeIds)
            var data = URLEncoder.encode("DailyID", "UTF-8") + "=" +
                    URLEncoder.encode(dailyId.toString(), "UTF-8")
            data += "&" + URLEncoder.encode("numOfMeals", "UTF-8") + "=" +
                    URLEncoder.encode(recipeNumbers, "UTF-8")
            data += "&" + URLEncoder.encode("recipeIds", "UTF-8") + "=" +
                    URLEncoder.encode(recipeIds, "UTF-8")




            Log.v("Elad1", data)
            Log.v("Elad1", "started asyn 1")
            val url = URL(link)
            val conn = url.openConnection()
            conn.readTimeout = 10000
            conn.connectTimeout = 15000
            conn.doOutput = true
            val wr = OutputStreamWriter(conn.getOutputStream())
            wr.write(data)
            wr.flush()
            val reader = BufferedReader(InputStreamReader(conn.getInputStream()))
            builder = StringBuilder()
            var line: String? = null
            Log.v("Elad1", "started asyn2")
            // Read Server Response
            while (reader.readLine().also { line = it } != null) {
                builder!!.append(line)
                break
            }
            builder.toString()
            Log.v("Elad1", builder.toString())
            Log.v("Elad1", "asyn worked")
        } catch (e: Exception) {
            Log.v("Elad1", "Failled")
        }.toString()

    }


    private fun getDailyID(): String {
        val link = "https://elad1.000webhostapp.com/getDailyID.php"
        Log.v("Elad1", "here222222222")

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


        Log.v("Elad1", "Id came is" + sb.toString())
        return sb.toString()
    }

    override fun getData(str: String) {
        print("DD")
    }
}

