package com.example.meals_schdueler

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.io.*

open class AddIngredientFragment : Fragment(), View.OnClickListener , CameraInterface{


    lateinit var ingredientName: EditText
    lateinit var costPerGram: EditText
    lateinit var typeOfMeal: Spinner
    lateinit var typeOfSeason: Spinner
    lateinit var nutritiousBtn: Button
    lateinit var saveBtn: Button
    lateinit var howToStoreBtn: Button
    lateinit var shareIngredient: CheckBox
    lateinit var shareInfo: CheckBox
    lateinit var typeOfMeall: String
    lateinit var typeSeasson: String
    lateinit var ingredientImage: ImageView
    lateinit var bitmap: Bitmap
    private var imageUri: Uri? = null
    var notritousValue: NutritousValues? = null
    var howToStoreValue: HowToStroreValue? = null
   // var ingredientImageInit : initImage? = null


    //for camera intent
//    private var userChoosenTask: String? = null
//    private val STORAGE_PERMISSION_CODE = 1
//    private val REQUEST_CODE = 1

    companion object {
        private val IMAGE_REQUEST = 1

        lateinit var pbDialog: ProgressBarDialog


    }

    private val CAMERA_REQUEST_CODE = 0

    var isFirstTimeNutrtious: Boolean = true
    var isFirstTimeHowToStore: Boolean = true
    var howToStoreDes: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val x = inflater.inflate(R.layout.add_ingredients_layout, null)
        ingredientName = x.findViewById(R.id.editTextIngredientName)
        typeOfMeal = x.findViewById(R.id.typeOfMealSpinner)
        typeOfSeason = x.findViewById(R.id.typeOfSeassonSpinner)
        nutritiousBtn = x.findViewById(R.id.buttonNutritious)
        howToStoreBtn = x.findViewById(R.id.howToStoreBtn)
        saveBtn = x.findViewById(R.id.buttonSave)
        shareInfo = x.findViewById(R.id.checkBoxShareInfo)
        shareIngredient = x.findViewById(R.id.checkBoxShareIngredient)
        ingredientImage = x.findViewById(R.id.imageViewPic)
        costPerGram = x.findViewById(R.id.editTextCost)

        notritousValue = NutritousValues("", 0F, 0F, 0F)
        howToStoreValue =  HowToStroreValue("")
       //ingredientImageInit = initImage(ingredientImage)




        Log.v("Elad1","USER IDDDDDDDD" + UserInterFace.userID.toString())


        saveBtn.setOnClickListener(this)
        ingredientImage.setOnClickListener(this)
        howToStoreBtn.setOnClickListener(this)
        nutritiousBtn.setOnClickListener(this)
        typeOfMeal.onItemSelectedListener = SpinnerActivity()
        typeOfSeason.onItemSelectedListener = SpinnerActivity()



        return x
    }


    inner class SpinnerActivity() : Activity(), AdapterView.OnItemSelectedListener {


        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)


            if (parent == typeOfMeal) {
                typeOfMeall = parent.getItemAtPosition(pos).toString()
                Log.v("Elad", "$typeOfMeall")
            } else if (parent == typeOfSeason) {
                typeSeasson = parent.getItemAtPosition(pos).toString()
                Log.v("Elad", "$typeSeasson")
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

        } else if (p0 == howToStoreBtn) {
            var dialog = HowToStoreDialog(howToStoreValue!!, isFirstTimeHowToStore)
            isFirstTimeHowToStore = false
            dialog.show(childFragmentManager, "HowToStoreDialog")
        } else if (p0 == saveBtn) {
            Log.v("Elad1", "click save")

            var ingredient = Ingredient(

                1,
                UserInterFace.userID,
                ingredientName.getText().toString(),
                bitmap,
                typeOfMeall,
                typeSeasson,
                howToStoreValue!!.howToStore,
                shareIngredient.isChecked,
                shareInfo.isChecked,
                notritousValue!!.protein,
                notritousValue!!.carbs,
                notritousValue!!.fats,
                notritousValue!!.des,
                costPerGram.getText().toString(),
                false


            ) // owenerId will be changed and will be determined from the user Table in the future.
//            pbDialog = ProgressBarDialog()
//            pbDialog.show(childFragmentManager, "ProgressBarDialog")
            var s = AsynTaskNew(ingredient,childFragmentManager)
            s.execute()
            //pbDialog.dismiss()


        } else if (p0 == ingredientImage) {
            val c = CameraIntent(this)
            c.OnUploadOrCaptureClick()


        }
    }
    // for camera


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


}


