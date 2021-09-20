package com.example.meals_schdueler

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.allyants.notifyme.NotifyMe
import com.example.meals_schdueler.dummy.DailySchedule
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class My_Daily_RecylerViewAdapter(

    values: ArrayList<DailySchedule>,
    childFragmentManager: FragmentManager,
    context: Context?,
    activity: Activity
) : RecyclerView.Adapter<My_Daily_RecylerViewAdapter.ViewHolder>(), GetAndPost,
    deleteInterface {

    //var builder: java.lang.StringBuilder? = null

    // private var mValues: ArrayList<DailySchedule> = values
    private var mValues: ArrayList<DailySchedule> = values
    private var childFragmentManager = childFragmentManager
    private lateinit var recipeList: ArrayList<Recipe>
    private var context = context
    private var activity = activity


    private var dailyToDelete: Int? = null

    private var date = ""
    private lateinit var cal: Calendar
    private lateinit var tpd: TimePickerDialog
    private lateinit var dpd: DatePickerDialog
    private var queryToExcute = ""
    private var dailyID = -1
    private var recipeIDs = ""
    private var quantitiesStr = ""
    private var numOfMeals = ""
    private var pos = -1


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): My_Daily_RecylerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_daily_schedule, parent, false)


        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: My_Daily_RecylerViewAdapter.ViewHolder, position: Int) {
        var item: DailySchedule = mValues[position]!! // each item postion
        holder.mItem = item

        holder.numOfDaily.setText(position.toString())

        holder.edit.setOnClickListener {


            queryToExcute = "edit"
            dailyID = mValues.get(position)!!.dailyId
            quantitiesStr = mValues.get(position)!!.quantities
            recipeIDs = mValues.get(position)!!.recipeIds
            numOfMeals = mValues.get(position)!!.numOfMeals
            pos = position + 1
            Log.v("Elad1", "Num of meals " + numOfMeals)
            Log.v("Elad1", "recipe ids " + recipeIDs)
            var s = AsynTaskNew(this, childFragmentManager)
            s.execute()
            // copying the Recipe list so if we edit it and then quit without saving, it wouldn't change the original list

        }



        holder.info.setOnClickListener {

            queryToExcute = "info"
            dailyID = mValues.get(position)!!.dailyId
            quantitiesStr = mValues.get(position)!!.quantities
            recipeIDs = mValues.get(position)!!.recipeIds
            numOfMeals = mValues.get(position)!!.numOfMeals
            pos = position + 1


            var s = AsynTaskNew(this, childFragmentManager)
            s.execute()


        }

        holder.delete.setOnClickListener {
            dailyToDelete = position
            var dialog = DeleteAlertDialog(
                "",
                null,
                mValues.get(position)!!.dailyId,
                "Daily",
                this

            )
            dialog.show(childFragmentManager, "DeleteDaily")

        }

        holder.date.setOnClickListener {

            cal = Calendar.getInstance()
            val currentYear = cal[Calendar.YEAR]
            val currentMonth = cal[Calendar.MONTH]
            val currentDay = cal[Calendar.DAY_OF_MONTH]

            dpd = DatePickerDialog(
                activity,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    tpd.show()


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
                activity,
                TimePickerDialog.OnTimeSetListener { view, hour, minute ->

                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    var month = cal.get(Calendar.MONTH) + 1
                    date = cal.get(Calendar.YEAR).toString() + "-" + month
                        .toString() + "-" + cal.get(Calendar.DAY_OF_MONTH).toString()


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

                                    Log.v("Elad1", "clicked yes")

                                }
                                DialogInterface.BUTTON_NEGATIVE -> {

                                    Log.v("Elad1", "clicked no")
                                }
                            }
                        }

                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("Would you like to get notification on the specific day?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show()

                    var upcoming = UpComingScheudule(
                        -1,
                        date,
                        11
                    )


                    var s = AsynTaskNew(upcoming, childFragmentManager)
                    s.execute()


                },
                hour,
                min,
                false
            )
        }
    }

    fun setmValues(mValues: ArrayList<DailySchedule>) {

        this.mValues = mValues

        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }


    fun setRecipeList(recipeList: ArrayList<Recipe>) {

        this.recipeList = recipeList

        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    override fun getItemCount(): Int = mValues.size


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var numOfDaily: TextView = view.findViewById(R.id.numOfDailyTextView)
        var info: Button = view.findViewById(R.id.buttonInfo)
        var edit: Button = view.findViewById(R.id.buttonEdit)
        var date: Button = view.findViewById(R.id.buttonSchedule)
        var delete: Button = view.findViewById(R.id.buttonDel)
        var cart: ImageView = view.findViewById(R.id.imageViewCart)
        lateinit var mItem: DailySchedule


        override fun toString(): String {
            return super.toString()
        }
    }

    override fun DoNetWorkOpreation(): String {
        var string = UserInterFace.userID.toString() + " " + dailyID

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

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    override fun getData(str: String) {
        var start = 0
        recipeList!!.clear()
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




            this!!.setRecipeList(recipeList!!)
            if (queryToExcute.equals("info")) {
                var dialog = DailyDialogInfo(
                    recipeList!!,
                    quantitiesStr,
                    numOfMeals,
                    recipeIDs,
                    pos,
                    dailyID

                )
                dialog.show(childFragmentManager, "DailyDialogInfo")
            } else {
                var tempRecipeList: ArrayList<Recipe> = ArrayList()
                for (i in recipeList) {
                    tempRecipeList.add(i)
                }

                Log.v("Elad1", "List size " + tempRecipeList.size)
                Log.v("Elad1", "quantities " + quantitiesStr)
                Log.v("Elad1", "num of meals " + numOfMeals)
                Log.v("Elad1", "recipe ids" + recipeIDs)
                Log.v("Elad1", "daily id " + dailyID)
                var dialog = EditDailyDialog(
                    tempRecipeList,
                    quantitiesStr,
                    numOfMeals,
                    recipeIDs,
                    pos,
                    this,
                    dailyID
                )
                dialog.show(childFragmentManager, "dailyEdit")
            }


        }
    }


    override fun toDelete(isDelete: Boolean) {
        if (isDelete) {
            mValues.removeAt(dailyToDelete!!)
            notifyDataSetChanged()
        }
    }


}