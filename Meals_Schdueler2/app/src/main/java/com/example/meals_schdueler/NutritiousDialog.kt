package com.example.meals_schdueler

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment


class NutritiousDialog() : DialogFragment() {


    lateinit var btnOk: Button
    lateinit var description: EditText
    lateinit var dialogListener: DialogListener




    override fun onAttach(activity: Activity) {
        super.onAttach(activity)

        try {
            dialogListener = activity as DialogListener
            Log.v("Elad", "here !!")
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + "must implement DialogListener")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var rootView: View = inflater.inflate(R.layout.nutritious_dialog, container, false)
        btnOk = rootView.findViewById(R.id.ButtonOkNutritious)
        description = rootView.findViewById(R.id.DescriptionEditText)

        btnOk.setOnClickListener({
            dialogListener.applyText(description.text.toString())
            // Toast.makeText(context, description.text.toString(), Toast.LENGTH_SHORT).show()
            // (activity as AddIngredientFragment?)!!.stam.setText(description.text)
            dismiss()
        })

        return rootView
    }


    interface DialogListener {
        fun applyText(str: String)
    }


}