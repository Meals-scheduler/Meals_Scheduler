package com.example.meals_schdueler

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meals_schdueler.dummy.DummyContent.DummyItem
import kotlin.collections.ArrayList

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class My_Ingredients_Recipe_RecyclerViewAdapter(
    private var values: ArrayList<Ingredient>,
    quantityList: ArrayList<Float>,
    childFragmentManager: FragmentManager
) : RecyclerView.Adapter<My_Ingredients_Recipe_RecyclerViewAdapter.ViewHolder>() {

    private var mValues: ArrayList<Ingredient> = values
    private var childFragmentManager = childFragmentManager
    private var quantityList = quantityList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_ingredients_list_info, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item: Ingredient = mValues[position] // each item postion
        holder.mItem = item
        holder.ingredientName.setText(item.ingridentName)
        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)
        holder.ingredientImage.setImageBitmap(item.pictureBitMap)
        holder.ingredientInfo.setOnClickListener {
            var dialog = AllIngredientInfo(item)
            dialog.show(childFragmentManager, "IngredientInfo")

        }

        holder.quantity.setText(quantityList.get(position).toString())
        holder.quantity.isFocusable = false
        var cost = quantityList.get(position).toFloat() * item.costPerGram.toFloat()/100
        holder.cost.setText(cost.toString())
        holder.cost.isFocusable = false


        //holder.idView.text = item.id
        // holder.contentView.text = item.content
    }

    fun setmValues(mValues: ArrayList<Ingredient>) {

        this.mValues = mValues

        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val idView: TextView = view.findViewById(R.id.item_number)
        //val contentView: TextView = view.findViewById(R.id.content)
        var ingredientImage: ImageView = view.findViewById(R.id.imageViewPicIngr)
        var ingredientCart: ImageView = view.findViewById(R.id.imageViewCart)
        var ingredientName: Button = view.findViewById(R.id.buttonIngredientName)
        var ingredientInfo: ImageView = view.findViewById(R.id.imageViewInfo)
        var quantity: EditText = view.findViewById(R.id.editTextCost)
        var cost : EditText = view.findViewById(R.id.editTextCost2)
        lateinit var mItem: Ingredient


        override fun toString(): String {
            return super.toString()
        }
    }
}
