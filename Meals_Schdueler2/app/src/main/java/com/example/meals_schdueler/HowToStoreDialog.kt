package com.example.meals_schdueler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class HowToStoreDialog(howToStoreListener: AddIngredientFragment, var isFirstTime: Boolean) : DialogFragment() {


    lateinit var description: EditText
    lateinit var btnDone : Button
    var l: AddIngredientFragment = howToStoreListener



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        var rootView: View = inflater.inflate(R.layout.how_to_store_dialog, container, false)
        description = rootView.findViewById(R.id.howToStoreDesEditText)
        btnDone = rootView.findViewById(R.id.ButtonOkHowToStore)

        if (!isFirstTime) {
            setData()
        }

        btnDone.setOnClickListener({


            if (description.text.isNotEmpty()) {
                l.howToStoreDes = description.getText().toString()
            }
            //l.saveData()
            dismiss()
        })

        return rootView

    }

    private fun setData() {
      description.setText(l.howToStoreDes)
    }
}