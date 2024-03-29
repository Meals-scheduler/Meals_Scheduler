package com.example.meals_schdueler

import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

class Upcoming_RecyclerViewAdapter(

    values: ArrayList<UpComingScheudule>,
    childFragmentManager: FragmentManager,
) : RecyclerView.Adapter<Upcoming_RecyclerViewAdapter.ViewHolder>(),
    deleteInterface {


    private var mValues: ArrayList<UpComingScheudule> = values
    private var childFragmentManager = childFragmentManager
    private var toDelete: toDelete? = null
    private var upcomingToDelete: Int? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Upcoming_RecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.upcoming_schedule, parent, false)


        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: Upcoming_RecyclerViewAdapter.ViewHolder, position: Int) {
        var item: UpComingScheudule = mValues[position] // each item postion
        holder.mItem = item


        holder.date.setText(item.date)



        holder.delete.setOnClickListener {
            upcomingToDelete = position
            var dialog = DeleteAlertDialog(
                "",
                null,
                mValues.get(position)!!.scheduleId,
                "Upcoming",
                this

            )
            dialog.show(childFragmentManager, "DeleteDaily")


        }
    }

    fun setmValues(mValues: ArrayList<UpComingScheudule>) {
        this.mValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }


    override fun getItemCount(): Int = mValues.size


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var info: Button = view.findViewById(R.id.buttonInfo)
        var date: TextView = view.findViewById(R.id.dateTextView)
        var delete: Button = view.findViewById(R.id.buttonDel)

        lateinit var mItem: UpComingScheudule


        override fun toString(): String {
            return super.toString()
        }
    }


    override fun toDelete(isDelete: Boolean) {
        if(isDelete){
            mValues.removeAt(upcomingToDelete!!)
            notifyDataSetChanged()
        }
    }


}



