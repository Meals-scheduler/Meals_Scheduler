package com.example.meals_schdueler


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meals_schdueler.dummy.DummyContent.DummyItem
import java.util.*
import kotlin.collections.ArrayList


/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class Recipe_Ingredients_Choose_RecyclerViewAdapter(
    private var values: ArrayList<Ingredient>,
    private var intValues: ArrayList<Int>,
    childFragmentManager: FragmentManager,
    costList: ArrayList<Float>,
    context: Context
) : RecyclerView.Adapter<Recipe_Ingredients_Choose_RecyclerViewAdapter.ViewHolder>() {

    private var mValues: ArrayList<Ingredient> = values
    private var mIntValues: ArrayList<Int> = intValues
    private var childFragmentManager = childFragmentManager
    private var costList: ArrayList<Float> = costList

    //    private var arr: ArrayList<Boolean> = ArrayList<Boolean>(100)
//    private var arr2: ArrayList<String> = ArrayList<String>(100)
    private var context = context
    private var posTmp = -1

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
        holder.cost.setText(holder.arr2.get(position))
        //  holder.choose.setChecked(holder.arr[position]);
        // holder.cost.setText(holder.arr2[position])
        holder.choose.setOnClickListener() {


            if (holder.choose.isChecked == true && !(mIntValues.contains(position))) {
                mValues.add(item)
                mIntValues.add(position)
                holder.arr[position] = true
                posTmp = position


                if ((holder.cost.getText().toString().trim().length > 0)) {
                    costList.add(holder.cost.text.toString().toFloat())
                    holder.arr2[position] = holder.cost.text.toString()
                } else {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)

                    builder.setTitle("Adding Ingredient")
                    builder.setMessage("You must choose amount!!.")

                    builder.setPositiveButton(
                        "OK",
                        DialogInterface.OnClickListener { dialog, which -> // Do nothing but close the dialog

                            dialog.dismiss()
                        })
                    val alert: AlertDialog = builder.create()
                    alert.show()
                    holder.arr[posTmp] = false
                    mIntValues.remove(posTmp)
                    mValues.remove(item)
                    holder.choose.isChecked = false
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
        //  Log.v("Elad1","List size " + mValues.size)
//        if (mValues.size > arr.size) {
//            for (i in 0..(mValues.size-arr.size)-1) {
//                arr.add(false)
//                arr2.add("")
//            }
//        }

//        Log.v("Elad1","arr size" + arr.size)
//        Log.v("Elad1","List sie" + mValues.size.toString())
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
        val arr = Array(100, { i -> false })
        val arr2 = Array(100, { i -> "" })


        override fun toString(): String {
            return super.toString()
        }
    }


}