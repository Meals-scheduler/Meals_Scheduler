package com.example.meals_schdueler

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.FragmentManager

import com.example.meals_schdueler.dummy.DummyContent.DummyItem
import java.util.ArrayList

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class All_IIngredients_RecyclerViewAdapter(
    private val values: ArrayList<Ingredient>,
    childFragmentManager: FragmentManager
) : RecyclerView.Adapter<All_IIngredients_RecyclerViewAdapter.ViewHolder>() {

    private var mValues: ArrayList<Ingredient> = values
    private var childFragmentManager = childFragmentManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_allingredients1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item : Ingredient = mValues[position] // each item postion
        holder.mItem =item
        holder.ingredientName.setText(item.ingridentName)
        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)
        holder.ingredientImage.setImageBitmap(item.pictureBitMap)
        holder.ingredientInfo.setOnClickListener{
            // opens IngredientInfo
            var dialog = AllIngredientInfo(item)
            dialog.show(childFragmentManager,"IngredientInfo")


        }

        holder.creatorInfo.setOnClickListener{
           var dialog = CreatorInfoDialog(item.shareInfo,item.ownerId)
            dialog.show(childFragmentManager,"CreatorInfo")
        }
    }

    fun setmValues(mValues: ArrayList<Ingredient>) {
        this.mValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var ingredientImage: ImageView = view.findViewById(R.id.imageViewPicIngr)
        var ingredientCart: ImageView = view.findViewById(R.id.imageViewCart)
        var ingredientName: Button = view.findViewById(R.id.buttonIngredientName)
        var ingredientInfo: ImageView = view.findViewById(R.id.imageViewInfo)
        var creatorInfo: ImageView = view.findViewById(R.id.imageViewCreatorInfo)
        lateinit var mItem : Ingredient

        override fun toString(): String {
            return super.toString()
        }
    }
}