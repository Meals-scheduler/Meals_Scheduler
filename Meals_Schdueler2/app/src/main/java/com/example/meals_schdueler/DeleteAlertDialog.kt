package com.example.meals_schdueler

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class DeleteAlertDialog(ingridentName: String, pictureBitMap: Bitmap, ingredientID: Int) :
    DialogFragment(), View.OnClickListener, GetAndPost {


    lateinit var btnYes: Button
    lateinit var btnNo: Button
    lateinit var ingredientImage: ImageView
    lateinit var ingredientName: TextView
    var ingridentName = ingridentName
    var pictureBitMap = pictureBitMap
    var ingredientID = ingredientID

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.alert_dialog, container, false)

        btnNo = rootView.findViewById(R.id.btn_no)
        btnYes = rootView.findViewById(R.id.btn_yes)
        ingredientImage = rootView.findViewById(R.id.imageViewIng)
        ingredientName = rootView.findViewById(R.id.ingName)
        btnYes.setOnClickListener(this)
        btnNo.setOnClickListener(this)
        ingredientImage.setImageBitmap(pictureBitMap)
        ingredientName.setText(ingridentName)
        return rootView


    }


    override fun onClick(p0: View?) {
        when (p0) {
            btnYes -> deleteIngredient()
            btnNo -> dismiss()

        }
        dismiss();
    }

    private fun deleteIngredient() {
        var s = AsynTaskNew(this, childFragmentManager)
        s.execute()
    }

    override fun DoNetWorkOpreation(): String {
        val link = "https://elad1.000webhostapp.com/delIngredient.php?ingredientID=" + ingredientID
        Log.v("Elad1", "here")

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


        Log.v("Elad1", "Id came is" + sb.toString())
        return sb.toString()
    }

    override fun getData(str: String) {
        Log.v("Elad1", str)
    }

}



