package com.example.meals_schdueler

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.meals_schdueler.dummy.DailySchedule
import java.util.*
import kotlin.collections.HashMap

class Weekly_Schedule_Choose_Dialog(
    weeklyList: TreeMap<String,WeeklySchedule>,
    dailyList:  TreeMap<String,DailySchedule>,
    recipeList: HashMap<String,Recipe>,
    list:Recipe_Ingredients_List,
) : DialogFragment() {


    lateinit var exit: ImageView
    // private var recipesList: ArrayList<Recipe>? = null
    private lateinit var btnDone: Button
    private var dailyList = dailyList
    private var recipeList = recipeList
    private var weeklyList = weeklyList
    private var l: Recipe_Ingredients_List = list

    private var weeklyChoosenRecyclerViewAdapter: Weekly_Schedule_Choose_RecyclerViewAdapter? =
        null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        weeklyChoosenRecyclerViewAdapter = Weekly_Schedule_Choose_RecyclerViewAdapter(
            weeklyList!!,dailyList!!, recipeList,l.list, childFragmentManager
        )

        var rootView: View =
            inflater.inflate(R.layout.weekly_choose_list, container, false)
        btnDone = rootView.findViewById(R.id.doneBtn)
        val recyclerView = rootView.findViewById<View>(R.id.list) as RecyclerView
        recyclerView.adapter = weeklyChoosenRecyclerViewAdapter
        weeklyChoosenRecyclerViewAdapter!!.setmValues(weeklyList!!)
        exit=rootView.findViewById(R.id.imageViewX)



        btnDone.setOnClickListener({

            dismiss()
        })

        exit.setOnClickListener({
            dismiss()
        })
        return rootView

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        val parentFragment: Fragment? = parentFragment
        if (parentFragment is DialogInterface.OnDismissListener) {
            (parentFragment as DialogInterface.OnDismissListener?)!!.onDismiss(dialog)
        }



    }



}