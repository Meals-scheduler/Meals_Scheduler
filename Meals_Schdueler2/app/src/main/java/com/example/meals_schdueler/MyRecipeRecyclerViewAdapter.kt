package com.example.meals_schdueler

import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView

class MyRecipeRecyclerViewAdapter(
    private var values: ArrayList<Recipe>,
    childFragmentManager: FragmentManager
) : RecyclerView.Adapter<MyRecipeRecyclerViewAdapter.ViewHolder>(), deleteInterface, toCopyRecipe {

    private var mValues: ArrayList<Recipe> = values
    private var childFragmentManager = childFragmentManager
    private var recipeToDelete: Int? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyRecipeRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_myrecipe, parent, false)



        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: MyRecipeRecyclerViewAdapter.ViewHolder, position: Int) {

        var item: Recipe = mValues[position]!! // each item postion
        holder.mItem = item
        holder.RecipeName.setText(item.recipeName)
        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)
        holder.recipeImage.setImageBitmap(item.pictureBitMap)

        holder.deleteRecipe.setOnClickListener {

            recipeToDelete = position

            var dialog =
                DeleteAlertDialog(
                    item.recipeName, item.pictureBitMap, item.recipeId, "Recipe", this

                )
            dialog.show(childFragmentManager, "DeleteAlertDialog")
        }

        holder.ingredientInfo.setOnClickListener {

            var instructions = HowToStroreValue(item.instructions.howToStore)
            var dialog = MyRecipeIngredietns(
                item.listOfIngredients,
                item.recipeName,
                item.pictureBitMap,
                item.numOfPortions,
                item.quantityList,
                item.totalCost,
                item.typeOfMeal,
                instructions
            )
            dialog.show(childFragmentManager, "MyRecipeIngredients")
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
        var recipeImage: ImageView = view.findViewById(R.id.imageViewPicRecipe)

        // var ingredientCart: ImageView = view.findViewById(R.id.imageViewCart)
        var RecipeName: Button = view.findViewById(R.id.buttonRecipeName)
        var ingredientInfo: ImageView = view.findViewById(R.id.imageViewIngredientsInfo)
        var deleteRecipe: ImageView = view.findViewById(R.id.imageViewDel)
        lateinit var mItem: Recipe


        override fun toString(): String {
            return super.toString()
        }
    }

    override fun toDelete(isDelete: Boolean) {
        if (isDelete) {
            mValues.removeAt(recipeToDelete!!)
            notifyDataSetChanged()
        }
    }



    override fun <T> toCopy(toCopy: T) {
        mValues.add(toCopy!! as Recipe)
        notifyDataSetChanged()
    }

}