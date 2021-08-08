package com.example.meals_schdueler

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class AllIngredientInfo(item: Ingredient) : DialogFragment(), View.OnClickListener {


    lateinit var ingredientName: EditText
    lateinit var costPerGram: EditText
    lateinit var typeOfMeal: TextView
    lateinit var typeOfSeason: TextView
    lateinit var nutritiousBtn: Button
    lateinit var copyBtn: Button
    lateinit var howToStoreBtn: Button
    lateinit var picture: String
    lateinit var ingredientImage: ImageView
    lateinit var imageX: ImageView
    var nutritousValues: NutritousValues? = null
    var howToStoreValue: HowToStroreValue? = null


    var ingredient = item
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var x: View = inflater.inflate(R.layout.all_ingredients_info, container, false)
        ingredientName = x.findViewById(R.id.editTextIngredientName)
        typeOfMeal = x.findViewById(R.id.typeOfMeal)
        typeOfSeason = x.findViewById(R.id.typeOfSeasson)
        nutritiousBtn = x.findViewById(R.id.buttonNutritious)
        howToStoreBtn = x.findViewById(R.id.howToStoreBtn)
        copyBtn = x.findViewById(R.id.buttonCopy)
        ingredientImage = x.findViewById(R.id.imageViewPic)
        costPerGram = x.findViewById(R.id.editTextCost)
        imageX = x.findViewById(R.id.imageViewX)
        nutritousValues = NutritousValues(
            ingredient.nutritiousDes,
            ingredient.fat,
            ingredient.carbs_,
            ingredient.protein_
        )
        howToStoreValue = HowToStroreValue(ingredient.howToStore)


        copyBtn.setOnClickListener(this)

        nutritiousBtn.setOnClickListener(this)
        howToStoreBtn.setOnClickListener(this)
        imageX.setOnClickListener(this)

         setIngredientData()


        return x
    }

    /// spinner clickabe false doesnt work
    private fun setIngredientData() {
        ingredientName.setText(ingredient.ingridentName)
        typeOfMeal.setText(ingredient.typeOfMeal)
        typeOfSeason.setText(ingredient.typeofSeason)
        costPerGram.setText(ingredient.costPerGram)
        ingredientImage.setImageBitmap(ingredient.pictureBitMap)


    }


    override fun onClick(p0: View?) {


        if (p0 == copyBtn) {
            // getting the bitmap from the image view
            val bitmapImage = (ingredientImage.getDrawable() as BitmapDrawable).bitmap
            // creating new ingredient to update the exiting one
            var ingredient1 = Ingredient(

                ingredient.ingredientID,
                UserInterFace.userID,
                ingredientName.getText().toString(),
                bitmapImage,
                ingredient.typeOfMeal,
                ingredient.typeofSeason,
                howToStoreValue!!.howToStore,
                false,
                false,
                nutritousValues!!.protein,
                nutritousValues!!.carbs,
                nutritousValues!!.fats,
                nutritousValues!!.des,
                costPerGram.getText().toString(),
                false


            )
            var s = AsynTaskNew(ingredient1, childFragmentManager)
            s.execute()


        } else if (p0 == imageX) {
            dismiss()
        } else if (p0 == nutritiousBtn) {
            // click on nutritious dialog
            var d = NutritiousDialog(nutritousValues!!, false)
            d.show(childFragmentManager, "NutritousDialog")
        } else if (p0 == howToStoreBtn) {
            // click on howToStore dialog
            var d = HowToStoreDialog(howToStoreValue!!, false)
            d.show(childFragmentManager, "HowToStoreDialog")
        }
    }
}