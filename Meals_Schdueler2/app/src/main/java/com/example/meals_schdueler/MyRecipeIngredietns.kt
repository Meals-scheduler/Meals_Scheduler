package com.example.meals_schdueler

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat
import kotlin.collections.ArrayList

class MyRecipeIngredietns(
    mValues: ArrayList<Ingredient>,
    recipeName: String,
    pictureBitMap: Bitmap,
    numOfPortions: String,
    quantityList: ArrayList<Float>,
    totalCost: Double,
    typeOfMeal : String,
    instructios : HowToStroreValue
) : DialogFragment() {

    private var columnCount = 1
    private var ingredientList: ArrayList<Ingredient> = mValues // list of ingredietns
    private var ingredientRecyclerViewAdapter: My_Ingredients_Recipe_RecyclerViewAdapter? =
        null // adapter for the list.
    private lateinit var recipeNameTextView: TextView
    private lateinit var numOfPortions: TextView
    private var recipeName = recipeName
    private var numOfPortionsInt = numOfPortions
    private var quantityList = quantityList
    private lateinit var recipeImg: ImageView
    private var pictureBitMap = pictureBitMap
    private lateinit var imageViewX: ImageView
    private var totalCost = totalCost
    private lateinit var totalCostEditText : TextView
    private lateinit var typeMeal : TextView
    private var typeOfMeal = typeOfMeal
    private lateinit var btnInstructions : Button
    private var instructions = instructios

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var x: View = inflater.inflate(R.layout.recipe_ingredients_list_info_list, container, false)
        val recyclerView = x.findViewById<View>(R.id.list) as RecyclerView
        recipeNameTextView = x.findViewById(R.id.textViewRecipeName)
        recipeNameTextView.setText(recipeName)
        recipeImg = x.findViewById(R.id.imageViewRecipe)
        recipeImg.setImageBitmap(pictureBitMap)
        imageViewX = x.findViewById(R.id.imageViewX)
        imageViewX.setOnClickListener {
            dismiss()
        }
        btnInstructions = x.findViewById(R.id.buttonInstructions)
        btnInstructions.setOnClickListener{
            var dialog = HowToStoreDialog(instructions!!, false)
            dialog.show(childFragmentManager, "HowToStoreDialog")
        }
        totalCostEditText = x.findViewById(R.id.textViewTotalCost)
        totalCost = (DecimalFormat("##.##").format(totalCost)).toDouble()
        totalCostEditText.setText("Cost :" + totalCost.toString())
        numOfPortions = x.findViewById(R.id.textViewNumOfPortions)
        numOfPortions.setText("Num of Portions : " + numOfPortionsInt)
        typeMeal=x.findViewById(R.id.textViewTypeMeal)
        typeMeal.setText("Type Of Meal : " +typeOfMeal)
        ingredientRecyclerViewAdapter =
            My_Ingredients_Recipe_RecyclerViewAdapter(ingredientList!!,quantityList, childFragmentManager)
        recyclerView.adapter = ingredientRecyclerViewAdapter
        ingredientRecyclerViewAdapter!!.setmValues(ingredientList)

        return x
    }


}

