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


    companion object {
        private val IMAGE_REQUEST = 1
        lateinit var bitmap: Bitmap


    }


    fun OnUploadOrCaptureClick() {

        // need to ask for permission


        // first we check if the permission was granted.
        if (ContextCompat.checkSelfPermission(
                a.getActivityy()!!,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {

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

        }
    }


    private fun galleryIntent() {

        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT

        when(a){
            is MyIngredientInfo -> a.startActivityForResult(Intent.createChooser(intent,"Select File"), IMAGE_REQUEST)
            is AddIngredientFragment -> a.startActivityForResult(Intent.createChooser(intent,"Select File"), IMAGE_REQUEST)
            is AddRecipeFragment -> a.startActivityForResult(Intent.createChooser(intent,"Select File"), IMAGE_REQUEST)

        }




    }


    private fun cameraIntent() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)


        when(a){
           is MyIngredientInfo -> a.startActivityForResult(intent, CAMERA_REQUEST_CODE)
            is AddIngredientFragment -> a.startActivityForResult(intent, CAMERA_REQUEST_CODE)
            is AddRecipeFragment -> a.startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }


    }


}