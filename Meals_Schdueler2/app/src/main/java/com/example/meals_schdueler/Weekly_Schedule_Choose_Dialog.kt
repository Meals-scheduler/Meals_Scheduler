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
) : DialogFragment() {


    lateinit var exit: ImageView

    private lateinit var btnDone: Button

    private var weeklyList = weeklyList
    private var l: Recipe_Ingredients_List = list

    private var progressBar: ProgressBar? = null
    private lateinit var searchView: SearchView
    private lateinit var noResultsTextView: TextView



    private var weeklyChoosenRecyclerViewAdapter: Weekly_Schedule_Choose_RecyclerViewAdapter? =
        null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var view: View =
            inflater.inflate(R.layout.weekly_choose_list, container, false)
        btnDone = view.findViewById(R.id.doneBtn)
        val recyclerView = view.findViewById<View>(R.id.list) as RecyclerView

        progressBar = view.findViewById(R.id.progress_bar)

        searchView = view.findViewById(R.id.search_bar)

        noResultsTextView = view.findViewById(R.id.tv_emptyTextView)
        weeklyChoosenRecyclerViewAdapter = Weekly_Schedule_Choose_RecyclerViewAdapter(
            weeklyList!!, l.list, childFragmentManager, progressBar, searchView, noResultsTextView
        )


        weeklyChoosenRecyclerViewAdapter!!.setmValues(weeklyList!!)
        weeklyChoosenRecyclerViewAdapter!!.startTask()
        exit = view.findViewById(R.id.imageViewX)



        btnDone.setOnClickListener({

            dismiss()
        })

        exit.setOnClickListener({
            dismiss()
        })


        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = weeklyChoosenRecyclerViewAdapter
        return view


    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        val parentFragment: Fragment? = parentFragment
        if (parentFragment is DialogInterface.OnDismissListener) {
            (parentFragment as DialogInterface.OnDismissListener?)!!.onDismiss(dialog)
        }


    }


}