package com.example.meals_schdueler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MyYearlyScheudleFragment : Fragment(), GetAndPost, NestedScrollView.OnScrollChangeListener {

    private var columnCount = 1
    private var yearlyList: ArrayList<YearlySchedule>? = null
    private var monthlyList: ArrayList<MonthlySchedule>? = null
    private var yearlyRecyclerViewAdapter: My_Yearly_RecylerViewAdapter? = null
    private lateinit var nestedScroll: NestedScrollView // list of ingredietns
    private var progressBar: ProgressBar? = null
    private var page = 0
    private var isScorlled = false


    private var numOfMonth: String = ""
    private var monthlyIds: String = ""
    private var totalcost = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        yearlyList = ArrayList()
        monthlyList = ArrayList()
        yearlyRecyclerViewAdapter = My_Yearly_RecylerViewAdapter(
            yearlyList!!,
            childFragmentManager,
            this.context,
            requireActivity()
        )

        // to know how many objects from the wanted type will be in a line
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

    }


    companion object {

        var instance: MyYearlyScheudleFragment? = null

        fun getInstance1(): MyYearlyScheudleFragment {
            return instance!!
        }


        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            MyYearlyScheudleFragment().apply {
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
        val view = inflater.inflate(R.layout.my_yearly_schedule_list, null)
        val recyclerView = view.findViewById<View>(R.id.recycler_view) as RecyclerView
        nestedScroll = view!!.findViewById(R.id.scroll_view)
        progressBar = view!!.findViewById(R.id.progress_bar)
        nestedScroll.setOnScrollChangeListener(this)


        instance = this

//        if (x is RecyclerView) {
//            with(x) {
//                layoutManager = when {
//                    columnCount <= 1 -> LinearLayoutManager(context)
//                    else -> GridLayoutManager(context, columnCount)
//                }
//                recyclerView.adapter = yearlyRecyclerViewAdapter
//
//            }
//        }
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = yearlyRecyclerViewAdapter
        startTask()

        return view
    }

    fun startTask() {
        var s = AsynTaskNew(this, childFragmentManager)
        s.execute()
    }


    override fun DoNetWorkOpreation(): String {

        if (!isScorlled) {
            page = 0
        }
        var string = UserInterFace.userID.toString() + " " + page


        var link = "https://elad1.000webhostapp.com/getYearly.php?ownerIDAndPage=" + string


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
                yearlyList!!.clear()

            numOfMonth = ""
            monthlyIds = ""
            // recipeList!!.clear()


            val yearlyInfo: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()


            // map to map each MonthlyID with a key as ID and contains all 2
            // array lists (e.g - numOfDay,dailyIds)
            var map: HashMap<String, ArrayList<String>> = HashMap()
            var mapTotalCost: HashMap<String, Double> = HashMap()


            var yearlyInfo2 = yearlyInfo[0].splitIgnoreEmpty("*")
            var currentYearlyID = yearlyInfo2[0].toInt()

            for (i in yearlyInfo.indices) {

                yearlyInfo2 = yearlyInfo[i].splitIgnoreEmpty("*")

                //means we switch to the next WeeklyID
                if (yearlyInfo2[0].toInt() != currentYearlyID) {

                    // to keep each Weekly its dailys ids and its num of day.(days in a week)
                    var totalLists: ArrayList<String> = ArrayList()
                    totalLists.add(numOfMonth)
                    totalLists.add(monthlyIds)
                    // saving this weekly daily ids and num of days
                    map.put(currentYearlyID.toString(), totalLists)
                    // saving this weekly total cost
                    mapTotalCost.put(currentYearlyID.toString(), totalcost)

                    //switching to the next WeeklyID
                    currentYearlyID = yearlyInfo2[0].toInt()

                    // clearing the variables for next WeeeklyID
                    numOfMonth = ""
                    monthlyIds = ""

                }

                numOfMonth += "" + yearlyInfo2[3] + " "
                monthlyIds += "" + yearlyInfo2[4] + " "
                // saving the last total cost
                totalcost = yearlyInfo2[2].toDouble()


            }

            // not to skip on the last Weeekly

            if (!numOfMonth.equals("")) {
                var totalLists: ArrayList<String> = ArrayList()
                totalLists.add(numOfMonth)
                totalLists.add(monthlyIds)
                map.put(currentYearlyID.toString(), totalLists)
                mapTotalCost.put(currentYearlyID.toString(), totalcost)
            }

            // making MonthlyScheudle objects

            currentYearlyID = -1
            for (i in yearlyInfo.indices) {
                var yearlyInfo2 = yearlyInfo[i].splitIgnoreEmpty("*")
                if (yearlyInfo2[0].toInt() != currentYearlyID) {

                    yearlyList!!.add(
                        YearlySchedule(
                            yearlyInfo2[0].toInt(),
                            yearlyInfo2[1].toInt(),
                            map.get(yearlyInfo2[0])!!.get(0),
                            map.get(yearlyInfo2[0])!!.get(1),
                            mapTotalCost.get(yearlyInfo2[0])!!,
                            false

                        )
                    )
                    currentYearlyID = yearlyInfo2[0].toInt()

                }
            }



            yearlyRecyclerViewAdapter!!.setYearlyValues(yearlyList!!)!!
            yearlyRecyclerViewAdapter!!.setMonthlyValues(monthlyList!!)
            progressBar!!.visibility = View.INVISIBLE
            isScorlled = false

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