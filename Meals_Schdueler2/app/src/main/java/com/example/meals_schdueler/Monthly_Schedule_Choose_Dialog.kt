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
) : DialogFragment() {
    lateinit var exit: ImageView


    private lateinit var btnDone: Button

    private var monthlyList = monthlyList
    private var l: Recipe_Ingredients_List = list


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


        var view: View =
            inflater.inflate(R.layout.monthly_choose_list, container, false)
        btnDone = view.findViewById(R.id.doneBtn)
        val recyclerView = view.findViewById<View>(R.id.list) as RecyclerView
        progressBar = view.findViewById(R.id.progress_bar)

        searchView = view.findViewById(R.id.search_bar)

        noResultsTextView = view.findViewById(R.id.tv_emptyTextView)

        monthlyChoosenRecyclerViewAdapter = Monthly_Schedule_Choose_RecyclerViewAdapter(
            monthlyList!!, l.list, childFragmentManager, progressBar, searchView, noResultsTextView,requireContext()
        )


        monthlyChoosenRecyclerViewAdapter!!.setmValues(monthlyList!!)
        monthlyChoosenRecyclerViewAdapter!!.startTask()
        exit = view.findViewById(R.id.imageViewX)



        btnDone.setOnClickListener({

            dismiss()
        })

        exit.setOnClickListener({
            dismiss()
        })


        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = monthlyChoosenRecyclerViewAdapter
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