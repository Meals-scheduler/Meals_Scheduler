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
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.io.*


class CameraIntent(a: CameraInterface) : Fragment() {


    private var userChoosenTask: String? = null
    private val STORAGE_PERMISSION_CODE = 1
    private val CAMERA_REQUEST_CODE = 0
    private var imageUri: Uri? = null
    private val a = a

//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val x = inflater.inflate(R.layout.add_ingredients_layout, null)
//
//
//
//        return x
//    }


    companion object {
        private val IMAGE_REQUEST = 1
        lateinit var bitmap: Bitmap


    }


    fun OnUploadOrCaptureClick() {

        // need to ask for permission


        // first we check if the permission was granted.
        Log.v("Elad", "check if we have permission")
        if (ContextCompat.checkSelfPermission(
                a.getActivityy()!!,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.v("Elad", "we have permission so access")
            val items = arrayOf<CharSequence>(
                "Take Photo", "Choose from gallery", "Cancel"
            )
            val builder = AlertDialog.Builder(a.getContextt())
            builder.setTitle("Add Photo")
            builder.setItems(items) { dialogInterface, i -> // boolean result = Utility
                if (items[i] == "Take Photo") {
                    userChoosenTask = "Take Photo"
                    cameraIntent()
                } else if (items[i] == "Choose from gallery") {
                    userChoosenTask = "Choose from gallery"
                    Log.v("Elad1", "HHHHH")
                    galleryIntent()
                } else if (items[i] == "Cancel") {
                    dialogInterface.dismiss()
                }
            }
            builder.show()
        } else {
            requestStoragePermission()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v("Elad", "permission granted")
                OnUploadOrCaptureClick()
            } else {
                Toast.makeText(a.getContextt(), "Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun requestStoragePermission() {

        // if the user Deny the permission before we want to open dialog to explain why we ask permission
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                a.getActivityy()!!,
                Manifest.permission.CAMERA
            )
        ) {
            Log.v("Elad", "usser denied be4")
            AlertDialog.Builder(a.getActivityy())
                .setTitle("Permission needed")
                .setMessage("This permisiion is needed because of this and that")
                .setPositiveButton(
                    "ok"
                ) { dialogInterface, i -> //ActivityCompat.requestPermissions(FirstTimeLogin.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                    ActivityCompat.requestPermissions(
                        a.getActivityy()!!,
                        arrayOf(Manifest.permission.CAMERA),
                        STORAGE_PERMISSION_CODE
                    )
                }
                .setNegativeButton(
                    "cancel"
                ) { dialogInterface, i -> dialogInterface.dismiss() }
                .create().show()
        } else {
            // ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
            ActivityCompat.requestPermissions(
                a.getActivityy()!!,
                arrayOf(Manifest.permission.CAMERA),
                STORAGE_PERMISSION_CODE
            )
            Log.v("Elad", "user didnt denied be4")
        }
    }


    private fun galleryIntent() {
        Log.v("Elad1", "AWESOME")
        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT

        when(a){
            is MyIngredientInfo -> a.startActivityForResult(Intent.createChooser(intent,"Select File"), IMAGE_REQUEST)
            is AddIngredientFragment -> a.startActivityForResult(Intent.createChooser(intent,"Select File"), IMAGE_REQUEST)
        }




    }


    private fun cameraIntent() {
//        if(a.getActivityy()==null){
//            Log.v("Elad1","NULL")
//        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)


        when(a){
           is MyIngredientInfo -> a.startActivityForResult(intent, CAMERA_REQUEST_CODE)
            is AddIngredientFragment -> a.startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }


    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        Log.v("Elad1", "i know")
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            imageUri = data!!.data
//            if (requestCode == IMAGE_REQUEST) {
//                //onSelectFromHalleryResult(data)
//            } else if (requestCode == CAMERA_REQUEST_CODE) {
//                // onCaptureImageResult(data)
//            }
//        }
//    }
//
//
//
//
//    private fun onCaptureImageResult(data: Intent) {
//        AddIngredientFragment.bitmap = (data.extras!!["data"] as Bitmap?)!!
//        val bytes = ByteArrayOutputStream()
//        AddIngredientFragment.bitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
//        val des: File = File(
//            activity!!.filesDir,
//            System.currentTimeMillis().toString() + "jpg"
//        )
//        var fo: FileOutputStream? = null
//        try {
//            des.createNewFile()
//            fo = FileOutputStream(des)
//            fo.write(bytes.toByteArray())
//            fo.close()
//            imageUri = Uri.fromFile(des)
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        //ingredientImage.setImageBitmap(AddIngredientFragment.bitmap)
//    }
//
//    private fun onSelectFromHalleryResult(data: Intent?) {
//        if (data != null) {
//            try {
//                AddIngredientFragment.bitmap = MediaStore.Images.Media.getBitmap(
//                    context?.getContentResolver(),
//                    data.data
//                )
//            } catch (e: FileNotFoundException) {
//                e.printStackTrace()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//            // ingredientImage.setImageBitmap(AddIngredientFragment.bitmap)
//
//        }
//    }


}