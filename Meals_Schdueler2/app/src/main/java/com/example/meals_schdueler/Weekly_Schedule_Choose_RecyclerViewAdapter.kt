package com.example.meals_schdueler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meals_schdueler.dummy.DailySchedule


class Weekly_Schedule_Choose_RecyclerViewAdapter(

    var weeklyValues: ArrayList<WeeklySchedule>,
    var dailyValues: ArrayList<DailySchedule>,
    var recipes: ArrayList<Recipe>,
    weeklyId: ArrayList<Int>?,
    childFragmentManager: FragmentManager
) : RecyclerView.Adapter<Weekly_Schedule_Choose_RecyclerViewAdapter.ViewHolder>() {

    private var mValues: ArrayList<WeeklySchedule> = weeklyValues
    private var mValuesDaily :  ArrayList<DailySchedule> = dailyValues
    private var recipe = recipes
    private var childFragmentManager = childFragmentManager
    private var numOfWeekly= 1
    private var weeklyId = weeklyId



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Weekly_Schedule_Choose_RecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.weekly_choose, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item: WeeklySchedule = mValues[position] // each item postion
        holder.mItem = item
        if (numOfWeekly < mValues.size) {
            holder.numOfDaily.setText(numOfWeekly++.toString())
        }
        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)


        holder.weeklyInfo.setOnClickListener {



//            var dialog = DailyDialogInfo(
//                recipe,
//                item.quantities,
//                item.numOfMeals,
//                item.recipeIds,
//                position + 1,
//                item.dailyId
//
//            )
      //      dialog.show(childFragmentManager, "MyRecipeIngredients")

        }


        holder.choose.setChecked(holder.arr[position]);
        holder.choose.setOnClickListener() {

//
//            if (holder.choose.isChecked == true && (dailyId!!.isEmpty())) {
//                dailyId!!.add(position)
//                holder.arr[position] = true
//
//            } else if (holder.choose.isChecked == false) {
//                if (dailyId!!.contains(position)) {
//                    holder.arr[position] = false
//                    dailyId!!.remove(position)
//
//                }
//
//            }


        }


    }


    fun setmValues(mValues: ArrayList<WeeklySchedule>) {
        this.mValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val idView: TextView = view.findViewById(R.id.item_number)
        //val contentView: TextView = view.findViewById(R.id.content)

        var weeklyInfo: Button = view.findViewById(R.id.buttonInfo)
        var cart: ImageView = view.findViewById(R.id.imageViewCart)
        var numOfDaily: TextView = view.findViewById(R.id.numOfDailyTextView)
        var choose: CheckBox = view.findViewById(R.id.DailyCheckBox)
        val arr = Array(mValues.size, { i -> false })
        lateinit var mItem: WeeklySchedule


        override fun toString(): String {
            return super.toString()
        }


    }

    override fun getItemCount(): Int = weeklyValues.size

}