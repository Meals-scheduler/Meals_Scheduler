package com.example.meals_schdueler

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meals_schdueler.dummy.DummyContent.DummyItem
import kotlin.collections.ArrayList

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class Recipe_Ingredients_RecyclerViewAdapter(
    l: AddRecipeFragment?,
    // private var values: ArrayList<Ingredient>,
    // listItems: Recipe_Ingredients_List?,
    childFragmentManager: FragmentManager
) : RecyclerView.Adapter<Recipe_Ingredients_RecyclerViewAdapter.ViewHolder>() {

    private var l = l
    private var mValues: ArrayList<Ingredient> = l!!.ingredientList!!
    private var mIntValues: Recipe_Ingredients_List = l!!.listItems!!
    private var childFragmentManager = childFragmentManager
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_ingredients_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item: Ingredient = mValues[position] // each item postion
        holder.mItem = item
        holder.ingredientName.setText(item.ingridentName)
        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)
        holder.ingredientImage.setImageBitmap(item.pictureBitMap)
        holder.ingredientInfo.setOnClickListener {
            var dialog = MyIngredientInfo(item, true)
            dialog.show(childFragmentManager, "IngredientInfo")

        }

        holder.deleteIngredient.setOnClickListener {

            mValues.removeAt(position)
            mIntValues.list!!.removeAt(position)
            notifyDataSetChanged()
            l!!.calculateCost()
            l!!.calculateNutritiousValues()
            l!!.costList!!.removeAt(position)

        }


        //holder.idView.text = item.id
        // holder.contentView.text = item.content
    }

    fun setmValues(mValues: ArrayList<Ingredient>) {
        this.mValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val idView: TextView = view.findViewById(R.id.item_number)
        //val contentView: TextView = view.findViewById(R.id.content)
        var ingredientImage: ImageView = view.findViewById(R.id.imageViewPicIngr)
        var ingredientName: Button = view.findViewById(R.id.buttonIngredientName)
        var ingredientInfo: ImageView = view.findViewById(R.id.imageViewInfo)
        var deleteIngredient: ImageView = view.findViewById(R.id.imageViewDel)
        lateinit var mItem: Ingredient


        override fun toString(): String {
            return super.toString()
        }
    }
}