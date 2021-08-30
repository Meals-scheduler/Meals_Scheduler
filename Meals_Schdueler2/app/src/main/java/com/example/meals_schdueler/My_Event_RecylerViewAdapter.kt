package com.example.meals_schdueler

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meals_schdueler.dummy.DailySchedule

import java.util.*
import kotlin.collections.ArrayList

class My_Event_RecylerViewAdapter(

    values: ArrayList<Event>,
    childFragmentManager: FragmentManager,
    context: Context?,

    ) : RecyclerView.Adapter<My_Event_RecylerViewAdapter.ViewHolder>(){


    private var mValues: ArrayList<Event> = values
    private var childFragmentManager = childFragmentManager
    private var numOfEvent = 1
    private lateinit var recipeList: ArrayList<Recipe>
    private var context = context

    private var eventToSchedule = -1
    var date = ""


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): My_Event_RecylerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_event_schedule, parent, false)


        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: My_Event_RecylerViewAdapter.ViewHolder, position: Int) {
        var item: Event = mValues[position]!! // each item postion
        holder.mItem = item

        holder.numOfEvent.setText(numOfEvent++.toString())

        holder.edit.setOnClickListener {

            var tempRecipeList: ArrayList<Recipe> = ArrayList()

            // copying the Recipe list so if we edit it and then quit without saving, it wouldn't change the original list
            for (i in recipeList) {
                tempRecipeList.add(i)
            }
            var dialog = EditEventDailog(
                tempRecipeList,
                mValues.get(position)!!.quantities,
                mValues.get(position)!!.recipeIds,
                position + 1,
                this,
                mValues.get(position)!!.eventId,
                mValues.get(position)!!.eventName,
                mValues.get(position)!!.date
            )
            dialog.show(childFragmentManager, "dailyEdit")
        }



        holder.info.setOnClickListener {

            var dialog = EventDialogInfo(
                recipeList!!,
                mValues.get(position)!!.quantities,
                mValues.get(position)!!.recipeIds,
                position + 1,
                mValues.get(position)!!.eventId,
                mValues.get(position)!!.eventName,
                mValues.get(position)!!.date,

            )
            dialog.show(childFragmentManager, "DailyDialogInfo")

        }
//
//        holder.delete.setOnClickListener {
//
//            var dialog = DeleteAlertDialog(
//                "",
//                null,
//                mValues.get(item.dailyId.toString())!!.dailyId,
//                "Daily"
//            )
//            dialog.show(childFragmentManager, "DeleteDaily")
//
//        }
//
//        holder.date.setOnClickListener {
//            dailyToSchedule = mValues.get(item.dailyId.toString())!!.dailyId
//
//            val cal = Calendar.getInstance()
//            // to open the calender with the current date of this moment.
//            val currentYear = cal[Calendar.YEAR]
//            val currentMonth = cal[Calendar.MONTH]
//            val currentDay = cal[Calendar.DAY_OF_MONTH]
//            var dialog = DatePickerDialog(
//                context!!,
//                android.R.style.Theme_Holo_Light,
//                My_Daily_RecylerViewAdapter.calenderListener(dailyToSchedule, date, childFragmentManager),
//                currentYear,
//                currentMonth,
//                currentDay
//            )
//            dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            dialog.show()
//        }
    }


    fun setmValues(mValues: ArrayList<Event>) {

        this.mValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }


    fun setRecipeList(recipeList: ArrayList<Recipe>) {

        this.recipeList = recipeList

        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    override fun getItemCount(): Int = mValues.size


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var numOfEvent: TextView = view.findViewById(R.id.numOfEventTextView)
        var info: Button = view.findViewById(R.id.buttonInfo)
        var edit: Button = view.findViewById(R.id.buttonEdit)
        var date: Button = view.findViewById(R.id.buttonSchedule)
        var delete: Button = view.findViewById(R.id.buttonDel)
        var cart: ImageView = view.findViewById(R.id.imageViewCart)
        lateinit var mItem: Event


        override fun toString(): String {
            return super.toString()
        }
    }


}