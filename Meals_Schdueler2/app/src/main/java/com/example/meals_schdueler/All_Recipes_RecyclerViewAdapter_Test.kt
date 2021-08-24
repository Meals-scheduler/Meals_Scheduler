package com.example.meals_schdueler

import android.app.Activity
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.io.ByteArrayOutputStream
import java.util.*

class All_Recipes_RecyclerViewAdapter_Test(

    private val values: ArrayList<Recipe>,
    childFragmentManager: FragmentManager,
    activity: Activity

) : RecyclerView.Adapter<All_Recipes_RecyclerViewAdapter_Test.ViewHolder>() {

    private var mValues: ArrayList<Recipe> = values
    private var childFragmentManager = childFragmentManager
    private var activity = activity


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): All_Recipes_RecyclerViewAdapter_Test.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_all_recipes, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: All_Recipes_RecyclerViewAdapter_Test.ViewHolder,
        position: Int
    ) {
        var data: Recipe = mValues[position]

//        Glide.with(activity).load(BitMapToString(data.pictureBitMap))
//            .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.recipeImage)

        holder.recipeName.setText(data.recipeName)
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    fun setmValues(mValues: ArrayList<Recipe>) {
        this.mValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    fun BitMapToString(imgBitMap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        imgBitMap.compress(Bitmap.CompressFormat.JPEG, 10, baos)
        val b: ByteArray = baos.toByteArray()
        return Base64.getEncoder().encodeToString(b)
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var recipeImage: ImageView = view.findViewById(R.id.imageViewPicRecipe)
        var ingredientCart: ImageView = view.findViewById(R.id.imageViewCart)
        var recipeName: Button = view.findViewById(R.id.buttonRecipeName)
        var copyImage: ImageView = view.findViewById(R.id.imageViewCopy)
        var recipeInfo: ImageView = view.findViewById(R.id.imageViewInfo)
        var creatorInfo: ImageView = view.findViewById(R.id.imageViewCreatorInfo)
        lateinit var mItem: Recipe

        override fun toString(): String {
            return super.toString()
        }
    }
}