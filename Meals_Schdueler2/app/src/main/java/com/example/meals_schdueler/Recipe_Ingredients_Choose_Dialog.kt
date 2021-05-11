package com.example.meals_schdueler

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList


class Recipe_Ingredients_Choose_Dialog(
    recipeIngredients: Recipe_Ingredients_List,
    userIngredientss: ArrayList<Ingredient>,
    costList : ArrayList<Int>
) :
    DialogFragment() {

    lateinit var description: EditText
    lateinit var btnDone: Button
    lateinit var exit: ImageView

    var l: Recipe_Ingredients_List = recipeIngredients
   // private var isFirstTime = isFirstTime
    private var ingredientList: ArrayList<Ingredient>? = userIngredientss
    private var costList : ArrayList<Int> = costList
    private var ingredietnsChoosenRecyclerViewAdapter: Recipe_Ingredients_Choose_RecyclerViewAdapter? =
        null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        ingredietnsChoosenRecyclerViewAdapter = Recipe_Ingredients_Choose_RecyclerViewAdapter(
            ingredientList!!, l.list!!, childFragmentManager,costList
        )


        var rootView: View =
            inflater.inflate(R.layout.recipe_ingredients_choose_dialog, container, false)
        btnDone = rootView.findViewById(R.id.doneBtn)
        val recyclerView = rootView.findViewById<View>(R.id.listView2) as RecyclerView
        recyclerView.adapter = ingredietnsChoosenRecyclerViewAdapter
        ingredietnsChoosenRecyclerViewAdapter!!.setmValues(ingredientList!!)
        exit=rootView.findViewById(R.id.imageViewX)
//        cost = rootView.findViewById(R.id.editTextCost)


        btnDone.setOnClickListener({

            dismiss()
        })

        exit.setOnClickListener({
            dismiss()
        })
        return rootView

    }

    // trigger the onDissmiss in AddRecipeFramgent to tell it that this dialog is dissmissed.
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        val parentFragment: Fragment? = parentFragment
        if (parentFragment is DialogInterface.OnDismissListener) {
            (parentFragment as DialogInterface.OnDismissListener?)!!.onDismiss(dialog)
        }
    }

}