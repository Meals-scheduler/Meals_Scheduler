package com.example.meals_schdueler

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.meals_schdueler.AddIngredientFragment.Companion.bitmap
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MyIngredientFragment: Fragment(), GetAndPost {

    lateinit var img: ImageView
    lateinit var str: String



    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val x = inflater.inflate(R.layout.my_ingredients_layout, null)
        img = x.findViewById(R.id.imageViewStam)

        var s = AsynTaskNew(this)
        s.execute()

        return x
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun DoNetWorkOpreation(): String {
        val link = "https://elad1.000webhostapp.com/getPic.php"


        val sb = StringBuilder()

        val url = URL(link)
        val urlConnection = url.openConnection() as HttpURLConnection
        try {
            val `in`: InputStream = BufferedInputStream(urlConnection.inputStream)
            val bin = BufferedReader(InputStreamReader(`in`))
            // temporary string to hold each line read from the reader.
            var inputLine: String?

            while (bin.readLine().also { inputLine = it } != null) {
                sb.append(inputLine)

            }
        } finally {
            // regardless of success or failure, we will disconnect from the URLConnection.
            urlConnection.disconnect()
        }



        return sb.toString()


    }

    override fun getData(str: String) {
        Log.v("Elad1", "the string is here and ::: " + str)

//        var sb = StringBuilder(str).also { it.setCharAt(39228, '=') }
//        var s = sb.toString()
        //Log.v("Elad1", "NOW::: " + str[39227])
        Log.v("Elad1", "NOW::: " + str.length)
        var bitmap2 = ImageConvert.StringToBitMap(str)

        img.setImageBitmap(bitmap2)

    }


}






