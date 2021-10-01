package com.example.meals_schdueler

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
    childFragmentManager: FragmentManager,
    progressbar: ProgressBar?,
    searchView: SearchView?,
    noResult: TextView?,
    context: Context
) : RecyclerView.Adapter<Weekly_Schedule_Choose_RecyclerViewAdapter.ViewHolder>(), GetAndPost,
    SearchView.OnQueryTextListener {

    private var mValues: ArrayList<WeeklySchedule> = weeklyValues
    private var dailyList: ArrayList<DailySchedule> = ArrayList()
    // private var mValuesDaily: TreeMap<String, DailySchedule> = dailyValues

    private var context = context

    //    private var recipe = recipes
    private var childFragmentManager = childFragmentManager
    private var weeklyId = weeklyId

    private var weeklyID = -1
    private var dailyIds = ""
    private var numOfDay = ""
    private var pos = -1
    private var totalCostDobule = 0.0


    private var page = 0
    private var isSearch = false
    private var recipeToSearch = ""
    private var isScorlled = false
    private var progressBar = progressbar
    private var searchView = searchView
    private var noResultsTextView = noResult
    private var info = false


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Weekly_Schedule_Choose_RecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.weekly_choose, parent, false)
        //  searchView!!.setOnQueryTextListener(this) // if i want to add the search in the future.

        return ViewHolder(view)
    }

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    fun startTask() {

        var s = AsynTaskNew(this, childFragmentManager,context)
        s.execute()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item: WeeklySchedule = mValues[position]!! // each item postion
        holder.mItem = item

        holder.numOfDaily.setText(position.toString())

        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)
        if (position == getItemCount() - 1 && !isSearch) {
            Log.v("Elad1", "AAAAAAAAAAAAa")
            page = page + 8
            isScorlled = true
            progressBar!!.visibility = View.VISIBLE
            startTask()
        }

        holder.weeklyInfo.setOnClickListener {

            weeklyID = mValues.get(position)!!.weeklyId
            dailyIds = mValues.get(position)!!.dailyIds
            numOfDay = mValues.get(position)!!.numOfDay
            pos = position + 1
            totalCostDobule = mValues.get(position).totalCost
            info = true

            var s = AsynTaskNew(this, childFragmentManager,context)
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
        val arr = Array(100, { i -> false })
        lateinit var mItem: WeeklySchedule


        override fun toString(): String {
            return super.toString()
        }


    }

    override fun getItemCount(): Int = weeklyValues.size

    override fun DoNetWorkOpreation(): String {
        var link = ""
        if (info) {
            var string = UserInterFace.userID.toString() + " " + weeklyID


            link =
                "https://elad1.000webhostapp.com/getDailyForWeekly.php?ownerIDAndWeekly=" + string

        } else {
            if (!isScorlled) {
                page = 0
            }
            Log.v("Elad1", "HHHHHHHHHHHHH")
            var string = UserInterFace.userID.toString() + " " + page

            link = "https://elad1.000webhostapp.com/getWeekly.php?ownerIDAndPage=" + string

        }
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

            if (info) {
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

                var daily = -1
                var dailyIdsArr = dailyIds.splitIgnoreEmpty(" ")
                //var dayid = -1
                for (i in dailyIdsArr) {
                    daily = -1
                    // if (dayid != i.toInt()) {

                    for (j in dailyInfo.indices) {
                        var dailyInfo2 = dailyInfo[j].splitIgnoreEmpty("*")
                        if (i.toInt() == dailyInfo2[0].toInt() && dailyList.size < dailyIdsArr.size && daily != dailyInfo2[0].toInt()) {
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
                            daily = dailyInfo2[0].toInt()
                        }
                    }
                    // }
                    // dayid = dailyInfo2[0].toInt()
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

                info = false
            } else if (!info) {
                if (!isScorlled)
                    mValues!!.clear()


                numOfDay = ""
                dailyIds = ""


                val weeklyInfo: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()


                // map to map each WeeklyID with a key as ID and contains all 2
                // array lists (e.g - numOfDay,dailyIds)
                var map: HashMap<String, ArrayList<String>> = HashMap()
                var mapTotalCost: HashMap<String, Double> = HashMap()

                var weeklyInfo2 = weeklyInfo[0].splitIgnoreEmpty("*")
                var currentWeeklyID = weeklyInfo2[0].toInt()

                for (i in weeklyInfo.indices) {

                    weeklyInfo2 = weeklyInfo[i].splitIgnoreEmpty("*")

                    //means we switch to the next WeeklyID
                    if (weeklyInfo2[0].toInt() != currentWeeklyID) {

                        // to keep each Weekly its dailys ids and its num of day.(days in a week)
                        var totalLists: ArrayList<String> = ArrayList()
                        totalLists.add(numOfDay)
                        totalLists.add(dailyIds)
                        // saving this weekly daily ids and num of days
                        map.put(currentWeeklyID.toString(), totalLists)
                        // saving this weekly total cost
                        mapTotalCost.put(currentWeeklyID.toString(), totalCostDobule)

                        //switching to the next WeeklyID
                        currentWeeklyID = weeklyInfo2[0].toInt()

                        // clearing the variables for next WeeeklyID
                        numOfDay = ""
                        dailyIds = ""

                    }

                    numOfDay += "" + weeklyInfo2[3] + " "
                    dailyIds += "" + weeklyInfo2[4] + " "
                    // saving the last total cost
                    totalCostDobule = weeklyInfo2[2].toDouble()


                }

                // not to skip on the last Weeekly

                if (!numOfDay.equals("")) {
                    var totalLists: ArrayList<String> = ArrayList()
                    totalLists.add(numOfDay)
                    totalLists.add(dailyIds)
                    map.put(currentWeeklyID.toString(), totalLists)
                    mapTotalCost.put(currentWeeklyID.toString(), totalCostDobule)
                }

                // making WeeklyScheudle objects


                currentWeeklyID = -1
                for (i in weeklyInfo.indices) {
                    var dailyInfo2 = weeklyInfo[i].splitIgnoreEmpty("*")
                    if (dailyInfo2[0].toInt() != currentWeeklyID) {
                        mValues!!.add(
                            WeeklySchedule(
                                dailyInfo2[0].toInt(),
                                dailyInfo2[1].toInt(),
                                map.get(dailyInfo2[0])!!.get(0),
                                map.get(dailyInfo2[0])!!.get(1),
                                mapTotalCost.get(dailyInfo2[0])!!,
                                false

                            )
                        )
                        currentWeeklyID = dailyInfo2[0].toInt()
                    }


                }



                this!!.setmValues(mValues!!)

                progressBar!!.visibility = View.INVISIBLE
                // UserPropertiesSingelton.getInstance()!!.setUserWeekly(sorted)
                isScorlled = false
            }
        } else {
            progressBar!!.visibility = View.INVISIBLE
        }


    }


    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        if (p0 != "") {
//            noResultsTextView!!.visibility = View.INVISIBLE
//            isSearch = true
//            recipeToSearch = p0!!
//            startTask()
        } else {
//            isSearch = false
//            mValues!!.clear()
//            noResultsTextView!!.visibility = View.INVISIBLE
//            startTask()
        }
        return true
    }
}

