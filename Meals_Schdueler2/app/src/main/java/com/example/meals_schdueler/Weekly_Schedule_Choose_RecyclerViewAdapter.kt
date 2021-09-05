package com.example.meals_schdueler

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meals_schdueler.dummy.DailySchedule
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class Weekly_Schedule_Choose_RecyclerViewAdapter(

    var weeklyValues: ArrayList<WeeklySchedule>,
    //var dailyValues: TreeMap<String, DailySchedule>,
    //var recipes:HashMap<String, Recipe>,
    weeklyId: ArrayList<Int>?,
    childFragmentManager: FragmentManager
) : RecyclerView.Adapter<Weekly_Schedule_Choose_RecyclerViewAdapter.ViewHolder>() , GetAndPost  {

    private var mValues: ArrayList<WeeklySchedule> = weeklyValues
    private var dailyList: ArrayList<DailySchedule> = ArrayList()
    // private var mValuesDaily: TreeMap<String, DailySchedule> = dailyValues


    //    private var recipe = recipes
    private var childFragmentManager = childFragmentManager
    private var weeklyId = weeklyId

    private var weeklyID = -1
    private var dailyIds = ""
    private var numOfDay = ""
    private var pos = -1
    private var totalCostDobule = 0.0


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Weekly_Schedule_Choose_RecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.weekly_choose, parent, false)


        return ViewHolder(view)
    }

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item: WeeklySchedule = mValues[position]!! // each item postion
        holder.mItem = item

        holder.numOfDaily.setText(position.toString())

        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)


        holder.weeklyInfo.setOnClickListener {

            weeklyID = mValues.get(position)!!.weeklyId
            dailyIds = mValues.get(position)!!.dailyIds
            numOfDay = mValues.get(position)!!.numOfDay
            pos = position + 1
            totalCostDobule = mValues.get(position).totalCost


            var s = AsynTaskNew(this, childFragmentManager)
            s.execute()

        }


        holder.choose.setChecked(holder.arr[position]);
        holder.choose.setOnClickListener() {

//
            if (holder.choose.isChecked == true && (weeklyId!!.isEmpty())) {
                mValues.add(item)
                weeklyId!!.add(position)
                holder.arr[position] = true

            } else if (holder.choose.isChecked == false) {
                if (weeklyId!!.contains(position)) {
                    holder.arr[position] = false
                    weeklyId!!.remove(position)

                }

            }


        }


    }


    fun setmValues(mValues: ArrayList<WeeklySchedule>) {
        this.mValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    fun setDailyList(dailyList: ArrayList<DailySchedule>) {

        this.dailyList = dailyList

        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val idView: TextView = view.findViewById(R.id.item_number)
        //val contentView: TextView = view.findViewById(R.id.content)

        var weeklyInfo: Button = view.findViewById(R.id.buttonInfo)
        var cart: ImageView = view.findViewById(R.id.imageViewCart)
        var numOfDaily: TextView = view.findViewById(R.id.numOfWeeklyTextView)
        var choose: CheckBox = view.findViewById(R.id.WeeklyCheckBox)
        val arr = Array(mValues.size, { i -> false })
        lateinit var mItem: WeeklySchedule


        override fun toString(): String {
            return super.toString()
        }


    }

    override fun getItemCount(): Int = weeklyValues.size

    override fun DoNetWorkOpreation(): String {
        var string = UserInterFace.userID.toString() + " " + weeklyID


        var link =
            "https://elad1.000webhostapp.com/getDailyForWeekly.php?ownerIDAndWeekly=" + string


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

        dailyList!!.clear()
        if (!str.equals("")) {
            var quantities = ""
            var numOfMeal = ""
            var recipeIds = ""
            var totalcost = 0.011


            val dailyInfo: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()

            // map to map each DailyID with a key as ID and contains all 3
            // array lists (e.g - quantities,recipeIds,numOfMeals)
            var map: HashMap<String, ArrayList<String>> = HashMap()
            var mapTotalCost: HashMap<String, Double> = HashMap()
            // first attach each meal to its dailyID.
            var dailyInfo2 = dailyInfo[0].splitIgnoreEmpty("*")
            var currentDailyID = dailyInfo2[0].toInt()
            for (i in dailyInfo.indices) {
                dailyInfo2 = dailyInfo[i].splitIgnoreEmpty("*")
                //means we switch to the next DailyID
                if (dailyInfo2[0].toInt() != currentDailyID) {
                    var totalLists: ArrayList<String> = ArrayList()
                    totalLists.add(quantities)
                    totalLists.add(numOfMeal)
                    totalLists.add(recipeIds)

                    // gathering all quantities , numOfMeal and recipeIds under
                    // the key of that DailyID
                    map.put(currentDailyID.toString(), totalLists)
                    mapTotalCost.put(currentDailyID.toString(), totalcost)
                    //switching to the next DailyID
                    currentDailyID = dailyInfo2[0].toInt()

                    // clearing the variables for next DailyID
                    quantities = ""
                    numOfMeal = ""
                    recipeIds = ""
                }
                quantities += "" + dailyInfo2[5] + " "
                numOfMeal += "" + dailyInfo2[3] + " "
                recipeIds += "" + dailyInfo2[4] + " "
                // saving the last total cost
                totalcost = dailyInfo2[2].toDouble()
            }
            if (!quantities.equals("")) {
                var totalLists: ArrayList<String> = ArrayList()
                totalLists.add(quantities)
                totalLists.add(numOfMeal)
                totalLists.add(recipeIds)
                map.put(currentDailyID.toString(), totalLists)
                mapTotalCost.put(currentDailyID.toString(), totalcost)
            }

            //  recipeNumbers += "" + i + " "
            // making DailyScheudle objects
            currentDailyID = -1

            var dailyIdsArr = dailyIds.splitIgnoreEmpty(" ")

            for (i in dailyIdsArr) {

                for (j in dailyInfo.indices) {
                    var dailyInfo2 = dailyInfo[j].splitIgnoreEmpty("*")
                    if (currentDailyID != dailyInfo2[0].toInt() && i.toInt() == dailyInfo2[0].toInt()) {
                        dailyList!!.add(
                            DailySchedule(
                                dailyInfo2[0].toInt(),
                                dailyInfo2[1].toInt(),
                                map.get(dailyInfo2[0])!!.get(1),
                                map.get(dailyInfo2[0])!!.get(0),
                                map.get(dailyInfo2[0])!!.get(2),
                                mapTotalCost.get(dailyInfo2[0])!!,
                                false

                            )
                        )
                        currentDailyID = dailyInfo2[0].toInt()
                    }
                }
            }



            Log.v("Elad1", "Num of Days " + numOfDay)
            Log.v("Elad1", "dailyIds " + dailyIds)
            this.setDailyList(dailyList)

            var dialog = WeeklyDialogInfo(

                dailyList!!,
                numOfDay,
                dailyIds,
                totalCostDobule,
                pos
            )


            dialog.show(childFragmentManager, "WeeklyDialogInfo")


        }


    }
    }

