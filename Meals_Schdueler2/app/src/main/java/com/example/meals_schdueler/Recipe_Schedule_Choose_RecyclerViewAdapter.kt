package com.example.meals_schdueler

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView


class Recipe_Schedule_Choose_RecyclerViewAdapter(

    private var values: ArrayList<Recipe>,
    private var intValues: ArrayList<Int>,
    private var quantities: ArrayList<Int>,
    childFragmentManager: FragmentManager
) : RecyclerView.Adapter<Recipe_Schedule_Choose_RecyclerViewAdapter.ViewHolder>() {

    private var mValues: ArrayList<Recipe> = values
    private var mIntValues: ArrayList<Int> = intValues
    private var childFragmentManager = childFragmentManager
    private var quantitiess = quantities
    private var firstTime: Boolean = true
    var str: String = ""

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Recipe_Schedule_Choose_RecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipes_choose, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item: Recipe = mValues[position] // each item postion
        holder.mItem = item
        holder.ingredientName.setText(item.recipeName)
        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)
        holder.ingredientImage.setImageBitmap(item.pictureBitMap)
        holder.ingredientInfo.setOnClickListener {
            var dialog = MyRecipeIngredietns(
                item.listOfIngredients,
                item.recipeName,
                item.pictureBitMap,
                item.numOfPortions,
                item.quantityList,
                item.totalCost
            )
            dialog.show(childFragmentManager, "MyRecipeIngredients")

        }

        holder.quantity.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {

                str = parent!!.getItemAtPosition(position).toString()



                if (firstTime && quantitiess.size == mValues.size) {
                    quantitiess.clear()
                    firstTime = false
                }
            }


            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        })


        holder.choose.setChecked(holder.arr[position]);
        holder.choose.setOnClickListener() {


            if (holder.choose.isChecked == true && !(mIntValues.contains(position))) {
                mIntValues.add(position)
                holder.arr[position] = true
                if (str == "") {
                    quantitiess.add(1)
                }
                else{
                    quantitiess.add(str.toInt())
                    str=""
                }


//                if ((holder.cost.getText().toString().trim().length > 0)) {
//                    holder.arr2[position]=holder.cost.text.toString()
//                }

            } else if (holder.choose.isChecked == false) {
                if (mIntValues.contains(position)) {
                    holder.arr[position] = false
                    mIntValues.remove(position)
                    quantitiess.remove(position)
                }

            }


        }


    }


    fun setmValues(mValues: ArrayList<Recipe>) {
        this.mValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val idView: TextView = view.findViewById(R.id.item_number)
        //val contentView: TextView = view.findViewById(R.id.content)
        var ingredientImage: ImageView = view.findViewById(R.id.imageViewPicRecipe)
        var ingredientName: Button = view.findViewById(R.id.buttonRecipeName)
        var ingredientInfo: ImageView = view.findViewById(R.id.imageViewIngredientsInfo)
        var choose: CheckBox = view.findViewById(R.id.RecipeCheckBox)
        var quantity: Spinner = view.findViewById(R.id.spinner)
        lateinit var mItem: Recipe
        val arr = Array(mValues.size, { i -> false })
//        val arr2 = Array(mValues.size, {i->""})


        override fun toString(): String {
            return super.toString()
        }


    }


}