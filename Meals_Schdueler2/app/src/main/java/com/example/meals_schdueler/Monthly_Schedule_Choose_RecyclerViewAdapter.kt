package com.example.meals_schdueler

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

class Monthly_Schedule_Choose_RecyclerViewAdapter(

    var monthlyValues: ArrayList<MonthlySchedule>,
    monthlyId: ArrayList<Int>?,
    childFragmentManager: FragmentManager,
    progressbar: ProgressBar?,
    searchView: SearchView?,
    noResult: TextView?

) : RecyclerView.Adapter<Monthly_Schedule_Choose_RecyclerViewAdapter.ViewHolder>(), GetAndPost,
    SearchView.OnQueryTextListener {


    private var mValues: ArrayList<MonthlySchedule> = monthlyValues
    private var weeklyList: ArrayList<WeeklySchedule> = ArrayList()
    // private var mValuesDaily: TreeMap<String, DailySchedule> = dailyValues


    //    private var recipe = recipes
    private var childFragmentManager = childFragmentManager
    private var monthlyId = monthlyId

    private var monthlyID = -1
    private var weeklyIds = ""
    private var numOfWeek = ""
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
    ): Monthly_Schedule_Choose_RecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.monthly_choose, parent, false)
        //  searchView!!.setOnQueryTextListener(this) // if i want to add the search in the future.
        return ViewHolder(view)
    }

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    fun startTask() {

        var s = AsynTaskNew(this, childFragmentManager)
        s.execute()
    }


    override fun onBindViewHolder(
        holder: Monthly_Schedule_Choose_RecyclerViewAdapter.ViewHolder,
        position: Int
    ) {
        var item: MonthlySchedule = mValues[position]!! // each item postion
        holder.mItem = item
        holder.numOfMonthly.setText(position.toString())
        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)


        holder.monthlyInfo.setOnClickListener {

            monthlyID = mValues.get(position)!!.monthlyId
            weeklyIds = mValues.get(position)!!.weeklyIds
            numOfWeek = mValues.get(position)!!.numOfWeek
            pos = position + 1
            totalCostDobule = mValues.get(position).totalCost
            info = true

            var s = AsynTaskNew(this, childFragmentManager)
            s.execute()

        }

        if (position == getItemCount() - 1 && !isSearch) {
            Log.v("Elad1", "AAAAAAAAAAAAa")
            page = page + 8
            isScorlled = true
            progressBar!!.visibility = View.VISIBLE
            startTask()
        }


        holder.choose.setChecked(holder.arr[position]);
        holder.choose.setOnClickListener() {

//
            if (holder.choose.isChecked == true && (monthlyId!!.isEmpty())) {
                mValues.add(item)
                monthlyId!!.add(position)
                holder.arr[position] = true

            } else if (holder.choose.isChecked == false) {
                if (monthlyId!!.contains(position)) {
                    holder.arr[position] = false
                    monthlyId!!.remove(position)

                }

            }


        }


    }


    fun setmValues(mValues: ArrayList<MonthlySchedule>) {
        this.mValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    fun setWeeklyList(weeklyList: ArrayList<WeeklySchedule>) {

        this.weeklyList = weeklyList

        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val idView: TextView = view.findViewById(R.id.item_number)
        //val contentView: TextView = view.findViewById(R.id.content)

        var monthlyInfo: Button = view.findViewById(R.id.buttonInfo)
        var cart: ImageView = view.findViewById(R.id.imageViewCart)
        var numOfMonthly: TextView = view.findViewById(R.id.numOfMonthlyTextView)
        var choose: CheckBox = view.findViewById(R.id.WeeklyCheckBox)
        val arr = Array(100, { i -> false })
        lateinit var mItem: MonthlySchedule


        override fun toString(): String {
            return super.toString()
        }


    }

    override fun getItemCount(): Int = monthlyValues.size

    override fun DoNetWorkOpreation(): String {
        var link = ""
        if (info) {
            var string = UserInterFace.userID.toString() + " " + monthlyID


            link =
                "https://elad1.000webhostapp.com/getWeeklyForMonthly.php?ownerIDAndMonthly=" + string

        } else {
            if (!isScorlled) {
                page = 0
            }
            Log.v("Elad1", "HHHHHHHHHHHHH")
            var string = UserInterFace.userID.toString() + " " + page

            link = "https://elad1.000webhostapp.com/getMonthly.php?ownerIDAndPage=" + string

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
        weeklyList.clear()
        if (!str.equals("")) {

            if (info) {
                var totalcost = 0.011
                var numOfDay = ""
                var dailyIds = ""
                // recipeList!!.clear()

                // weeklyList!!.clear()

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
                        mapTotalCost.put(currentWeeklyID.toString(), totalcost)

                        //switching to the next WeeklyID
                        currentWeeklyID = weeklyInfo2[0].toInt()

                        // clearing the variables for next WeeeklyID
                        numOfDay = ""
                        dailyIds = ""

                    }

                    numOfDay += "" + weeklyInfo2[3] + " "
                    dailyIds += "" + weeklyInfo2[4] + " "
                    // saving the last total cost
                    totalcost = weeklyInfo2[2].toDouble()


                }

                // not to skip on the last Weeekly

                if (!numOfDay.equals("")) {
                    var totalLists: ArrayList<String> = ArrayList()
                    totalLists.add(numOfDay)
                    totalLists.add(dailyIds)
                    map.put(currentWeeklyID.toString(), totalLists)
                    mapTotalCost.put(currentWeeklyID.toString(), totalcost)
                }

                // making WeeklyScheudle objects
                // making WeeklyScheudle objects
                // making WeeklyScheudle objects
                var weeklyIdsArr = weeklyIds.splitIgnoreEmpty(" ")
                //  currentWeeklyID = -1
                var weekly = -1
                for (i in weeklyIdsArr) {
                    weekly= -1
                    for (j in weeklyInfo.indices) {
                        var weeklyInfo2 = weeklyInfo[j].splitIgnoreEmpty("*")
                        if (i.toInt() == weeklyInfo2[0].toInt() && weeklyList.size < weeklyIdsArr.size && weekly != weeklyInfo2[0].toInt()) {
                            weeklyList!!.add(
                                WeeklySchedule(
                                    weeklyInfo2[0].toInt(),
                                    weeklyInfo2[1].toInt(),
                                    map.get(weeklyInfo2[0])!!.get(0),
                                    map.get(weeklyInfo2[0])!!.get(1),
                                    mapTotalCost.get(weeklyInfo2[0])!!,
                                    false

                                )
                            )
                            weekly = weeklyInfo2[0].toInt()
                        }


                    }

                }



                var dialog = MonthlyDialogInfo(

                    weeklyList,
                    numOfWeek,
                    weeklyIds,
                    totalCostDobule,
                    pos
                )


                dialog.show(childFragmentManager, "WeeklyDialogInfo")

                info = false
            } else if (!info) {

                if (!isScorlled)
                    mValues!!.clear()

                numOfWeek = ""
                weeklyIds = ""
                // recipeList!!.clear()
                // monthlyList!!.clear()


                val monthlyInfo: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()


                // map to map each MonthlyID with a key as ID and contains all 2
                // array lists (e.g - numOfDay,dailyIds)
                var map: HashMap<String, ArrayList<String>> = HashMap()
                var mapTotalCost: HashMap<String, Double> = HashMap()


                var monthlyInfo2 = monthlyInfo[0].splitIgnoreEmpty("*")
                var currentMonthlyID = monthlyInfo2[0].toInt()


                for (i in monthlyInfo.indices) {

                    monthlyInfo2 = monthlyInfo[i].splitIgnoreEmpty("*")

                    //means we switch to the next WeeklyID
                    if (monthlyInfo2[0].toInt() != currentMonthlyID) {

                        // to keep each Weekly its dailys ids and its num of day.(days in a week)
                        var totalLists: ArrayList<String> = ArrayList()
                        totalLists.add(numOfWeek)
                        totalLists.add(weeklyIds)
                        // saving this weekly daily ids and num of days
                        map.put(currentMonthlyID.toString(), totalLists)
                        // saving this weekly total cost
                        mapTotalCost.put(currentMonthlyID.toString(), totalCostDobule)

                        //switching to the next WeeklyID
                        currentMonthlyID = monthlyInfo2[0].toInt()

                        // clearing the variables for next WeeeklyID
                        numOfWeek = ""
                        weeklyIds = ""

                    }

                    numOfWeek += "" + monthlyInfo2[3] + " "
                    weeklyIds += "" + monthlyInfo2[4] + " "
                    // saving the last total cost
                    totalCostDobule = monthlyInfo2[2].toDouble()


                }

                // not to skip on the last Weeekly

                if (!numOfWeek.equals("")) {
                    var totalLists: ArrayList<String> = ArrayList()
                    totalLists.add(numOfWeek)
                    totalLists.add(weeklyIds)
                    map.put(currentMonthlyID.toString(), totalLists)
                    mapTotalCost.put(currentMonthlyID.toString(), totalCostDobule)
                }

                // making MonthlyScheudle objects

                currentMonthlyID = -1
                for (i in monthlyInfo.indices) {
                    var monthlyInfo2 = monthlyInfo[i].splitIgnoreEmpty("*")
                    if (monthlyInfo2[0].toInt() != currentMonthlyID) {
                        mValues!!.add(
                            MonthlySchedule(
                                monthlyInfo2[0].toInt(),
                                monthlyInfo2[1].toInt(),
                                map.get(monthlyInfo2[0])!!.get(0),
                                map.get(monthlyInfo2[0])!!.get(1),
                                mapTotalCost.get(monthlyInfo2[0])!!,
                                false

                            )
                        )

                        currentMonthlyID = monthlyInfo2[0].toInt()
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



