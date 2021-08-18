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
import java.util.*

class Monthly_Schedule_Choose_Dialog(
    monthlyList: TreeMap<String, MonthlySchedule>,
    list: Recipe_Ingredients_List,
) : DialogFragment(){
    lateinit var exit: ImageView


    private lateinit var btnDone: Button

    private var monthlyList = monthlyList
    private var l: Recipe_Ingredients_List = list

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

        var rootView: View =
            inflater.inflate(R.layout.monthly_choose_list, container, false)
        btnDone = rootView.findViewById(R.id.doneBtn)
        val recyclerView = rootView.findViewById<View>(R.id.list) as RecyclerView
        recyclerView.adapter = monthlyChoosenRecyclerViewAdapter
        monthlyChoosenRecyclerViewAdapter!!.setmValues(monthlyList!!)
        exit = rootView.findViewById(R.id.imageViewX)



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