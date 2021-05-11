package com.example.meals_schdueler


import android.R.attr.checked
import android.R.id.checkbox
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meals_schdueler.dummy.DummyContent.DummyItem


/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class Recipe_Ingredients_Choose_RecyclerViewAdapter(
    private var values: ArrayList<Ingredient>,
    private var intValues: ArrayList<Int>,
    childFragmentManager: FragmentManager,
    costList: ArrayList<Int>
) :  RecyclerView.Adapter<Recipe_Ingredients_Choose_RecyclerViewAdapter.ViewHolder>() {

    private var mValues: ArrayList<Ingredient> = values
    private var mIntValues: ArrayList<Int> = intValues
    private var childFragmentManager = childFragmentManager
    private var costList: ArrayList<Int> = costList



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_ingredients_choose, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item: Ingredient = mValues[position] // each item postion
        holder.mItem = item
        holder.ingredientName.setText(item.ingridentName)
        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)
        holder.ingredientImage.setImageBitmap(item.pictureBitMap)
        holder.ingredientInfo.setOnClickListener {
            var dialog = MyIngredientInfo(item, true)
            dialog.show(childFragmentManager, "IngredientInfo")

        }



        holder.choose.setChecked(holder.arr[position]);
        holder.cost.setText(holder.arr2[position])
        holder.choose.setOnClickListener() {



            if (holder.choose.isChecked == true && !(mIntValues.contains(position))) {
                mIntValues.add(position)
                holder.arr[position] = true


                if ((holder.cost.getText().toString().trim().length > 0)) {
                    costList.add(holder.cost.text.toString().toInt())
                    holder.arr2[position]=holder.cost.text.toString()
                }

            } else if (holder.choose.isChecked == false) {
                if (mIntValues.contains(position)) {
                    holder.arr[position] = false
                    mIntValues.remove(position)
                }

            }


      }



    }



    fun setmValues(mValues: ArrayList<Ingredient>) {
        this.mValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val idView: TextView = view.findViewById(R.id.item_number)
        //val contentView: TextView = view.findViewById(R.id.content)
        var ingredientImage: ImageView = view.findViewById(R.id.imageViewPicIngr)
        var ingredientName: Button = view.findViewById(R.id.buttonIngredientName)
        var ingredientInfo: ImageView = view.findViewById(R.id.imageViewInfo)
        var choose: CheckBox = view.findViewById(R.id.ingCheckBox)
        var cost: EditText = view.findViewById(R.id.editTextCost)
        lateinit var mItem: Ingredient
        val arr = Array(mValues.size, {i-> false})
        val arr2 = Array(mValues.size, {i->""})





        override fun toString(): String {
            return super.toString()
        }
    }


}