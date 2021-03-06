package com.example.meals_schdueler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment


//class NutritiousDialog(listener: AddIngredientFragment, var isFirstTime: Boolean) :
class NutritiousDialog(
   var nutritousValues: NutritousValues,
    var isFirstTime: Boolean
) :
    DialogFragment() {


    lateinit var btnOk: Button
    lateinit var description: EditText
    lateinit var fats: EditText
    lateinit var carbs: EditText
    lateinit var protein: EditText
    var l: NutritousValues = nutritousValues


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var rootView: View = inflater.inflate(R.layout.nutritious_dialog, container, false)
        btnOk = rootView.findViewById(R.id.ButtonOkNutritious)
        description = rootView.findViewById(R.id.DescriptionEditText)
        fats = rootView.findViewById(R.id.FatEditText)
        protein = rootView.findViewById(R.id.ProteinEditText)
        carbs = rootView.findViewById(R.id.CarbsEditText)


        if (!isFirstTime) {
            setData()
        }



        btnOk.setOnClickListener {
            // passing the user inputs to the Addingredients. Listener class
            if (carbs.text.isNotEmpty()) {
                l.carbs = carbs.getText().toString().toFloat()


            }
            if (fats.text.isNotEmpty()) {
                l.fats = fats.getText().toString().toFloat()

            }
            if (protein.text.isNotEmpty()) {
                l.protein = protein.getText().toString().toFloat()

            }
            if (description.text.isNotEmpty()) {
                l.des = description.getText().toString()


            }


            //l.saveData()
            dismiss()
        }

        return rootView
    }

    private fun setData() {
//        carbs.setText(l.carbs_.toString())
//        fats.setText(l.fat_.toString()l)
//        protein.setText(l.protein_.toString())
//        description.setText(l.nutritiousDes)


        carbs.setText(l.carbs.toString())
        fats.setText(l.fats.toString())
        protein.setText(l.protein.toString())
        description.setText(l.des)
    }


}