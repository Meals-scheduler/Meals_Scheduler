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
import kotlin.collections.List
import kotlin.collections.filter
import kotlin.collections.indices
import kotlin.collections.iterator
import kotlin.collections.toTypedArray


class MyDailyScheduleFragment : Fragment(), GetAndPost, NestedScrollView.OnScrollChangeListener {


    private var columnCount = 1

    //private var dailyList: ArrayList<DailySchedule>? = null
    private var dailyList: ArrayList<DailySchedule>? = null
    private var recipeList: ArrayList<Recipe>? = null
    private var dailyRecyclerViewAdapter: My_Daily_RecylerViewAdapter? = null
    private lateinit var nestedScroll: NestedScrollView // list of ingredietns
    private var progressBar: ProgressBar? = null
    public lateinit var noDailyTextView: TextView
    private var page = 0
    private var isScorlled = false


    private var quantities: String = ""
    private var numOfMeal: String = ""
    private var recipeIds: String = ""
    private var totalcost = 0.011

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeList = ArrayList()
        // dailyList = ArrayList()
        dailyList = ArrayList()
        dailyRecyclerViewAdapter = My_Daily_RecylerViewAdapter(
            dailyList!!,
            childFragmentManager,
            this.context,
            requireActivity()
        )

        // to know how many objects from the wanted type will be in a line
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

    }

    fun getRecycler(): My_Daily_RecylerViewAdapter {
        return dailyRecyclerViewAdapter!!
    }


    companion object {

        var instance: MyDailyScheduleFragment? = null

        fun getInstance1(): MyDailyScheduleFragment {
            return instance!!
        }


        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            MyDailyScheduleFragment().apply {
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
        val view = inflater.inflate(R.layout.my_daily_schedule_list, null)
        val recyclerView = view.findViewById<View>(R.id.recycler_view) as RecyclerView
        nestedScroll = view!!.findViewById(R.id.scroll_view)
        progressBar = view!!.findViewById(R.id.progress_bar)
        nestedScroll.setOnScrollChangeListener(this)
        noDailyTextView = view.findViewById(R.id.textViewNoDaily)

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
        recyclerView.adapter = dailyRecyclerViewAdapter
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

        var link = "https://elad1.000webhostapp.com/getDaily.php?ownerIDAndPage=" + string


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
        Log.v("Elad1", "Page is " + page)
        progressBar!!.visibility = View.INVISIBLE
        if (!str.equals("")) {
            if (noDailyTextView.visibility == View.VISIBLE) {
                noDailyTextView.visibility = View.INVISIBLE
            }


            if (!isScorlled)
                dailyList!!.clear()

            quantities = ""
            numOfMeal = ""
            recipeIds = ""


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
            for (i in dailyInfo.indices) {
                var dailyInfo2 = dailyInfo[i].splitIgnoreEmpty("*")
                if (dailyInfo2[0].toInt() != currentDailyID) {
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

            // TreeMap to store values of HashMap

            // TreeMap to store values of HashMap
            // sorted = TreeMap<String, DailySchedule>()

            // Copy all data from hashMap into TreeMap

            // Copy all data from hashMap into TreeMap


            // now getting the recipes for each daily


            currentDailyID = -1


            //UserPropertiesSingelton.getInstance()!!.setUserDaily(sorted)
            dailyRecyclerViewAdapter!!.setmValues(dailyList!!)
            dailyRecyclerViewAdapter!!.setRecipeList(recipeList!!)
            progressBar!!.visibility = View.INVISIBLE


//            dailyList!!.clear()
//            recipeList!!.clear()


            //save it also in singleton
            //  UserPropertiesSingelton.getInstance()!!.setUserRecipess(recipeList)
            //dailyRecyclerViewAdapter!!.setmValues(recipeList!!)
            isScorlled = false
        }

        else{
            if (dailyList!!.size == 0) {
                noDailyTextView.visibility = View.VISIBLE
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