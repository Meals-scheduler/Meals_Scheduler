package com.example.meals_schdueler

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.util.*

class ImageConvert(imgBitMap: Bitmap) {

//A bitmap is an object , which is an instance of the Bitmap Class. This class represents a 2d coordinate system .
// The coordinate system move to the right on the x axis , and to the bottom on the y axis
//Each point in the coordinate system is called a pixel . A pixel is formed of bits , and bits represent the color of this pixel.
// A pixel can be formed of 8 , or 16 or 24 bits … The x axis represents the width , and the y axis represent the height .


    var imgBitMap: Bitmap = imgBitMap


    // convert bitmap into string to save in DBq
    @RequiresApi(Build.VERSION_CODES.O)
    fun BitMapToString(): String? {
        val baos = ByteArrayOutputStream()
        imgBitMap.compress(Bitmap.CompressFormat.JPEG, 10, baos)
        val b: ByteArray = baos.toByteArray()
        return Base64.getEncoder().encodeToString(b)
    }

    //convert the saved String in DB to the bitmap to display in app.

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun StringToBitMap(str: String): Bitmap? {

            val imageBytes = Base64.getDecoder().decode(str)
            val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            return image
        }
    }


}