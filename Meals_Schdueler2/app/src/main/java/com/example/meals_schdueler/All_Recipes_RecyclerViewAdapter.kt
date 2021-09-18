package com.example.meals_schdueler

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList
import kotlin.math.max

class All_Recipes_RecyclerViewAdapter(

    private val values: ArrayList<Recipe>,
    childFragmentManager: FragmentManager
) : RecyclerView.Adapter<All_Recipes_RecyclerViewAdapter.ViewHolder>() {

    private var mValues: ArrayList<Recipe> = values
    private var childFragmentManager = childFragmentManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_all_recipes, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item: Recipe = mValues[position] // each item postion
        holder.mItem = item
        holder.recipeName.setText(item.recipeName)
        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)
        holder.recipeImage.setImageBitmap(item.pictureBitMap)


        holder.recipeInfo.setOnClickListener {
            var instructions =
                HowToStroreValue(item.instructions.howToStore)
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

        holder.creatorInfo.setOnClickListener {
            var dialog = CreatorInfoDialog(item.shareInfo, item.ownerId)
            dialog.show(childFragmentManager, "CreatorInfo")
        }


        holder.copyImage.setOnClickListener {

            var tmp: ArrayList<Ingredient> = ArrayList()
            for (i in item.listOfIngredients) {
                var ingredient = Ingredient(
                    i.ingredientID,
                    UserInterFace.userID,
                    i.ingridentName,
                    i.pictureBitMap,
                    i.typeOfMeal,
                    i.typeofSeason,
                    i.howToStore,
                    false,
                    false,
                    i.protein_,
                    i.carbs_,
                    i.fat,
                    i.nutritiousDes,
                    i.costPerGram,
                    false


                )


                var s = AsynTaskNew(ingredient, childFragmentManager)
                s.execute()

                // because ingredient is transfer by reference we save each "copied ingredient" in tmp list
                //then when asyn task finished the ingredientID of each element there will be changed
                //and then when we upload recipe we give it the tmp list with the same ingredients but different id
                tmp.add(ingredient)


            }

            var instructions =
                HowToStroreValue(item.instructions.howToStore)
            var recipe = Recipe(
                item.recipeId,
                UserInterFace.userID,
                item.recipeName,
                item.pictureBitMap,
                item.typeOfMeal,
                item.numOfPortions,
                item.totalCost,
                false,
                false,
                tmp,
                item.quantityList,
                instructions
            )

            var s = AsynTaskNew(recipe, childFragmentManager)
            s.execute()


        }
    }

    fun setmValues(mValues: ArrayList<Recipe>) {
        this.mValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var recipeImage: ImageView = view.findViewById(R.id.imageViewPicRecipe)
//        var ingredientCart: ImageView = view.findViewById(R.id.imageViewCart)
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