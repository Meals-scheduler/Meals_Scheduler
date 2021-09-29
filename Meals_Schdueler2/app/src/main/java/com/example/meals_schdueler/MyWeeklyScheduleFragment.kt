package com.example.meals_schdueler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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

class MyWeeklyScheduleFragment : Fragment(), GetAndPost,NestedScrollView.OnScrollChangeListener {

    private var columnCount = 1

    //private var weeklyList: ArrayList<WeeklySchedule>? = null
    private var weeklyList: ArrayList<WeeklySchedule>? = null
    private var dailyList: ArrayList<DailySchedule>? = null
    private var weeklyRecyclerViewAdapter: My_Weekly_RecylerViewAdapter? = null
    private lateinit var nestedScroll: NestedScrollView // list of ingredietns
    private var progressBar: ProgressBar? = null
    private var page = 0
    private var isScorlled = false
    public lateinit var noWeeklyTextView: TextView
    var numOfDay: String = ""
    var dailyIds: String = ""
    private var totalcost = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // recipeList = ArrayList()
        //  weeklyList = ArrayList()

        weeklyList = ArrayList()
        dailyList = ArrayList()
        weeklyRecyclerViewAdapter = My_Weekly_RecylerViewAdapter(
            weeklyList!!,
            childFragmentManager,
            this.context,
            requireActivity()
        )

        // to know how many objects from the wanted type will be in a line
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

    }

    fun getRecycler(): My_Weekly_RecylerViewAdapter {
        return weeklyRecyclerViewAdapter!!
    }


    companion object {

        var instance: MyWeeklyScheduleFragment? = null

        fun getInstance1(): MyWeeklyScheduleFragment {
            return instance!!
        }


        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            MyWeeklyScheduleFragment().apply {
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
        val view = inflater.inflate(R.layout.my_weekly_schedule_list, null)
        val recyclerView = view.findViewById<View>(R.id.recycler_view) as RecyclerView
        nestedScroll = view!!.findViewById(R.id.scroll_view)
        progressBar = view!!.findViewById(R.id.progress_bar)
        nestedScroll.setOnScrollChangeListener(this)
        noWeeklyTextView = view.findViewById(R.id.textViewNoWeekly)


        instance = this

//        if (x is RecyclerView) {
//            with(x) {
//                layoutManager = when {
//                    columnCount <= 1 -> LinearLayoutManager(context)
//                    else -> GridLayoutManager(context, columnCount)
//                }
//
//
//            }
//        }
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = weeklyRecyclerViewAdapter
        startTask()

        return view
    }

    fun startTask() {
        var s = AsynTaskNew(this, childFragmentManager)
        s.execute()
    }


    override fun DoNetWorkOpreation(): String {
        if(!isScorlled){
            page =0
        }
        var string = UserInterFace.userID.toString() + " " + page


        var link = "https://elad1.000webhostapp.com/getWeekly.php?ownerIDAndPage=" + string


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


        // Log.v("Elad1", "Id came is" + sb.toString())
        return sb.toString()
    }


    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    override fun getData(str: String) {
        progressBar!!.visibility = View.INVISIBLE
        if (!str.equals("")) {
            if (!isScorlled)
                weeklyList!!.clear()

            if (noWeeklyTextView.visibility == View.VISIBLE) {
                noWeeklyTextView.visibility = View.INVISIBLE
            }


            numOfDay = ""
            dailyIds = ""
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





            currentWeeklyID = -1
            for (i in weeklyInfo.indices) {

                var dailyInfo2 = weeklyInfo[i].splitIgnoreEmpty("*")
                if (dailyInfo2[0].toInt() != currentWeeklyID) {
                    weeklyList!!.add(
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



            weeklyRecyclerViewAdapter!!.setWeeklyValues(weeklyList!!)
            weeklyRecyclerViewAdapter!!.setDailyValues(dailyList!!)
            progressBar!!.visibility = View.INVISIBLE
           // UserPropertiesSingelton.getInstance()!!.setUserWeekly(sorted)
            isScorlled = false

        }
        else {
            if (weeklyList!!.size == 0) {
                noWeeklyTextView.visibility = View.VISIBLE
            }
        }
        isScorlled = false
    }

    override fun onScrollChange(
        v: NestedScrollView?,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        if (scrollY == v!!.getChildAt(0).measuredHeight - v.measuredHeight) {

            page = page + 8
            progressBar!!.visibility = View.VISIBLE
            isScorlled = true
            startTask()

        }
    }
}