package com.example.meals_schdueler

import android.content.DialogInterface
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
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

import java.net.HttpURLConnection
import java.net.URL


class DeleteAlertDialog(
    ingridentName: String,
    pictureBitMap: Bitmap?,
    ingredientID: Int,
    type: String,
    toDelete: deleteInterface,

) :
    DialogFragment(), View.OnClickListener, GetAndPost {


    lateinit var btnYes: Button
    lateinit var btnNo: Button
    lateinit var ingredientImage: ImageView
    lateinit var ingredientName: TextView
    var ingridentName = ingridentName
    var pictureBitMap = pictureBitMap
    var ingredientID = ingredientID
    var type = type


    var toDelete = toDelete

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
        if (ingredientImage != null) {
            ingredientImage.setImageBitmap(pictureBitMap)
        }
        ingredientName.setText(ingridentName)
        return rootView


    }


    override fun onClick(p0: View?) {

        if (p0 == btnYes) {
            deleteIngredient()
            toDelete.toDelete(true)

        } else {
            toDelete.toDelete(false)
            dismiss()
        }
        dismiss();
    }

    private fun deleteIngredient() {
        var s = AsynTaskNew(this, childFragmentManager)
        s.execute()
    }

    override fun DoNetWorkOpreation(): String {

        var link = ""

        when (type) {
            "Ingredient" -> link =
                "https://elad1.000webhostapp.com/delIngredient.php?ingredientID=" + ingredientID
            "Recipe" -> link =
                "https://elad1.000webhostapp.com/deleteRecipe.php?RecipeID=" + ingredientID
            "Daily" -> link = "https://elad1.000webhostapp.com/delDaily.php?DailyID=" + ingredientID
            "Weekly" -> link =
                "https://elad1.000webhostapp.com/delWeekly.php?WeeklyID=" + ingredientID
            "Monthly" -> link =
                "https://elad1.000webhostapp.com/delMonthly.php?MonthlyID=" + ingredientID
            "Yearly" -> link =
                "https://elad1.000webhostapp.com/delYearly.php?YearlyID=" + ingredientID
            "Event" -> link = "https://elad1.000webhostapp.com/delEvent.php?EventID=" + ingredientID
            "Upcoming" -> link =
                "https://elad1.000webhostapp.com/delSchedule.php?ScheduleID=" + ingredientID
        }


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


        //Log.v("Elad1", "Id came is" + sb.toString())
        return sb.toString()
    }

    override fun getData(str: String) {
        Log.v("Elad1", str)
    }

//    override fun onDismiss(dialog: DialogInterface) {
//        super.onDismiss(dialog)
//
//        val parentFragment:
//                RecyclerView = childFragmentManager
//        if (parentFragment is DialogInterface.OnDismissListener) {
//            (parentFragment as DialogInterface.OnDismissListener?)!!.onDismiss(dialog)
//        }
//
//
//    }

}



