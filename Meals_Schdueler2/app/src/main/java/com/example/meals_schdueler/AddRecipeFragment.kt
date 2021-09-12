package com.example.meals_schdueler

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
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
import androidx.recyclerview.widget.RecyclerView
import java.io.*


class AddRecipeFragment : Fragment(), View.OnClickListener, CameraInterface,
    DialogInterface.OnDismissListener {


    lateinit var recipeName: EditText
    lateinit var totalCost: EditText
    lateinit var typeOfMeal: Spinner
    lateinit var numOfPortions: Spinner
    lateinit var numOfPortionss: String
    lateinit var bitmap: Bitmap

    //lateinit var typeOfSeason: Spinner
    lateinit var nutritiousBtn: Button
    lateinit var saveBtn: Button
    lateinit var instructiosBtn: Button
    lateinit var addBtn: Button
    lateinit var shareRecipe: CheckBox
    lateinit var shareInfo: CheckBox
    lateinit var typeOfMeall: String
    private var ingredientListChoosen : ArrayList<Ingredient>? = null
    private var savedSize = 0
    private var totalCostt = 0F

    // lateinit var typeSeasson: String
    lateinit var ingredientImage: ImageView
    private var imageUri: Uri? = null

    var notritousValue: NutritousValues? = null
    var instructions: HowToStroreValue? = null
    var listItems: Recipe_Ingredients_List? = null

    var isFirstTimeNutrtious: Boolean = false
    var isFirstInstructions: Boolean = true

    private var columnCount = 1
    private val IMAGE_REQUEST = 1
    private val CAMERA_REQUEST_CODE = 0

    var ingredientList: ArrayList<Ingredient>? = null // list of ingredietns
    private var ingredientRecyclerViewAdapter: Recipe_Ingredients_RecyclerViewAdapter? =
        null // adapter for the list.

    // arrary list of int to the wrapper object Recipe_ingredients_list that contains a list of choosen items that
    // will be returned here for user choosen ingreidetnts.
    private var listItemsChoosen: ArrayList<Int>? = null
    var costList: ArrayList<Float>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listItemsChoosen = ArrayList<Int>()
        ingredientListChoosen = ArrayList()
        costList = ArrayList()
        listItems = Recipe_Ingredients_List(listItemsChoosen)
        ingredientList = ArrayList<Ingredient>()
        ingredientRecyclerViewAdapter = Recipe_Ingredients_RecyclerViewAdapter(
            this,
            //  ingredientList!!,
            // listItems,
            childFragmentManager
        )

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }


    }

    companion object {

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
        val recyclerView = x.findViewById<View>(R.id.listView2) as RecyclerView

        recipeName = x.findViewById(R.id.editTextRecipetName)
        typeOfMeal = x.findViewById(R.id.typeOfMealSpinner)
        //typeOfSeason = x.findViewById(R.id.typeOfSeassonSpinner)
        nutritiousBtn = x.findViewById(R.id.nutriousValueBtn)
        instructiosBtn = x.findViewById(R.id.instructionsBtn)
        saveBtn = x.findViewById(R.id.buttonSave)
        addBtn = x.findViewById(R.id.addIngredientBtn)
        shareInfo = x.findViewById(R.id.checkBoxShareInfo)
        shareRecipe = x.findViewById(R.id.checkBoxShareRecipe)
        ingredientImage = x.findViewById(R.id.imageViewPic)
        totalCost = x.findViewById(R.id.editTextTotalCost)
        numOfPortions = x.findViewById(R.id.numOfPortions)
        //listView = x!!.findViewById<View>(R.id.listView) as ListView


        recyclerView.adapter = ingredientRecyclerViewAdapter

        notritousValue = NutritousValues("", 0F, 0F, 0F)
        instructions = HowToStroreValue("")
        //ingredientImageInit = initImage(ingredientImage)


        //  Log.v("Elad1", "USER IDDDDDDDD222" + UserInterFace.userID.toString())

        addBtn.setOnClickListener(this)
        saveBtn.setOnClickListener(this)
        ingredientImage.setOnClickListener(this)
        instructiosBtn.setOnClickListener(this)
        nutritiousBtn.setOnClickListener(this)
        typeOfMeal.onItemSelectedListener = SpinnerActivity()
        numOfPortions.onItemSelectedListener = SpinnerActivity()
        //typeOfSeason.onItemSelectedListener = SpinnerActivity()


        return x
    }


    inner class SpinnerActivity() : Activity(), AdapterView.OnItemSelectedListener {


        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)


            if (parent == typeOfMeal) {
                typeOfMeall = parent.getItemAtPosition(pos).toString()

            } else if (parent == numOfPortions) {

                numOfPortionss = parent.getItemAtPosition(pos).toString()
            }
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
        } else if (p0 == ingredientImage) {
            val c = CameraIntent(this)
            c.OnUploadOrCaptureClick()
        } else if (p0 == addBtn) {
           // listItems!!.list!!.clear()
            ingredientList!!.clear()
            var dialog = Recipe_Ingredients_Choose_Dialog(
                listItems!!,
                ingredientList!!,
                costList!!
            )
            dialog.show(childFragmentManager, "Recipe_Ingredietns_Choose")


        } else if (p0 == saveBtn) {
            var recipe = Recipe(
                1,
                UserInterFace.userID,
                recipeName.text.toString(),
                bitmap,
                typeOfMeall,
                numOfPortionss,
                totalCost.text.toString().toDouble(),
                shareRecipe.isChecked,
                shareInfo.isChecked,
                ingredientListChoosen!!,
                costList!!,

                )

            var s = AsynTaskNew(recipe, childFragmentManager)
            s.execute()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            imageUri = data!!.data
            if (requestCode == IMAGE_REQUEST) {
                onSelectFromHalleryResult(data)
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                onCaptureImageResult(data)
            }
        }
    }

    override fun onCaptureImageResult(data: Intent) {
        bitmap = (data.extras!!["data"] as Bitmap?)!!
        val bytes = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
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
        ingredientImage.setImageBitmap(bitmap)
    }

    override fun onSelectFromHalleryResult(data: Intent?) {
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                    context?.getContentResolver(),
                    data.data
                )
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            ingredientImage.setImageBitmap(bitmap)

        }
    }

    override fun getActivityy(): Activity? {
        return activity
    }

    override fun getContextt(): Context? {
        return context
    }

    override fun onDismiss(p0: DialogInterface?) {
        // on dissmiss event , when we dissmiss the ingrdeitns selcection dialog we want to update the list with
        // the chosen ingredients.


        var j = 0
        for (i in listItems!!.list!!) {
            if (j >= savedSize) {
                var ing = ingredientList!!.get(i)
                if (!ingredientListChoosen!!.contains(ing))
                    ingredientListChoosen!!.add(
                        ing!!

                    )
            }
            j++
        }

        ingredientRecyclerViewAdapter!!.setmValues(ingredientListChoosen!!)
       // if(ingredientListChoosen!!.size >= costList!!.size){
            calculateCost()
            calculateNutritiousValues()

      //  }


    }

    public fun calculateNutritiousValues() {
        var j = 0
        // make them 0 because when we delete we dont want to still add to whats exsits.
        notritousValue!!.carbs = 0f
        notritousValue!!.protein = 0F
        notritousValue!!.fats = 0F

        for (i in ingredientListChoosen!!) {


            var cur = costList!!.get(j) * i.carbs_.toFloat() / 100
           // Log.v("Elad1","Carbs " + cur)
            notritousValue!!.carbs += cur
           // Log.v("Elad1"," total Carbs " +     notritousValue!!.carbs)
            cur = costList!!.get(j) * i.protein_.toFloat() / 100
          //  Log.v("Elad1","Protein " + cur)
            notritousValue!!.protein += cur
          //  Log.v("Elad1"," total protein " +     notritousValue!!.protein)
            cur = costList!!.get(j) * i.fat.toFloat() / 100
           // Log.v("Elad1","Fats " + cur)
            notritousValue!!.fats += cur
           // Log.v("Elad1"," total fats " +     notritousValue!!.fats)
            j++
        }
        savedSize = ingredientListChoosen!!.size

    }


    public fun calculateCost() {
        var calc: Float = 0f
        var j = 0

        for (i in ingredientListChoosen!!) {


            var cur = costList!!.get(j) * i.costPerGram.toFloat() / 100

            calc += cur
            j++
        }


        totalCost.setText(calc.toString())
    }


}



