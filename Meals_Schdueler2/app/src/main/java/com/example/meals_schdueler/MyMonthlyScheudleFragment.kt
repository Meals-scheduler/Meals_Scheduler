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

class MyMonthlyScheudleFragment : Fragment(), GetAndPost, NestedScrollView.OnScrollChangeListener {

    private var columnCount = 1
    private var monthlyList: ArrayList<MonthlySchedule>? = null
    private var weeklyList: ArrayList<WeeklySchedule>? = null
    private var monthlyRecyclerViewAdapter: My_Monthly_RecylerViewAdapter? = null
    private lateinit var nestedScroll: NestedScrollView // list of ingredietns
    private var progressBar: ProgressBar? = null
    public lateinit var noMonthlyTextView: TextView
    private var page = 0
    private var isScorlled = false


    private var numOfWeek: String = ""
    private var weeklyIds: String = ""
    private var totalcost = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        monthlyList = ArrayList()
        weeklyList = ArrayList()
        monthlyRecyclerViewAdapter = My_Monthly_RecylerViewAdapter(
            monthlyList!!,
            childFragmentManager,
            this.context,
            requireActivity()
        )

        // to know how many objects from the wanted type will be in a line
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

    }

    fun getRecycler(): My_Monthly_RecylerViewAdapter {
        return monthlyRecyclerViewAdapter!!
    }


    companion object {

        var instance: MyMonthlyScheudleFragment? = null

        fun getInstance1(): MyMonthlyScheudleFragment {
            return instance!!
        }


        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            MyMonthlyScheudleFragment().apply {
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
        val view = inflater.inflate(R.layout.my_monthly_schedule_list, null)
        val recyclerView = view.findViewById<View>(R.id.recycler_view) as RecyclerView
        nestedScroll = view!!.findViewById(R.id.scroll_view)
        progressBar = view!!.findViewById(R.id.progress_bar)
        nestedScroll.setOnScrollChangeListener(this)

        noMonthlyTextView = view.findViewById(R.id.textViewNoMonthly)

        instance = this

//        if (x is RecyclerView) {
//            with(x) {
//                layoutManager = when {
//                    columnCount <= 1 -> LinearLayoutManager(context)
//                    else -> GridLayoutManager(context, columnCount)
//                }
//                recyclerView.adapter = monthlyRecyclerViewAdapter
//
//            }
//        }

        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = monthlyRecyclerViewAdapter
        startTask()

        return view
    }

    fun startTask() {
        var s = AsynTaskNew(this, childFragmentManager,requireContext())
        s.execute()
    }


    override fun DoNetWorkOpreation(): String {

        if (!isScorlled) {
            page = 0
        }
        var string = UserInterFace.userID.toString() + " " + page


        var link = "https://elad1.000webhostapp.com/getMonthly.php?ownerIDAndPage=" + string


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



        return sb.toString()
    }


    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    override fun getData(str: String) {
        progressBar!!.visibility = View.INVISIBLE
        try {
            if (!str.equals("")) {
                if (!isScorlled)
                    monthlyList!!.clear()

                if (noMonthlyTextView.visibility == View.VISIBLE) {
                    noMonthlyTextView.visibility = View.INVISIBLE
                }


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
                        mapTotalCost.put(currentMonthlyID.toString(), totalcost)

                        //switching to the next WeeklyID
                        currentMonthlyID = monthlyInfo2[0].toInt()

                        // clearing the variables for next WeeeklyID
                        numOfWeek = ""
                        weeklyIds = ""

                    }

                    numOfWeek += "" + monthlyInfo2[3] + " "
                    weeklyIds += "" + monthlyInfo2[4] + " "
                    // saving the last total cost
                    totalcost = monthlyInfo2[2].toDouble()


                }

                // not to skip on the last Weeekly

                if (!numOfWeek.equals("")) {
                    var totalLists: ArrayList<String> = ArrayList()
                    totalLists.add(numOfWeek)
                    totalLists.add(weeklyIds)
                    map.put(currentMonthlyID.toString(), totalLists)
                    mapTotalCost.put(currentMonthlyID.toString(), totalcost)
                }

                // making MonthlyScheudle objects

                currentMonthlyID = -1
                for (i in monthlyInfo.indices) {
                    var monthlyInfo2 = monthlyInfo[i].splitIgnoreEmpty("*")
                    if (monthlyInfo2[0].toInt() != currentMonthlyID) {
                        monthlyList!!.add(
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



                monthlyRecyclerViewAdapter!!.setMonthlyValues(monthlyList!!)!!
                monthlyRecyclerViewAdapter!!.setWeeklyValues(weeklyList!!)
                progressBar!!.visibility = View.INVISIBLE
                isScorlled = false

            } else {
                if (monthlyList!!.size == 0) {
                    noMonthlyTextView.visibility = View.VISIBLE
                }
            }
            isScorlled = false
        }
        catch (e: Exception) {

        }

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
