package com.example.meals_schdueler

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
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

class Weekly_Schedule_Choose_Dialog(
    weeklyList: ArrayList<WeeklySchedule>,
    list: Recipe_Ingredients_List,
) : DialogFragment(), NestedScrollView.OnScrollChangeListener, SearchView.OnQueryTextListener,
    GetAndPost {


    lateinit var exit: ImageView

    // private var recipesList: ArrayList<Recipe>? = null
    private lateinit var btnDone: Button

    //private var dailyList = dailyList
    // private var recipeList = recipeList
    private var weeklyList = weeklyList
    private var l: Recipe_Ingredients_List = list

    private lateinit var nestedScroll: NestedScrollView // list of ingredietns
    private var progressBar: ProgressBar? = null
    private lateinit var searchView: SearchView
    private lateinit var noResultsTextView: TextView

    private var page = 0
    private var isSearch = false
    private var weeklyToSearch = ""
    private var isScorlled = false

    private var numOfDay: String = ""
    private var dailyIds: String = ""
    private var totalcost = 0.011

    private var weeklyChoosenRecyclerViewAdapter: Weekly_Schedule_Choose_RecyclerViewAdapter? =
        null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        weeklyChoosenRecyclerViewAdapter = Weekly_Schedule_Choose_RecyclerViewAdapter(
            weeklyList!!, l.list, childFragmentManager
        )

        var view: View =
            inflater.inflate(R.layout.weekly_choose_list, container, false)
        btnDone = view.findViewById(R.id.doneBtn)
        val recyclerView = view.findViewById<View>(R.id.list) as RecyclerView

        weeklyChoosenRecyclerViewAdapter!!.setmValues(weeklyList!!)

        exit = view.findViewById(R.id.imageViewX)
        nestedScroll = view.findViewById(R.id.scroll_view)
        progressBar = view.findViewById(R.id.progress_bar)
        nestedScroll.setOnScrollChangeListener(this)
        searchView = view.findViewById(R.id.search_bar)
        searchView.setOnQueryTextListener(this)
        noResultsTextView = view.findViewById(R.id.tv_emptyTextView)



        btnDone.setOnClickListener({

            dismiss()
        })

        exit.setOnClickListener({
            dismiss()
        })


        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = weeklyChoosenRecyclerViewAdapter
        startTask()
        return view



    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        val parentFragment: Fragment? = parentFragment
        if (parentFragment is DialogInterface.OnDismissListener) {
            (parentFragment as DialogInterface.OnDismissListener?)!!.onDismiss(dialog)
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
            if (!isSearch) {
                page = page + 4
                progressBar!!.visibility = View.VISIBLE
                isScorlled = true
                startTask()
            }
        }
    }

    fun startTask() {

        var s = AsynTaskNew(this, childFragmentManager)
        s.execute()
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        if (p0 != "") {
            noResultsTextView.visibility = View.INVISIBLE
            isSearch = true
            weeklyToSearch = p0!!
            startTask()
        } else {
            isSearch = false
            weeklyList!!.clear()
            noResultsTextView.visibility = View.INVISIBLE
            startTask()
        }
        return true
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



            weeklyChoosenRecyclerViewAdapter!!.setmValues(weeklyList!!)

            progressBar!!.visibility = View.INVISIBLE
            // UserPropertiesSingelton.getInstance()!!.setUserWeekly(sorted)
            isScorlled = false

        }
        isScorlled = false
    }


}