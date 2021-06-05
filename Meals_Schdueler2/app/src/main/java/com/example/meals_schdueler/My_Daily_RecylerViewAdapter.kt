package com.example.meals_schdueler

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meals_schdueler.dummy.DailySchedule
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class My_Daily_RecylerViewAdapter (

    values: ArrayList<DailySchedule>,
    childFragmentManager: FragmentManager,
    context: Context?,
) : RecyclerView.Adapter<My_Daily_RecylerViewAdapter.ViewHolder>() , GetAndPost {

    private var mValues: ArrayList<DailySchedule> = values
    private var childFragmentManager = childFragmentManager
    private var numOfDaily = 1
    private lateinit var recipeList: ArrayList<Recipe>
    private var context = context
    private var dailyToDelete=-1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): My_Daily_RecylerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_daily_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: My_Daily_RecylerViewAdapter.ViewHolder, position: Int) {
        var item: DailySchedule = mValues[position] // each item postion
        holder.mItem = item

        holder.numOfDaily.setText(numOfDaily++.toString())

        holder.edit.setOnClickListener {

            var tempRecipeList : ArrayList<Recipe> = ArrayList()


            // copying the Recipe list so if we edit it and then quit without saving, it wouldn't change the original list
            for(i in recipeList){
                tempRecipeList.add(i)
            }
            var dialog = EditDailyDialog(
                tempRecipeList,
                mValues.get(position).quantities,
                mValues.get(position).numOfMeals,
                mValues.get(position).recipeIds,
                position+1
            )
            dialog.show(childFragmentManager, "dailyEdit")
        }

        holder.info.setOnClickListener {
            var dialog = MyRecipeIngredietns(
                recipeList.get(position).listOfIngredients,
                recipeList.get(position).recipeName,
                recipeList.get(position).pictureBitMap,
                recipeList.get(position).numOfPortions,
                recipeList.get(position).quantityList,
                recipeList.get(position).totalCost
            )
            dialog.show(childFragmentManager, "MyRecipeIngredients")

        }

        holder.delete.setOnClickListener{
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)

            builder.setTitle("Confirm")
            builder.setMessage("Are you sure?")

            builder.setPositiveButton(
                "YES",
                DialogInterface.OnClickListener { dialog, which -> // Do nothing but close the dialog
                    dailyToDelete = item.dailyId
                    var s = AsynTaskNew(this,childFragmentManager)
                    s.execute()
                    dialog.dismiss()
                })

            builder.setNegativeButton(
                "NO",
                DialogInterface.OnClickListener { dialog, which -> // Do nothing
                    dialog.dismiss()
                })

            val alert: AlertDialog = builder.create()
            alert.show()
        }


//        holder.ingredientInfo.setOnClickListener{
//            var dialog = MyIngredientInfo(item,false)
//            dialog.show(childFragmentManager,"IngredientInfo")
//
//        }
//        holder.deleteIngredient.setOnClickListener{
//            var dialog = DeleteAlertDialog(item.ingridentName,item.pictureBitMap,item.ingredientID,false)
//            dialog.show(childFragmentManager,"DeleteAlertDialog")
//        }


        //holder.idView.text = item.id
        // holder.contentView.text = item.content
    }

    fun setmValues(mValues: ArrayList<DailySchedule>) {
        numOfDaily = 1
        this.mValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }


    fun setRecipeList(recipeList: ArrayList<Recipe>) {

        this.recipeList = recipeList
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    override fun getItemCount(): Int = mValues.size


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var numOfDaily: TextView = view.findViewById(R.id.numOfDailyTextView)
        var info: Button = view.findViewById(R.id.buttonInfo)
        var edit: Button = view.findViewById(R.id.buttonEdit)
        var date: Button = view.findViewById(R.id.buttonSchedule)
        var delete: Button = view.findViewById(R.id.buttonDel)
        var cart: ImageView = view.findViewById(R.id.imageViewCart)
        lateinit var mItem: DailySchedule


        override fun toString(): String {
            return super.toString()
        }
    }

    override fun DoNetWorkOpreation(): String {
        var link = "https://elad1.000webhostapp.com/delDaily.php?DailyID=" + dailyToDelete

        Log.v("Elad1", "here")

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


        Log.v("Elad1", "Id came is" + sb.toString())
        return sb.toString()
    }

    override fun getData(str: String) {
       Log.v("Elad1","Delete successfully")
    }
}