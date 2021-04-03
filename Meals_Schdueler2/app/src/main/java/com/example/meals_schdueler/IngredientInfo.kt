package com.example.meals_schdueler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import kotlin.math.cos


class IngredientInfo(item: Ingredient) : DialogFragment(), GetAndPost, View.OnClickListener {


    lateinit var ingredientName: EditText
    lateinit var costPerGram: EditText
    lateinit var typeOfMeal: Spinner
    lateinit var typeOfSeason: Spinner
    lateinit var nutritiousBtn: Button
    lateinit var editBtn: Button
    lateinit var copyBtn: Button
    lateinit var howToStoreBtn: Button
    lateinit var shareIngredient: CheckBox
    lateinit var shareInfo: CheckBox
    lateinit var typeOfMeall: String
    lateinit var typeSeasson: String
    lateinit var ingredientImage: ImageView

    var ingredient = item
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var x: View = inflater.inflate(R.layout.ingredeint_info, container, false)
        ingredientName = x.findViewById(R.id.editTextIngredientName)
        typeOfMeal = x.findViewById(R.id.typeOfMealSpinner)
        typeOfSeason = x.findViewById(R.id.typeOfSeassonSpinner)
        nutritiousBtn = x.findViewById(R.id.buttonNutritious)
        howToStoreBtn = x.findViewById(R.id.howToStoreBtn)
        editBtn = x.findViewById(R.id.buttonEdit)
        shareInfo = x.findViewById(R.id.checkBoxShareInfo)
        shareIngredient = x.findViewById(R.id.checkBoxShareIngredient)
        ingredientImage = x.findViewById(R.id.imageViewPic)
        costPerGram = x.findViewById(R.id.editTextCost)

        editBtn.setOnClickListener(this)

        setIngredientData()


        return x
    }


    /// spinner clickabe false doesnt work
    private fun setIngredientData() {
        ingredientName.setText(ingredient.ingridentName)
        // Log.v("Elad",getIndex(typeOfMeal,ingredient.typeOfMeal).toString())
        typeOfMeal.setSelection(getIndex(typeOfMeal, ingredient.typeOfMeal))
        typeOfSeason.setSelection(getIndex(typeOfSeason, ingredient.typeofSeason))
        costPerGram.setText(ingredient.costPerGram)
        ingredientImage.setImageBitmap(ingredient.pictureBitMap)
        shareInfo!!.isChecked = ingredient.shareInfo
        shareIngredient!!.isChecked = ingredient.shareIngredient
//        typeOfSeason!!.isClickable = false
//        typeOfMeal.isClickable = false
        ingredientName.isFocusable = false
        costPerGram.isFocusable = false


    }

    // a function thats gets a value in a spinner and returns its position
    private fun getIndex(spinner: Spinner, myString: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(myString, ignoreCase = true)) {
                return i
            }
        }
        return 0
    }


    override fun DoNetWorkOpreation(): String {
        TODO("Not yet implemented")
    }

    override fun getData(str: String) {
        TODO("Not yet implemented")
    }

    override fun onClick(p0: View?) {
        if (p0 == editBtn) {
            Log.v("Elad", "CLICKEDDDD")
            unlockEdit()
        } else if (p0 == ingredientImage) {
            Log.v("Elad", "CLICKED")
        }
    }

    private fun unlockEdit() {
        costPerGram.isFocusable = true
        ingredientName.isFocusable = true
        shareIngredient.isClickable = true
        shareInfo.isClickable = true

    }
}