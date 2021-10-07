package com.example.meals_schdueler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import java.util.ArrayList

class UpcomingScheduleFragment : Fragment(), GetAndPost {


    private var columnCount = 1
    private var upcomingList: ArrayList<UpComingScheudule>? = null // list of ingredietns
    private var upcomingRecyclerViewAdapter: Upcoming_RecyclerViewAdapter? =
        null // adapter for the list.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        upcomingList = ArrayList<UpComingScheudule>()
        upcomingRecyclerViewAdapter =
            Upcoming_RecyclerViewAdapter(upcomingList!!, childFragmentManager)

        arguments?.let {
            columnCount = it.getInt(MyingredientFragment1.ARG_COLUMN_COUNT)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /////////////////////////// change view
        val view = inflater.inflate(R.layout.upcoming_schedule_list, container, false)
        val recyclerView = view.findViewById<View>(R.id.list) as RecyclerView

        val context = view.context
        instance = this

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                recyclerView.adapter = upcomingRecyclerViewAdapter

            }
        }

        startTask()
//        var s = AsynTaskNew(this)
//        s.execute()
        return view
    }


    companion object {

        var instance: UpcomingScheduleFragment? = null

        fun getInstance1(): UpcomingScheduleFragment {
            return instance!!
        }

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            UpcomingScheduleFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)

                }
            }
    }

    override fun DoNetWorkOpreation(): String {
        val link =
            "https://elad1.000webhostapp.com/getSchedule.php?ownerID=" + UserInterFace.userID;


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
        upcomingList!!.clear()
        if (!str.equals("")) {
            val upcomingInfo: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()



            for (i in upcomingInfo.indices) {

                var upcomingInfo2 = upcomingInfo[i].splitIgnoreEmpty(",")
                    upcomingList!!.add(UpComingScheudule(
                        upcomingInfo2[0].toInt(),
                        upcomingInfo2[2],
                        upcomingInfo2[3].toInt()

                    ))

            }

            upcomingRecyclerViewAdapter!!.setmValues(upcomingList!!)
        }


    }

    fun startTask() {
        var s = AsynTaskNew(this, childFragmentManager,requireContext())
        s.execute()
    }


}