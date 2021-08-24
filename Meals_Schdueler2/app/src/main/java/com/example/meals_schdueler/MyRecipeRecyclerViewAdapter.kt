package com.example.meals_schdueler

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView

class MyRecipeRecyclerViewAdapter(
    private var values:HashMap<String,Recipe>,
    childFragmentManager: FragmentManager
) : RecyclerView.Adapter<MyRecipeRecyclerViewAdapter.ViewHolder>() {

    private var mValues:HashMap<String,Recipe> = values
    private var childFragmentManager = childFragmentManager
    private var RecipeList : ArrayList<Recipe> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyRecipeRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_myrecipe, parent, false)

        for(i in values){
            RecipeList.add(i.value)
        }
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: MyRecipeRecyclerViewAdapter.ViewHolder, position: Int) {

        var item: Recipe = RecipeList[position]!! // each item postion
        holder.mItem = item
        holder.RecipeName.setText(item.recipeName)
        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)
        holder.recipeImage.setImageBitmap(item.pictureBitMap)

        holder.deleteRecipe.setOnClickListener {
            var dialog = DeleteAlertDialog(item.recipeName, item.pictureBitMap, item.recipeId, "Recipe")
            dialog.show(childFragmentManager, "DeleteAlertDialog")
        }

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

    }

    fun setmValues(mValues: HashMap<String,Recipe>) {
        this.mValues = mValues
        RecipeList.clear()
        for (i in mValues) {
            RecipeList.add(i.value)
        }
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val idView: TextView = view.findViewById(R.id.item_number)
        //val contentView: TextView = view.findViewById(R.id.content)
        var recipeImage: ImageView = view.findViewById(R.id.imageViewPicRecipe)
        var ingredientCart: ImageView = view.findViewById(R.id.imageViewCart)
        var RecipeName: Button = view.findViewById(R.id.buttonRecipeName)
        var ingredientInfo: ImageView = view.findViewById(R.id.imageViewIngredientsInfo)
        var deleteRecipe: ImageView = view.findViewById(R.id.imageViewDel)
        lateinit var mItem: Recipe


        override fun toString(): String {
            return super.toString()
        }
    }
}