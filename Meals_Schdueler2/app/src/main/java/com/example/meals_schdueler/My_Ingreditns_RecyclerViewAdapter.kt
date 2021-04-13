package com.example.meals_schdueler

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
class MyItemRecyclerViewAdapter(
    private var values: ArrayList<Ingredient>,
    childFragmentManager: FragmentManager
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    private var mValues: ArrayList<Ingredient> = values
    private var childFragmentManager = childFragmentManager
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_myingredient1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item : Ingredient = mValues[position] // each item postion
        holder.mItem =item
        holder.ingredientName.setText(item.ingridentName)
        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)
        holder.ingredientImage.setImageBitmap(item.pictureBitMap)
        holder.ingredientInfo.setOnClickListener{
            var dialog = MyIngredientInfo(item)
            dialog.show(childFragmentManager,"IngredientInfo")

        }
        holder.deleteIngredient.setOnClickListener{
            var dialog = DeleteAlertDialog(item.ingridentName,item.pictureBitMap,item.ingredientID)
            dialog.show(childFragmentManager,"DeleteAlertDialog")
        }







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
        var deleteIngredient: ImageView = view.findViewById(R.id.imageViewDel)
        lateinit var mItem : Ingredient


        override fun toString(): String {
            return super.toString()
        }
    }
}