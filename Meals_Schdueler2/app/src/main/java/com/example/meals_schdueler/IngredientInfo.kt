package com.example.meals_schdueler

import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.URL
import java.net.URLEncoder


class IngredientInfo(item: Ingredient) : DialogFragment(), View.OnClickListener {

    var builder: java.lang.StringBuilder? = null
    lateinit var ingredientName: EditText
    lateinit var costPerGram: EditText
    lateinit var typeOfMeal: Spinner
    lateinit var typeOfSeason: Spinner
    lateinit var nutritiousBtn: Button
    lateinit var editBtn: Button
    lateinit var saveBtn: Button
    lateinit var howToStoreBtn: Button
    lateinit var shareIngredient: CheckBox
    lateinit var shareInfo: CheckBox
    lateinit var typeOfMeall: String
    lateinit var typeSeasson: String
    lateinit var picture: String
    lateinit var ingredientImage: ImageView
    var nutritousValues: NutritousValues? = null
    var howToStoreValue: HowToStroreValue? = null

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
        saveBtn = x.findViewById(R.id.buttonSave)
        nutritousValues = NutritousValues(
            ingredient.nutritiousDes,
            ingredient.fat,
            ingredient.carbs_,
            ingredient.protein_
        )
        howToStoreValue = HowToStroreValue(ingredient.howToStore)


        saveBtn.setOnClickListener(this)
        editBtn.setOnClickListener(this)
        nutritiousBtn.setOnClickListener(this)
        howToStoreBtn.setOnClickListener(this)
        typeOfMeal.onItemSelectedListener = SpinnerActivity()
        typeOfSeason.onItemSelectedListener = SpinnerActivity()
        setIngredientData()


        return x
    }


    /// spinner clickabe false doesnt work
    private fun setIngredientData() {
        ingredientName.setText(ingredient.ingridentName)
        // Log.v("Elad",getIndex(typeOfMeal,ingredient.typeOfMeal).toString())
        typeOfMeal.setSelection(getIndex(typeOfMeal, ingredient.typeOfMeal))
        typeOfSeason.setSelection(getIndex(typeOfSeason, ingredient.typeofSeason))
        typeOfMeall = typeOfMeal.selectedItem.toString()
        typeSeasson = typeOfSeason.selectedItem.toString()
        costPerGram.setText(ingredient.costPerGram)
        ingredientImage.setImageBitmap(ingredient.pictureBitMap)
        shareInfo!!.isChecked = ingredient.shareInfo
        shareIngredient!!.isChecked = ingredient.shareIngredient
        shareInfo.isClickable = false
        shareIngredient.isClickable = false
        typeOfSeason!!.isEnabled = false
        typeOfMeal.isEnabled = false
        ingredientName.isEnabled = false
        costPerGram.isEnabled = false


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

    inner class SpinnerActivity() : Activity(), AdapterView.OnItemSelectedListener {


        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)


            if (parent == typeOfMeal) {
                typeOfMeall = parent.getItemAtPosition(pos).toString()
                Log.v("Elad12", "$typeOfMeall")
            } else if (parent == typeOfSeason) {
                typeSeasson = parent.getItemAtPosition(pos).toString()
                Log.v("Elad12", "$typeSeasson")
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            // Another interface callback


        }
    }



    override fun onClick(p0: View?) {
        // click on edit button
        if (p0 == editBtn) {
            unlockEdit()
        } else if (p0 == ingredientImage) {
            // click on image to change it
            Log.v("Elad", "CLICKED")
        } else if (p0 == nutritiousBtn) {
            // click on nutritious dialog
            var d = NutritiousDialog(nutritousValues!!, false)
            d.show(childFragmentManager, "NutritousDialog")
        }
        else if(p0 == howToStoreBtn){
            // click on howToStore dialog
            var d = HowToStoreDialog(howToStoreValue!!, false)
            d.show(childFragmentManager, "HowToStoreDialog")
        }
        else if(p0 == saveBtn){
            // getting the bitmap from the image view
            val bitmapImage = (ingredientImage.getDrawable() as BitmapDrawable).bitmap
            // creating new ingredient to update the exiting one
            var ingredient1 = Ingredient(

                ingredient.ingredientID ,
                ingredient.ownerId,
                ingredientName.getText().toString(),
                bitmapImage,
                typeOfMeall,
                typeSeasson,
                howToStoreValue!!.howToStore,
                shareIngredient.isChecked,
                shareInfo.isChecked,
                nutritousValues!!.protein,
                nutritousValues!!.carbs,
                nutritousValues!!.fats,
                nutritousValues!!.des,
                costPerGram.getText().toString(),
                true


            )
            var s = AsynTaskNew(ingredient1)
            s.execute()



        }
    }

    private fun unlockEdit() {
        costPerGram.isEnabled = true
        ingredientName.isEnabled = true
        shareIngredient.isClickable = true
        shareInfo.isClickable = true
        typeOfSeason!!.isEnabled = true
        typeOfMeal.isEnabled = true


    }

}