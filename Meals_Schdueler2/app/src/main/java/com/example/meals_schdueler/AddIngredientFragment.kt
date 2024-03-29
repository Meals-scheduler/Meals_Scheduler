package com.example.meals_schdueler

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import java.io.*


open class AddIngredientFragment : Fragment(), View.OnClickListener, CameraInterface {


    lateinit var ingredientName: EditText
    lateinit var costPerGram: EditText
    lateinit var typeOfMeal: Spinner
    lateinit var typeOfSeason: Spinner
    lateinit var nutritiousBtn: Button
    lateinit var buttonApi: Button
    lateinit var saveBtn: Button
    lateinit var howToStoreBtn: Button
    lateinit var shareIngredient: CheckBox
    lateinit var shareInfo: CheckBox
    lateinit var typeOfMeall: String
    lateinit var typeSeasson: String
    lateinit var ingredientImage: ImageView
    var bitmap: Bitmap? = null
    private var imageUri: Uri? = null
    var notritousValue: NutritousValues? = null
    var howToStoreValue: HowToStroreValue? = null


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
        buttonApi = x.findViewById(R.id.buttonApi)

        notritousValue = NutritousValues("", 0F, 0F, 0F)
        howToStoreValue = HowToStroreValue("")
        //ingredientImageInit = initImage(ingredientImage)
        buttonApi.setOnClickListener(this)





        saveBtn.setOnClickListener(this)
        ingredientImage.setOnClickListener(this)
        howToStoreBtn.setOnClickListener(this)
        nutritiousBtn.setOnClickListener(this)
        typeOfMeal.onItemSelectedListener = SpinnerActivity()
        typeOfSeason.onItemSelectedListener = SpinnerActivity()

        if (UserInterFace.userID != -1) {
            buttonApi.visibility = View.INVISIBLE
        }

        return x
    }


    inner class SpinnerActivity() : Activity(), AdapterView.OnItemSelectedListener {


        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)


            if (parent == typeOfMeal) {
                typeOfMeall = parent.getItemAtPosition(pos).toString()

            } else if (parent == typeOfSeason) {
                typeSeasson = parent.getItemAtPosition(pos).toString()

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

            if (bitmap == null) {
                setDefaultPic()
            }
            var ingredient = Ingredient(

                1,
                UserInterFace.userID,
                ingredientName.getText().toString(),
                bitmap!!,
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
            var s = AsynTaskNew(ingredient, childFragmentManager, requireContext())
            s.execute()
            //pbDialog.dismiss()

            MyingredientFragment1.getInstance1().getRecycler().toCopy(ingredient)

            if (MyingredientFragment1.instance!!.noIngredientsTextView.visibility == View.VISIBLE) {
                MyingredientFragment1.instance!!.noIngredientsTextView.visibility = View.INVISIBLE
            }

            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Added successfully!")
            builder.setPositiveButton(
                "Got it!"
            ) { dialog, id -> dialog.cancel() }.show()


        } else if (p0 == ingredientImage) {
            val c = CameraIntent(this)
            c.OnUploadOrCaptureClick()


        } else if (p0 == buttonApi) {
//            val i = Intent(
//                context,
//                ApiFood::class.java
//            )
//            startActivity(i)

            var arr: ArrayList<Int> = ArrayList()
            var id = 46883 // in 55026 we hvae problem with quntities
            for (i in 1..2) {
                arr.add(id)
                id += 1
            }

            for (i in arr) {
                var g = ApiFood(i, childFragmentManager, requireContext());
                g.startTask()
            }
        }
    }

    private fun setDefaultPic() {
        bitmap = BitmapFactory.decodeResource(
            requireContext().resources,
            R.drawable.question_mark
        )
    }
    // for camera


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


}


