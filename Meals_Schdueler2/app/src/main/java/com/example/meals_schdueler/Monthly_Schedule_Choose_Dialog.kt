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
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class Monthly_Schedule_Choose_Dialog(
    monthlyList: ArrayList<MonthlySchedule>,
    list: Recipe_Ingredients_List,
) : DialogFragment(), NestedScrollView.OnScrollChangeListener, SearchView.OnQueryTextListener,
    GetAndPost {
    lateinit var exit: ImageView


    private lateinit var btnDone: Button

    private var monthlyList = monthlyList
    private var l: Recipe_Ingredients_List = list

    private lateinit var nestedScroll: NestedScrollView // list of ingredietns
    private var progressBar: ProgressBar? = null
    private lateinit var searchView: SearchView
    private lateinit var noResultsTextView: TextView

    private var page = 0
    private var isSearch = false
    private var monthlyToSearch = ""
    private var isScorlled = false

    private var numOfWeek: String = ""
    private var weeklyIds: String = ""
    private var totalcost = 0.011

    private var monthlyChoosenRecyclerViewAdapter: Monthly_Schedule_Choose_RecyclerViewAdapter? =
        null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        monthlyChoosenRecyclerViewAdapter = Monthly_Schedule_Choose_RecyclerViewAdapter(
            monthlyList!!, l.list, childFragmentManager
        )

        var view: View =
            inflater.inflate(R.layout.monthly_choose_list, container, false)
        btnDone = view.findViewById(R.id.doneBtn)
        val recyclerView = view.findViewById<View>(R.id.list) as RecyclerView
        monthlyChoosenRecyclerViewAdapter!!.setmValues(monthlyList!!)
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
        recyclerView.adapter = monthlyChoosenRecyclerViewAdapter
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
            monthlyToSearch = p0!!
            startTask()
        } else {
            isSearch = false
            monthlyList!!.clear()
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
                monthlyList!!.clear()

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
                if (monthlyInfo2[0].toInt() != currentMonthlyID ) {

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



            monthlyChoosenRecyclerViewAdapter!!.setmValues(monthlyList!!)!!
            progressBar!!.visibility = View.INVISIBLE
            isScorlled = false

        }
        isScorlled = false

    }


}