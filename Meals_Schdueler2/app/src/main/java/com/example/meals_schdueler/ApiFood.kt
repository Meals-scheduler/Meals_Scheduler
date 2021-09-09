package com.example.meals_schdueler

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ApiFood : AppCompatActivity(), GetAndPost {
    private var fcid = 1001611

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.api_test)



       // for (i in 1..5){
            startTask()
        //}




    }

    fun startTask() {
        var s = AsynTaskNew(this, supportFragmentManager)
        s.execute()
    }


    override fun DoNetWorkOpreation(): String {

        Log.v("Elad1","FCID is " + fcid)
        var link = "https://api.nal.usda.gov/fdc/v1/food/"+fcid+"?api_key=OdFMvbIZOgVXGfpOpVETK2crxi8k0L0cQf5LHIyP"



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


        fcid+=2
        return sb.toString()
    }

    override fun getData(str: String) {
      // Log.v("Elad1", str)
        val jsonObject = JSONObject(str)
        val name = jsonObject.getString("description")
        var ingredient = jsonObject.getString("ingredients")
        var foodNutrients = jsonObject.getString("foodNutrients")

        val arr = JSONArray(foodNutrients)


        // now we want to get the texts as they are in JSON.
        for (i in 0 until arr.length()) {
            val jsonSecondary = arr.getJSONObject(i)
            var id = ""
            var description = ""
            var num = jsonSecondary.getString("nutrient")
//            description = jsonSecondary.getString("description")
            Log.v("Elad1","nutrient " + num)

            var myObj = JSONObject(num)
            var name2 = myObj.getString("name")
            Log.v("Elad1","NAME " + name2)

        }

        Log.v("Elad1","Name of the recipe " + name)
        Log.v("Elad1","Ingredients " + ingredient)

    }
}