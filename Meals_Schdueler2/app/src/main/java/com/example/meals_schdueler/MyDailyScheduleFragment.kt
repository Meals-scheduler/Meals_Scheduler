package com.example.meals_schdueler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class MyDailyScheduleFragment : Fragment() , GetAndPost {


    private var columnCount = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(AddDailyScheduleFragment.ARG_COLUMN_COUNT)
        }

    }



    companion object {

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
        val x = inflater.inflate(R.layout.my_daily_schedule, null)

        return x
    }

    override fun DoNetWorkOpreation(): String {
        TODO("Not yet implemented")
    }

    override fun getData(str: String) {
        TODO("Not yet implemented")
    }
}