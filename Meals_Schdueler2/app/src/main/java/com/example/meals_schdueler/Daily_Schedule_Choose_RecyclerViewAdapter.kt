package com.example.meals_schdueler

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meals_schdueler.dummy.DailySchedule
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Daily_Schedule_Choose_RecyclerViewAdapter(

    var values: TreeMap<String, DailySchedule>,
    var recipes: HashMap<String, Recipe>,
    dailyId: ArrayList<Int>?,
    childFragmentManager: FragmentManager
) : RecyclerView.Adapter<Daily_Schedule_Choose_RecyclerViewAdapter.ViewHolder>() {
    private var mValues: TreeMap<String, DailySchedule> = values
    private var recipe = recipes
    private var childFragmentManager = childFragmentManager
    private var numOfDaily = 1
    private var dailyId = dailyId

    private var dailyList: ArrayList<DailySchedule> = ArrayList()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Daily_Schedule_Choose_RecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daily_choose, parent, false)

        for(i in values){
            dailyList.add(i.value)
        }
        return ViewHolder(view)
    }

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item: DailySchedule = dailyList[position]!! // each item postion
        holder.mItem = item
        if (numOfDaily < mValues.size) {
            holder.numOfDaily.setText(numOfDaily++.toString())
        }
        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)


        holder.dailyInfo.setOnClickListener {


            Log.v("Elad1", "OKKK2")
            var recipeist: ArrayList<Recipe> = ArrayList()
            var recipesArr = item.recipeIds.splitIgnoreEmpty(" ")
            var j = 0
            for (i in recipesArr) {
                recipeist.add(UserPropertiesSingelton.getInstance()!!.getUserRecipess()!!.get(i)!!)

            }


            var dialog = DailyDialogInfo(
                recipeist,
                item.quantities,
                item.numOfMeals,
                item.recipeIds,
                position + 1,
                item.dailyId

            )
            dialog.show(childFragmentManager, "MyRecipeIngredients")

        }


        holder.choose.setChecked(holder.arr[position]);
        holder.choose.setOnClickListener() {


            if (holder.choose.isChecked == true && (dailyId!!.isEmpty())) {
                dailyId!!.add(item.dailyId)
                holder.arr[position] = true

            } else if (holder.choose.isChecked == false) {
                if (dailyId!!.contains(position)) {
                    holder.arr[position] = false
                    dailyId!!.remove(position)

                }

            }


        }


    }


    fun setmValues(mValues: TreeMap<String, DailySchedule>) {
        this.mValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val idView: TextView = view.findViewById(R.id.item_number)
        //val contentView: TextView = view.findViewById(R.id.content)

        var dailyInfo: Button = view.findViewById(R.id.buttonInfo)
        var cart: ImageView = view.findViewById(R.id.imageViewCart)
        var numOfDaily: TextView = view.findViewById(R.id.numOfDailyTextView)
        var choose: CheckBox = view.findViewById(R.id.DailyCheckBox)
        val arr = Array(mValues.size, { i -> false })
        lateinit var mItem: DailySchedule


        override fun toString(): String {
            return super.toString()
        }


    }

    override fun getItemCount(): Int = values.size
}
