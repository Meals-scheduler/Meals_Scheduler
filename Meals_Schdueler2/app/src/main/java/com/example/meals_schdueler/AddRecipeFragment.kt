package com.example.meals_schdueler

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.*
import java.util.*

class AddRecipeFragment : Fragment(), View.OnClickListener, CameraInterface {


    lateinit var recipeName: EditText
    lateinit var totalCost: EditText
    lateinit var typeOfMeal: Spinner

    //lateinit var typeOfSeason: Spinner
    lateinit var nutritiousBtn: Button
    lateinit var saveBtn: Button
    lateinit var instructiosBtn: Button
    lateinit var addBtn: Button
    lateinit var deleteBtn: Button
    lateinit var shareIngredient: CheckBox
    lateinit var shareInfo: CheckBox
    lateinit var typeOfMeall: String

    // lateinit var typeSeasson: String
    lateinit var ingredientImage: ImageView
    private var imageUri: Uri? = null
    var notritousValue: NutritousValues? = null
    var instructions: HowToStroreValue? = null

    var isFirstTimeNutrtious: Boolean = true
    var isFirstInstructions: Boolean = true

    private var columnCount = 1
    private var ingredientList: ArrayList<Ingredient>? = null // list of ingredietns
    private var ingredientRecyclerViewAdapter: Recipe_Ingredients_RecyclerViewAdapter? =
        null // adapter for the list.

    var listView: ListView? = null
    private var adapter: ArrayAdapter<Ingredient>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ingredientList = ArrayList<Ingredient>()
        ingredientRecyclerViewAdapter = Recipe_Ingredients_RecyclerViewAdapter(
            ingredientList!!,
            childFragmentManager
        )
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    companion object {

//        var instance : MyingredientFragment1? = null

//        fun getInstance1() : MyingredientFragment1{
//            return instance!!
//        }
        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            MyingredientFragment1().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)

                }
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val x = inflater.inflate(R.layout.add_recipe_layout, null)
        val recyclerView = x.findViewById<View>(R.id.listView) as RecyclerView

        recipeName = x.findViewById(R.id.editTextRecipetName)
        typeOfMeal = x.findViewById(R.id.typeOfMealSpinner)
        //typeOfSeason = x.findViewById(R.id.typeOfSeassonSpinner)
        nutritiousBtn = x.findViewById(R.id.nutriousValueBtn)
        instructiosBtn = x.findViewById(R.id.instructionsBtn)
        saveBtn = x.findViewById(R.id.buttonSave)
        addBtn = x.findViewById(R.id.addIngredientBtn)
        deleteBtn = x.findViewById(R.id.deleteIngredientBtn)
        shareInfo = x.findViewById(R.id.checkBoxShareInfo)
        shareIngredient = x.findViewById(R.id.checkBoxShareIngredient)
        ingredientImage = x.findViewById(R.id.imageViewPic)
        totalCost = x.findViewById(R.id.editTextTotalCost)
        //listView = x!!.findViewById<View>(R.id.listView) as ListView


        recyclerView.adapter = ingredientRecyclerViewAdapter

        notritousValue = NutritousValues("", 0F, 0F, 0F)
        instructions = HowToStroreValue("")
        //ingredientImageInit = initImage(ingredientImage)


        Log.v("Elad1", "USER IDDDDDDDD222" + UserInterFace.userID.toString())

        addBtn.setOnClickListener(this)
        deleteBtn.setOnClickListener(this)
        saveBtn.setOnClickListener(this)
        ingredientImage.setOnClickListener(this)
        instructiosBtn.setOnClickListener(this)
        nutritiousBtn.setOnClickListener(this)
        typeOfMeal.onItemSelectedListener = SpinnerActivity()
        //typeOfSeason.onItemSelectedListener = SpinnerActivity()



        return x
    }


    inner class SpinnerActivity() : Activity(), AdapterView.OnItemSelectedListener {


        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)


            if (parent == typeOfMeal) {
                typeOfMeall = parent.getItemAtPosition(pos).toString()
                Log.v("Elad", "$typeOfMeall")
            }
//            } else if (parent == typeOfSeason) {
//                typeSeasson = parent.getItemAtPosition(pos).toString()
//                Log.v("Elad", "$typeSeasson")
//            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            // Another interface callback


        }
    }

    override fun onClick(p0: View?) {
        if (p0 == nutritiousBtn) {
            var dialog = NutritiousDialog(
                notritousValue!!,
                isFirstTimeNutrtious
            )
            isFirstTimeNutrtious = false
            dialog.show(childFragmentManager, "NutritiousDialog")

        } else if (p0 == instructiosBtn) {
            var dialog = HowToStoreDialog(instructions!!, isFirstInstructions)
            isFirstInstructions = false
            dialog.show(childFragmentManager, "HowToStoreDialog")
        }
        else if(p0 == ingredientImage){
            val c = CameraIntent(this)
            c.OnUploadOrCaptureClick()
        }
        else if(p0== addBtn){
            Log.v("Elad1","ADDBTN")

            ingredientList!!.add(UserPropertiesSingelton.getInstance()!!.getUserIngredientss()!!.get(0))
            Log.v("Elad1", ingredientList!!.count().toString())
            Log.v("Elad1", ingredientList!!.get(0).ingridentName.toString())
            ingredientRecyclerViewAdapter!!.setmValues(ingredientList!!)
        }
    }

    override fun onCaptureImageResult(data: Intent) {
        AddIngredientFragment.bitmap = (data.extras!!["data"] as Bitmap?)!!
        val bytes = ByteArrayOutputStream()
        AddIngredientFragment.bitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val des: File = File(
            requireContext().filesDir,
            System.currentTimeMillis().toString() + "jpg"
        )
        var fo: FileOutputStream? = null
        try {
            des.createNewFile()
            fo = FileOutputStream(des)
            fo.write(bytes.toByteArray())
            fo.close()
            imageUri = Uri.fromFile(des)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        ingredientImage.setImageBitmap(AddIngredientFragment.bitmap)
    }

    override fun onSelectFromHalleryResult(data: Intent?) {
        if (data != null) {
            try {
                AddIngredientFragment.bitmap = MediaStore.Images.Media.getBitmap(
                    context?.getContentResolver(),
                    data.data
                )
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            ingredientImage.setImageBitmap(AddIngredientFragment.bitmap)

        }
    }

    override fun getActivityy(): Activity? {
       return activity
    }

    override fun getContextt(): Context? {
        return context
    }


}