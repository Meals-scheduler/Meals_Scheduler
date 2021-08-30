package com.example.meals_schdueler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class MyEventScheduleFragment : Fragment(), GetAndPost {

    private var columnCount = 1
    private var recipeList: ArrayList<Recipe>? = null
    private var eventList: ArrayList<Event>? = null
    private var eventRecyclerViewAdapter: My_Event_RecylerViewAdapter? = null

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
            this.context
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.my_event_schedule_list, null)
        val recyclerView = view.findViewById<View>(R.id.list) as RecyclerView


        instance = this

        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                recyclerView.adapter = eventRecyclerViewAdapter

            }
        }

        startTask()

        return view
    }

    fun startTask() {
        var s = AsynTaskNew(this, childFragmentManager)
        s.execute()
    }


    override fun DoNetWorkOpreation(): String {

        var link = "https://elad1.000webhostapp.com/getEvent.php?ownerID=" + UserInterFace.userID;


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
        if (!str.equals("")) {
            quantities = ""
            recipeIds = ""
            recipeList!!.clear()
            eventList!!.clear()

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
                quantities += "" + eventInfo2[6] + " "
                recipeIds += "" + eventInfo2[5] + " "
                // saving the last total cost
                totalcost = eventInfo2[4].toDouble()
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
                            eventinfo2[3],
                            map.get(eventinfo2[0])!!.get(1),
                            mapTotalCost.get(eventinfo2[0])!!,
                            false

                        )
                    )
                    currentEventID = eventinfo2[0].toInt()
                }
            }


            currentEventID = -1
            for (i in eventList!!) {
                //taking all the recipes for this daily
                if (currentEventID != i.eventId) {
                    var ids = i.recipeIds.splitIgnoreEmpty(" ")
                    for (k in ids) {


                        var recipe =
                            UserPropertiesSingelton.getInstance()!!
                                .getUserRecipess()!!.get(k)!!

                        if (!recipeList!!.contains(recipe))
                            recipeList!!.add(recipe)


                        //recipeList!!.add(recipe)
                    }

                    currentEventID = i.eventId

                }
            }

            eventRecyclerViewAdapter!!.setmValues(eventList!!)
            eventRecyclerViewAdapter!!.setRecipeList(recipeList!!)
        }
    }
}