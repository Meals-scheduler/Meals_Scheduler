package com.example.meals_schdueler

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

class MyRecipeIngredietns(
    mValues: ArrayList<Ingredient>,
    recipeName: String,
    pictureBitMap: Bitmap,
    numOfPortions: String,
    quantityList: ArrayList<Int>,
    totalCost: Double
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
        totalCostEditText = x.findViewById(R.id.textViewTotalCost)
        totalCostEditText.setText("Cost :" + totalCost.toString())
        numOfPortions = x.findViewById(R.id.textViewNumOfPortions)
        numOfPortions.setText("Num of Portions : " + numOfPortionsInt)
        ingredientRecyclerViewAdapter =
            My_Ingredients_Recipe_RecyclerViewAdapter(ingredientList!!,quantityList, childFragmentManager)
        recyclerView.adapter = ingredientRecyclerViewAdapter
        ingredientRecyclerViewAdapter!!.setmValues(ingredientList)

        return x
    }


}
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.recipe_ingredients_list_info_list, container, false)
//        val recyclerView = view.findViewById<View>(R.id.list) as RecyclerView
//
//
//        val context = view.context
//        // Set the adapter
//        if (view is RecyclerView) {
//            with(view) {
//                layoutManager = when {
//                    columnCount <= 1 -> LinearLayoutManager(context)
//                    else -> GridLayoutManager(context, columnCount)
//                }
//                recyclerView.adapter = ingredientRecyclerViewAdapter
//
//            }
//        }
//
//        return view
//    }
