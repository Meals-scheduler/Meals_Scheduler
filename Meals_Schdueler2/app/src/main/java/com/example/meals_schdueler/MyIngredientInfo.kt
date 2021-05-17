package com.example.meals_schdueler

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import java.io.*


class MyIngredientInfo(item: Ingredient, isRecipeList: Boolean) : DialogFragment(), View.OnClickListener, CameraInterface {

    lateinit var ingredientName: EditText
    lateinit var costPerGram: EditText
    lateinit var typeOfMeal: Spinner
    lateinit var typeOfSeason: Spinner
    lateinit var nutritiousBtn: Button
    lateinit var editBtn: Button
    lateinit var saveBtn: Button
    lateinit var bitmap: Bitmap
    lateinit var howToStoreBtn: Button
    lateinit var shareIngredient: CheckBox
    lateinit var shareInfo: CheckBox
    lateinit var typeOfMeall: String
    lateinit var typeSeasson: String
    lateinit var picture: String
    lateinit var ingredientImage: ImageView
    lateinit var imageX : ImageView
    var nutritousValues: NutritousValues? = null
    var howToStoreValue: HowToStroreValue? = null
    private var imageUri: Uri? = null
    private val IMAGE_REQUEST = 1
    private val CAMERA_REQUEST_CODE = 0
    private var isRecipeList : Boolean = isRecipeList

    var ingredient = item
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var x: View = inflater.inflate(R.layout.my_ingredeint_info, container, false)
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
        imageX = x.findViewById(R.id.imageViewX)
        nutritousValues = NutritousValues(
            ingredient.nutritiousDes,
            ingredient.fat,
            ingredient.carbs_,
            ingredient.protein_
        )
        howToStoreValue = HowToStroreValue(ingredient.howToStore)


        ingredientImage.setOnClickListener(this)
        saveBtn.setOnClickListener(this)
        editBtn.setOnClickListener(this)
        nutritiousBtn.setOnClickListener(this)
        howToStoreBtn.setOnClickListener(this)
        imageX.setOnClickListener(this)
        typeOfMeal.onItemSelectedListener = SpinnerActivity()
        typeOfSeason.onItemSelectedListener = SpinnerActivity()
        setIngredientData()

        // not to allow to edit and save from the recipe list ingredients.
        if(isRecipeList){
            editBtn.isClickable=false
            saveBtn.isClickable=false

        }

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
        ingredientImage.isEnabled =false


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
            val c = CameraIntent(this)
            c.OnUploadOrCaptureClick()

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
            var s = AsynTaskNew(ingredient1, childFragmentManager)
            s.execute()



        }
        else if(p0 == imageX){
            dismiss()
        }
    }

    private fun unlockEdit() {
        costPerGram.isEnabled = true
        ingredientName.isEnabled = true
        shareIngredient.isClickable = true
        shareInfo.isClickable = true
        typeOfSeason!!.isEnabled = true
        typeOfMeal.isEnabled = true
        ingredientImage.isEnabled = true


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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.v("Elad1","FINALLY")
        if (resultCode == Activity.RESULT_OK) {
            imageUri = data!!.data
            if (requestCode == IMAGE_REQUEST) {
                onSelectFromHalleryResult(data)
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                onCaptureImageResult(data)
            }
        }
    }

    override fun getActivityy(): Activity? {
       return activity
    }

    override fun getContextt(): Context? {
        return context
    }

}