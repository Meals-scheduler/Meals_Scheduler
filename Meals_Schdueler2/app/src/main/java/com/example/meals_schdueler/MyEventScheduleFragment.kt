package com.example.meals_schdueler

import android.os.Bundle
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

class MyEventScheduleFragment : Fragment(), GetAndPost, NestedScrollView.OnScrollChangeListener {

    private var columnCount = 1
    private var recipeList: ArrayList<Recipe>? = null
    private var eventList: ArrayList<Event>? = null
    private var eventRecyclerViewAdapter: My_Event_RecylerViewAdapter? = null
    private lateinit var nestedScroll: NestedScrollView // list of ingredietns
    private var progressBar: ProgressBar? = null
    private var page = 0
    public lateinit var noEventTextView: TextView
    private var isScorlled = false


    private var quantities: String = ""
    private var recipeIds: String = ""
    private var totalcost = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeList = ArrayList()

        // dailyList = ArrayList()
        eventList = ArrayList()
        eventRecyclerViewAdapter = My_Event_RecylerViewAdapter(
            eventList!!,
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

        var instance: MyEventScheduleFragment? = null

        fun getInstance1(): MyEventScheduleFragment {
            return instance!!
        }


        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            MyEventScheduleFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)

                }
            }
    }

    fun getRecycler(): My_Event_RecylerViewAdapter {
        return eventRecyclerViewAdapter!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.my_event_schedule_list, null)
        val recyclerView = view.findViewById<View>(R.id.recycler_view) as RecyclerView
        nestedScroll = view!!.findViewById(R.id.scroll_view)
        progressBar = view!!.findViewById(R.id.progress_bar)
        nestedScroll.setOnScrollChangeListener(this)
        noEventTextView = view.findViewById(R.id.textViewNoEvent)


        instance = this
//
//        if (view is RecyclerView) {
//            with(view) {
//                layoutManager = when {
//                    columnCount <= 1 -> LinearLayoutManager(context)
//                    else -> GridLayoutManager(context, columnCount)
//                }
//                recyclerView.adapter = eventRecyclerViewAdapter
//
//            }
//        }
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = eventRecyclerViewAdapter
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

        var link = "https://elad1.000webhostapp.com/getEvent.php?ownerIDAndPage=" + string


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
        if (!str.equals("")) {
            if (!isScorlled)
                eventList!!.clear()

            if (noEventTextView.visibility == View.VISIBLE) {
                noEventTextView.visibility = View.INVISIBLE
            }

            quantities = ""
            recipeIds = ""
          //  recipeList!!.clear()


            // extracting dividing the data

            val eventinfo: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()

            var map: HashMap<String, ArrayList<String>> = HashMap()
            var mapTotalCost: HashMap<String, Double> = HashMap()

            var eventInfo2 = eventinfo[0].splitIgnoreEmpty("*")
            var currentEventID = eventInfo2[0].toInt()

            for (i in eventinfo.indices) {
                eventInfo2 = eventinfo[i].splitIgnoreEmpty("*")
                //means we switch to the next DailyID
                if (eventInfo2[0].toInt() != currentEventID) {
                    var totalLists: ArrayList<String> = ArrayList()
                    totalLists.add(quantities)
                    totalLists.add(recipeIds)

                    // gathering all quantities , numOfMeal and recipeIds under
                    // the key of that DailyID
                    map.put(currentEventID.toString(), totalLists)
                    mapTotalCost.put(currentEventID.toString(), totalcost)
                    //switching to the next DailyID
                    currentEventID = eventInfo2[0].toInt()

                    // clearing the variables for next DailyID
                    quantities = ""
                    recipeIds = ""
                }
                quantities += "" + eventInfo2[5] + " "
                recipeIds += "" + eventInfo2[4] + " "
                // saving the last total cost
                totalcost = eventInfo2[3].toDouble()
            }

            if (!quantities.equals("")) {
                var totalLists: ArrayList<String> = ArrayList()
                totalLists.add(quantities)
                totalLists.add(recipeIds)
                map.put(currentEventID.toString(), totalLists)
                mapTotalCost.put(currentEventID.toString(), totalcost)
            }

            // making Event objects by the extracted data
            currentEventID = -1
            for (i in eventinfo.indices) {
                var eventinfo2 = eventinfo[i].splitIgnoreEmpty("*")
                if (eventinfo2[0].toInt() != currentEventID) {
                    eventList!!.add(

                        Event(
                            eventinfo2[0].toInt(),
                            eventinfo2[1].toInt(),
                            eventinfo2[2],
                            map.get(eventinfo2[0])!!.get(0),
                            map.get(eventinfo2[0])!!.get(1),
                            mapTotalCost.get(eventinfo2[0])!!,
                            false

                        )
                    )
                    currentEventID = eventinfo2[0].toInt()
                }
            }



            eventRecyclerViewAdapter!!.setmValues(eventList!!)
            eventRecyclerViewAdapter!!.setRecipeList(recipeList!!)
            progressBar!!.visibility = View.INVISIBLE

            isScorlled = false
        }
        else{
            if (eventList!!.size == 0) {
                noEventTextView.visibility = View.VISIBLE
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